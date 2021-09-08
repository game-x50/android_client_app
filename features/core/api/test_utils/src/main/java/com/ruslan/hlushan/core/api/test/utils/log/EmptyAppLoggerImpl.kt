package com.ruslan.hlushan.core.api.test.utils.log

import com.ruslan.hlushan.core.api.log.AppLogger

class EmptyAppLoggerImpl : AppLogger {

    override fun log(any: Any) = Unit

    override fun log(any: Any, message: String?) = Unit

    override fun log(any: Any, message: String?, error: Throwable) = Unit

    override fun logClass(clazz: Class<*>) = Unit

    override fun logClass(clazz: Class<*>, message: String?) = Unit

    override fun logClass(clazz: Class<*>, message: String?, error: Throwable) = Unit
}