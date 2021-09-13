package com.ruslan.hlushan.core.extensions

inline fun String.nullIfBlank(): String? = if (this.isBlank()) null else this