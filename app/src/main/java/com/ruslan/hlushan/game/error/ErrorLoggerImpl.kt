package com.ruslan.hlushan.game.error

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ruslan.hlushan.core.api.log.ErrorLogger
import com.ruslan.hlushan.game.BuildConfig

/**
 * @author Ruslan Hlushan on 10/30/18.
 */
internal class ErrorLoggerImpl : ErrorLogger {

    @SuppressWarnings("ClassOrdering")
    companion object {
        fun init() {
            val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
            firebaseCrashlytics.setCrashlyticsCollectionEnabled(BuildConfig.IS_CRASHLYTICS_ENABLED)
        }
    }

    override fun logError(throwable: Throwable?) {
        if (throwable != null) {
            FirebaseCrashlytics.getInstance().recordException(throwable)
        }
    }
}