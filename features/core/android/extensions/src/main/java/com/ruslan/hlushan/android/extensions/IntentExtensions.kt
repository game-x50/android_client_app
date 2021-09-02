package com.ruslan.hlushan.android.extensions

import android.content.Intent

/**
 * @author Ruslan Hlushan on 12/27/18
 */

fun Intent.getIntExtraOrNull(extraKey: String): Int? =
		if (hasExtra(extraKey)) {
			getIntExtra(extraKey, 0)
		} else {
			null
		}

fun Intent.getLongExtraOrNull(extraKey: String): Long? =
		if (hasExtra(extraKey)) {
			getLongExtra(extraKey, 0L)
		} else {
			null
		}

fun Intent.getBooleanExtraOrNull(extraKey: String): Boolean? =
		if (hasExtra(extraKey)) {
			getBooleanExtra(extraKey, false)
		} else {
			null
		}

fun Intent.getFloatExtraOrNull(extraKey: String): Float? =
		if (hasExtra(extraKey)) {
			getFloatExtra(extraKey, 0.0f)
		} else {
			null
		}

fun Intent.getDoubleExtraOrNull(extraKey: String): Double? =
		if (hasExtra(extraKey)) {
			getDoubleExtra(extraKey, 0.0)
		} else {
			null
		}

fun Intent.getShortExtraOrNull(extraKey: String): Short? =
		if (hasExtra(extraKey)) {
			getShortExtra(extraKey, 0)
		} else {
			null
		}