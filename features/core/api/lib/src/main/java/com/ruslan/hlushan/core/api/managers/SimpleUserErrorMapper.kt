package com.ruslan.hlushan.core.api.managers

interface SimpleUserErrorMapper {

    fun produceUserMessage(error: Throwable): String?
}