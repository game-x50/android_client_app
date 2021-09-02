package com.ruslan.hlushan.core.impl.log

import android.util.Log
import com.ruslan.hlushan.core.api.log.LogcatLogger
import com.ruslan.hlushan.core.api.utils.InitAppConfig
import javax.inject.Inject

/**
 * @author Ruslan Hlushan on 10/29/18.
 */
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