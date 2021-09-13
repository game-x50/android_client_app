package com.ruslan.hlushan.core.logger.impl

import android.util.Log
import com.ruslan.hlushan.core.api.utils.InitAppConfig
import com.ruslan.hlushan.core.extensions.nullIfBlank
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.logger.api.ErrorLogger
import com.ruslan.hlushan.core.logger.api.FileLogger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ZZZZZ", Locale.UK)

private fun String?.toDefaultMessageIfEmpty(): String = (this?.nullIfBlank() ?: "empty_message")

private fun String?.createMessage(): String = " :-${toDefaultMessageIfEmpty()}"

private fun String.createMessageForFileLogger(): String = "${dateFormatter.format(Date())} : $this"

private fun Throwable?.createMessage(): String =
        (this?.let { err ->
            "${err::class.java.name}(${err.message.toDefaultMessageIfEmpty()}) \n ${Log.getStackTraceString(err)}"
        } ?: "unknown_throwable")

private fun String?.createMessage(throwable: Throwable?): String =
        " :-${toDefaultMessageIfEmpty()} : ${throwable.createMessage()}"

internal class AppLoggerImpl
@Inject
constructor(
        private val fileLogger: FileLogger,
        private val logcatLogger: LogcatLogger,
        private val errorLogger: ErrorLogger,
        private val initAppConfig: InitAppConfig
) : AppLogger {

    @Suppress("MagicNumber")
    private val methodName: String
        get() = ".${Thread.currentThread().stackTrace[4].methodName}()"

    override fun log(any: Any) = logMessageIfMakeSense {
        val logMessage = "${any.javaClass.simpleName}$methodName"
        logcatLogger.log(initAppConfig.appTag, logMessage)
        fileLogger.logToFile(logMessage.createMessageForFileLogger())
    }

    override fun log(any: Any, message: String?) = logMessageIfMakeSense {
        val logMessage = "${any.javaClass.simpleName}$methodName${message.createMessage()}"
        logcatLogger.log(initAppConfig.appTag, logMessage)
        fileLogger.logToFile(logMessage.createMessageForFileLogger())
    }

    override fun log(any: Any, message: String?, error: Throwable) {
        logMessageIfMakeSense {
            val logMessage = "${any.javaClass.simpleName}$methodName${message.createMessage(error)}"
            logcatLogger.log(initAppConfig.appTag, message, error)
            fileLogger.logToFile(logMessage.createMessageForFileLogger())
        }
        errorLogger.logError(error)
    }

    override fun logClass(clazz: Class<*>) = logMessageIfMakeSense {
        val logMessage = "${clazz.simpleName}$methodName"
        logcatLogger.log(initAppConfig.appTag, logMessage)
        fileLogger.logToFile(logMessage.createMessageForFileLogger())
    }

    override fun logClass(clazz: Class<*>, message: String?) = logMessageIfMakeSense {
        val logMessage = "${clazz.simpleName}$methodName${message.createMessage()}"
        logcatLogger.log(initAppConfig.appTag, logMessage)
        fileLogger.logToFile(logMessage.createMessageForFileLogger())
    }

    override fun logClass(clazz: Class<*>, message: String?, error: Throwable) {
        logMessageIfMakeSense {
            val logMessage = "${clazz.simpleName}$methodName${message.createMessage(error)}"
            logcatLogger.log(initAppConfig.appTag, logMessage, error)
            fileLogger.logToFile(logMessage.createMessageForFileLogger())
        }
        errorLogger.logError(error)
    }

    private inline fun logMessageIfMakeSense(log: () -> Unit) {
        if (initAppConfig.isLogcatEnabled || fileLogger.enabled) {
            log()
        }
    }
}