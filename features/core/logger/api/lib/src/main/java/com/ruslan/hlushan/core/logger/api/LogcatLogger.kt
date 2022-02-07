package com.ruslan.hlushan.core.logger.api

interface LogcatLogger {

    var enabled: Boolean

    fun log(tag: String, logMessage: String)

    fun log(tag: String, logMessage: String?, error: Throwable?)
}

object EmptyLogcatLoggerImpl : LogcatLogger {

    override var enabled: Boolean
        get() = false
        set(value) {}

    override fun log(tag: String, logMessage: String) = Unit

    override fun log(tag: String, logMessage: String?, error: Throwable?) = Unit
}