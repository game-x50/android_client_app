package com.ruslan.hlushan.android.extensions

import androidx.fragment.app.Fragment

fun Fragment.showSystemMessage(text: String, longDuration: Boolean = false) {
    activity?.showSystemMessage(text, longDuration)
}