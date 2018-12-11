package com.ruslan.hlushan.core.api.log

interface AppLogger {

    fun log(any: Any)

    fun log(any: Any, message: String?)

    fun log(any: Any, message: String?, error: Throwable)

    fun logClass(clazz: Class<*>)

    fun logClass(clazz: Class<*>, message: String?)

    fun logClass(clazz: Class<*>, message: String?, error: Throwable)
}