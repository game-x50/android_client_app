package com.ruslan.hlushan.core.logger.api.test.utils

import com.ruslan.hlushan.core.logger.api.AppLogger

object EmptyAppLoggerImpl : AppLogger {

    override fun log(any: Any) = Unit

    override fun log(any: Any, message: String?) = Unit

    override fun log(any: Any, message: String?, error: Throwable) = Unit

    override fun logClass(clazz: Class<*>) = Unit

    override fun logClass(clazz: Class<*>, message: String?) = Unit

    override fun logClass(clazz: Class<*>, message: String?, error: Throwable) = Unit
}