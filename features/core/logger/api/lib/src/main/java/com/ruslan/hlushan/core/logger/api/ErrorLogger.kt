package com.ruslan.hlushan.core.logger.api

interface ErrorLogger {

    fun logError(throwable: Throwable?)
}