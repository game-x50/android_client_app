package com.ruslan.hlushan.extensions

/**
 * @author Ruslan Hlushan on 1/9/19.
 */

fun <T> lazyUnsafe(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)