package com.ruslan.hlushan.core.impl.exceptions

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Process

private const val RESTARTED = "appExceptionHandler_restarted"
private const val LAST_EXCEPTION = "appExceptionHandler_lastException"

internal class AppExceptionHandler(
        private val systemHandler: Thread.UncaughtExceptionHandler,
        private val crashlyticsHandler: Thread.UncaughtExceptionHandler,
        private val application: Application
) : Thread.UncaughtExceptionHandler {

    private var lastStartedActivity: Activity? = null
    private var startCount = 0

    init {
        init()
    }

    private fun init() {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

            override fun onActivityStarted(activity: Activity) {
                startCount++
                lastStartedActivity = activity
            }

            override fun onActivityResumed(activity: Activity) = Unit

            override fun onActivityPaused(activity: Activity) = Unit

            override fun onActivityStopped(activity: Activity) {
                startCount--
                if (startCount <= 0) {
                    lastStartedActivity = null
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

            override fun onActivityDestroyed(activity: Activity) = Unit
        })
    }

    override fun uncaughtException(t: Thread, e: Throwable) {

        if (lastStartedActivity != null) {
            val isRestarted = lastStartedActivity?.intent?.getBooleanExtra(RESTARTED, false)
                              ?: false
            val lastException = lastStartedActivity?.intent?.getSerializableExtra(LAST_EXCEPTION) as? Throwable

            if (!isRestarted || !isSameException(e, lastException)) {
                killThisProcess {
                    crashlyticsHandler.uncaughtException(t, e)

                    val intent = lastStartedActivity?.intent
                            ?.putExtra(RESTARTED, true)
                            ?.putExtra(LAST_EXCEPTION, e)
                            ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                    lastStartedActivity?.finish()
                    lastStartedActivity?.startActivity(intent)
                }
            } else {
                killThisProcess { systemHandler.uncaughtException(t, e) }
            }
        } else {
            killThisProcess {
                crashlyticsHandler.uncaughtException(t, e)
                systemHandler.uncaughtException(t, e)
            }
        }
    }

    private fun isSameException(originalException: Throwable?, lastException: Throwable?): Boolean =
            (lastException != null && originalException != null
             && originalException.javaClass == lastException.javaClass
             && originalException.stackTrace[0] == originalException.stackTrace[0]
             && (originalException.message.isNullOrEmpty() && lastException.message.isNullOrEmpty()
                 || originalException.message == lastException.message))

    private fun killThisProcess(runnable: () -> Unit) {
        runnable()
        Process.killProcess(Process.myPid())
        @SuppressWarnings("MagicNumber")
        val exitStatus = 10
        System.exit(exitStatus)
    }

    companion object {
        fun setUp(application: Application) {
            val systemHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { t, e -> }
            val crashlyticsExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler(
                    AppExceptionHandler(systemHandler, crashlyticsExceptionHandler, application)
            )
        }
    }
}