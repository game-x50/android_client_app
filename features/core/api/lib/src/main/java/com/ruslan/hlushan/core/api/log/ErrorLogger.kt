package com.ruslan.hlushan.core.api.log

/**
 * @author Ruslan Hlushan on 10/29/18.
 */
interface ErrorLogger {

    fun logError(throwable: Throwable?)
}