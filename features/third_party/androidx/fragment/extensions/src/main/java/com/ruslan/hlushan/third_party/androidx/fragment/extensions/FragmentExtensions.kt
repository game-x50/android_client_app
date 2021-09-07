@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.androidx.fragment.extensions

import androidx.fragment.app.Fragment
import com.ruslan.hlushan.android.extensions.showSystemMessage

fun Fragment.showSystemMessage(text: String, longDuration: Boolean = false) {
    activity?.showSystemMessage(text, longDuration)
}