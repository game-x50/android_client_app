package com.ruslan.hlushan.android.extensions

import android.os.Bundle

fun Bundle.getIntOrNull(argKey: String): Int? =
		if (this.containsKey(argKey)) {
			getInt(argKey)
		} else {
			null
		}

fun Bundle.getLongOrNull(argKey: String): Long? =
		if (this.containsKey(argKey)) {
			getLong(argKey)
		} else {
			null
		}

fun Bundle.getBooleanOrNull(argKey: String): Boolean? =
		if (containsKey(argKey)) {
			getBoolean(argKey)
		} else {
			null
		}

fun Bundle.getFloatOrNull(argKey: String): Float? =
		if (containsKey(argKey)) {
			getFloat(argKey)
		} else {
			null
		}

fun Bundle.getDoubleOrNull(argKey: String): Double? =
		if (containsKey(argKey)) {
			getDouble(argKey)
		} else {
			null
		}

fun Bundle.getShortOrNull(argKey: String): Short? =
		if (containsKey(argKey)) {
			getShort(argKey)
		} else {
			null
		}