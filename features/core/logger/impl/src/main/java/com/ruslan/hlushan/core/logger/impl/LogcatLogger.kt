package com.ruslan.hlushan.core.logger.impl

internal interface LogcatLogger {

    fun log(tag: String, logMessage: String)

    fun log(tag: String, logMessage: String?, error: Throwable?)
}