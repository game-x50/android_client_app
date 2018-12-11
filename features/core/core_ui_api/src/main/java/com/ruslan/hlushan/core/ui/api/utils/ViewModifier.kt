package com.ruslan.hlushan.core.ui.api.utils

import android.view.View

/**
 * Simple function that modifies a [View] and returns modified one so the consumer should use modifier version.
 */
interface ViewModifier {

    fun modify(view: View): View
}