package com.ruslan.hlushan.core.error

interface SimpleUserErrorMapper {

    fun produceUserMessage(error: Throwable): String?
}