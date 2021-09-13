package com.ruslan.hlushan.core.extensions

import kotlin.math.absoluteValue
import kotlin.math.log10

fun Int.divRoundingUpToLargerInt(divisor: Int): Int {
    val min = this / divisor
    return if (min * divisor == this) {
        min
    } else {
        min + 1
    }
}

fun Int.countDigits(): Int = when {
    this == 0 -> 1
    this > 0  -> (log10(this.toDouble()) + 1).toInt()
    else      -> (-this).countDigits() + 1
}

fun Float.isSameWithError(other: Float, error: Float) =
        ((this - other).absoluteValue < error)