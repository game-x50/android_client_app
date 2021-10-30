package com.ruslan.hlushan.core.logger.impl

import android.util.Log
import com.ruslan.hlushan.core.config.app.InitAppConfig
import javax.inject.Inject

internal class LogcatLoggerImpl
@Inject
constructor(
        private val initAppConfig: InitAppConfig
) : LogcatLogger {

    override fun log(tag: String, logMessage: String) {
        if (initAppConfig.isLogcatEnabled) {
            Log.i(tag, logMessage)
        }
    }

    override fun log(tag: String, logMessage: String?, error: Throwable?) {
        if (initAppConfig.isLogcatEnabled) {
            Log.e(tag, logMessage, error)
        }
    }
}