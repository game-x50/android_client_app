package com.ruslan.hlushan.extensions

import java.lang.IllegalArgumentException

inline fun Boolean.Companion.allEquals(vararg values: Boolean): Boolean =
        if (values.size > 1) {
            val firstValue = values.first()
            values.all { value -> value == firstValue }
        } else {
            throw IllegalArgumentException("Size should be more than 1")
        }