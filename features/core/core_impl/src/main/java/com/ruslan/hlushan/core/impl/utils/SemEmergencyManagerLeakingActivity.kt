package com.ruslan.hlushan.core.impl.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle

//https://gist.github.com//jankovd/a210460b814c04d500eb12025902d60d

internal class SemEmergencyManagerLeakingActivity
private constructor(private val application: Application) : Application.ActivityLifecycleCallbacks {

    @SuppressWarnings("ClassOrdering")
    companion object {
        fun applyFix(application: Application) {
            @SuppressLint("ObsoleteSdkInt")
            if ((Build.MANUFACTURER == "samsung")
                && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                && (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N)) {
                application.registerActivityLifecycleCallbacks(SemEmergencyManagerLeakingActivity(application))
            }
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        try {
            swapActivityWithApplicationContext()
        } catch (ignored: Exception) {
            // the same result is expected on subsequent tries.
        }

        application.unregisterActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    @Throws(ClassNotFoundException::class, NoSuchFieldException::class, IllegalAccessException::class)
    private fun swapActivityWithApplicationContext() {
        val semEmergencyManagerClass = Class.forName("com.samsung.android.emergencymode.SemEmergencyManager")
        val sInstanceField = semEmergencyManagerClass.getDeclaredField("sInstance")
        sInstanceField.isAccessible = true
        val sInstance = sInstanceField.get(null)
        val mContextField = semEmergencyManagerClass.getDeclaredField("mContext")
        mContextField.isAccessible = true
        mContextField.set(sInstance, application)
    }
}