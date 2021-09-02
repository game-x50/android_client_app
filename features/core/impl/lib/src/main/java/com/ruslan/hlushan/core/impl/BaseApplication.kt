package com.ruslan.hlushan.core.impl

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.ruslan.hlushan.android.storage.SharedPrefsProvider
import com.ruslan.hlushan.core.api.di.IBaseInjector
import com.ruslan.hlushan.core.api.di.InjectorHolder
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.Settings
import com.ruslan.hlushan.core.api.managers.appLanguageNotFullCode
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.impl.tools.initTools
import com.ruslan.hlushan.core.impl.utils.SemEmergencyManagerLeakingActivity
import com.ruslan.hlushan.core.impl.utils.exceptions.RxErrorHandlingUtil
import com.ruslan.hlushan.core.impl.utils.getWrappedOrUpdateContext
import javax.inject.Inject

private const val APP_INITIALIZATION_TAG: String = "APP_INITIALIZATION"

abstract class BaseApplication : Application(), InjectorHolder {

    @Inject protected lateinit var settings: Settings
    @Inject protected lateinit var appLogger: AppLogger

    @get:SuppressWarnings("VariableNaming")
    abstract val APP_TAG: String
    abstract override val iBaseInjector: IBaseInjector

    override fun onCreate() {
        logMessage("before super.onCreate()")
        super.onCreate()
        initCrashlytics()
        SharedPrefsProvider.init()
        logMessage("after super.onCreate()")
        SemEmergencyManagerLeakingActivity.applyFix(this)
        logMessage("after SemEmergencyManagerLeakingActivity.applyFix()")
        initDagger2AndInject()
        logMessage("after initDagger2AndInject()")
        initSecond()
    }

    @UiMainThread
    protected abstract fun initCrashlytics()

    @UiMainThread
    protected abstract fun initDagger2AndInject()

    @UiMainThread
    private fun initSecond() {
        logMessage("initSecond()")
        setAppErrorHandling()
        logMessage("after setAppErrorHandling()")
        initBaseContextLang()
        logMessage("after initBaseContextLang()")
        initCrashlytics()
        logMessage("after initCrashlytics()")
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        logMessage("after AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)")
        initTools(this, appLogger, this::logMessage)
    }

    @UiMainThread
    private fun initBaseContextLang() {
        getWrappedOrUpdateContext(baseContext, settings.appLanguageNotFullCode)
    }

    private fun setAppErrorHandling() {
        RxErrorHandlingUtil.setRxErrorHandling(appLogger)
        // TODO: tests needed
        //AppExceptionHandler.setUp(this)
    }

    private fun logMessage(message: String) =
            Log.i(APP_TAG, "$APP_INITIALIZATION_TAG -> $message")
}