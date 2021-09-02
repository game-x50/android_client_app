package com.ruslan.hlushan.core.api.log

/**
 * @author Ruslan Hlushan on 10/29/18.
 */
interface LogcatLogger {

    fun log(tag: String, logMessage: String)

    fun log(tag: String, logMessage: String?, error: Throwable?)
}