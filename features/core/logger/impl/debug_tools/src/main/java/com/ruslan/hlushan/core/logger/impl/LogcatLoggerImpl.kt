package com.ruslan.hlushan.core.logger.impl

import android.content.Context
import android.util.Log
import com.ruslan.hlushan.android.storage.BoolPreferencesDelegate
import com.ruslan.hlushan.android.storage.SharedPrefsProvider
import com.ruslan.hlushan.core.logger.api.LogcatLogger
import javax.inject.Inject

class LogcatLoggerImpl
@Inject
constructor(
        appContext: Context
) : LogcatLogger {

    override var enabled: Boolean by BoolPreferencesDelegate(
            preferences = SharedPrefsProvider.providePrefs(appContext, "logcat_logger_prefs"),
            key = "KEY_IS_Logcat_LOGS_ENABLED",
            defaultValue = true
    )

    override fun log(tag: String, logMessage: String) {
        if (enabled) {
            Log.i(tag, logMessage)
        }
    }

    override fun log(tag: String, logMessage: String?, error: Throwable?) {
        if (enabled) {
            Log.e(tag, logMessage, error)
        }
    }
}