package com.ruslan.hlushan.test.utils

import kotlin.reflect.KClass

fun <E : Throwable> assertThrows(clazz: KClass<E>, lambda: () -> Unit) {
    var error: Throwable? = null

    try {
        lambda()
    } catch (e: Throwable) {
        error = e
    }

    when {
        (error == null)         -> throw Exception("Exception was not thrown, but expected<$clazz>")
        (error::class != clazz) -> throw Exception("Unexpected exception, expected<$clazz>, but was $error")
    }
}