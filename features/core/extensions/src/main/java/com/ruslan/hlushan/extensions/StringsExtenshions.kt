package com.ruslan.hlushan.extensions

/**
 * Created by mac-131 on 3/26/18.
 */

inline fun String.nullIfBlank(): String? = if (this.isBlank()) null else this