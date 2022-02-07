package com.ruslan.hlushan.core.impl

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.ruslan.hlushan.android.storage.SharedPrefsProvider
import com.ruslan.hlushan.core.di.InjectorHolder
import com.ruslan.hlushan.core.impl.tools.initTools
import com.ruslan.hlushan.core.impl.utils.SemEmergencyManagerLeakingActivity
import com.ruslan.hlushan.core.impl.utils.exceptions.RxErrorHandlingUtil
import com.ruslan.hlushan.core.impl.utils.getWrappedOrUpdateContext
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.manager.api.Settings
import com.ruslan.hlushan.core.thread.UiMainThread
import javax.inject.Inject

private const val APP_INITIALIZATION_TAG: String = "APP_INITIALIZATION"

abstract class BaseApplication : Application(), InjectorHolder {

    @get:SuppressWarnings("VariableNaming")
    protected abstract val APP_TAG: String

    protected abstract val appLogger: AppLogger

    @Inject protected lateinit var settings: Settings

    @UiMainThread
    protected abstract fun initCrashlytics()

    @UiMainThread
    protected abstract fun initDagger2AndInject()

    @UiMainThread
    protected abstract fun areAppLoggersInitialized(): Boolean

    override fun onCreate() {
        val timestampBeforeSuperOnCreate = System.currentTimeMillis()

        super.onCreate()

        val timestampAfterSuperOnCreate = System.currentTimeMillis()

        initCrashlytics()

        val timestampAfterInitCrashlytics = System.currentTimeMillis()

        SharedPrefsProvider.init()

        val timestampAfterSharedPrefsProviderInit = System.currentTimeMillis()

        /**
         * Init crashlytics first of all, to not miss any startup crash;
         *
         * Tink used inside [SharedPrefsProvider] should be also initialized before,
         * as it used in loggers for persistent storage;
         */
        require(!areAppLoggersInitialized()) {
            "App Loggers should not be initialized here"
        }
        logMessage("After Loggers init")
        val onCreateTimestampsMessage = "onCreate timestamps: " +
                                        "BeforeSuperOnCreate = $timestampBeforeSuperOnCreate, " +
                                        "AfterSuperOnCreate = $timestampAfterSuperOnCreate, " +
                                        "AfterInitCrashlytics = $timestampAfterInitCrashlytics, " +
                                        "AfterSharedPrefsProviderInit = $timestampAfterSharedPrefsProviderInit"
        logMessage(onCreateTimestampsMessage)

        SemEmergencyManagerLeakingActivity.applyFix(this)
        logMessage("after SemEmergencyManagerLeakingActivity.applyFix()")
        initDagger2AndInject()
        logMessage("after initDagger2AndInject()")
        initSecond()
    }

    @UiMainThread
    private fun initSecond() {
        logMessage("initSecond()")
        setAppErrorHandling()
        logMessage("after setAppErrorHandling()")
        initBaseContextLang()
        logMessage("after initBaseContextLang()")
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        logMessage("after AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)")
        initTools(this, appLogger)
    }

    @UiMainThread
    private fun initBaseContextLang() {
        getWrappedOrUpdateContext(baseContext, settings.appLanguageFullCode.nonFullCode)
    }

    private fun setAppErrorHandling() {
        RxErrorHandlingUtil.setRxErrorHandling(appLogger)
        // TODO: tests needed
        //AppExceptionHandler.setUp(this)
    }

    private fun logMessage(message: String) =
            appLogger.log("$APP_TAG -> $APP_INITIALIZATION_TAG -> $message")
}