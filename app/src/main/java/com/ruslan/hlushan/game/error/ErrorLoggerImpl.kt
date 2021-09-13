package com.ruslan.hlushan.game.error

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ruslan.hlushan.core.logger.api.ErrorLogger
import com.ruslan.hlushan.game.BuildConfig

internal class ErrorLoggerImpl : ErrorLogger {

    override fun logError(throwable: Throwable?) {
        if (throwable != null) {
            FirebaseCrashlytics.getInstance().recordException(throwable)
        }
    }

    companion object {
        fun init() {
            val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
            firebaseCrashlytics.setCrashlyticsCollectionEnabled(BuildConfig.IS_CRASHLYTICS_ENABLED)
        }
    }
}