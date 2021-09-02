package com.ruslan.hlushan.android.extensions

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange

fun Activity.showSystemMessage(text: String, longDuration: Boolean = false) =
        Toast.makeText(this, text, if (longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()

fun Activity.applyColorOverlay(
        @FloatRange(from = 0.0, to = 1.0) dimAmount: Float,
        @ColorInt color: Int
) {
    val overlayDrawable = ColorDrawable(color)
    @Suppress("MagicNumber")
    overlayDrawable.alpha = (255 * dimAmount).toInt()
    applyDrawableOverlay(overlayDrawable)
}

fun Activity.applyDrawableOverlay(
        overlayDrawable: Drawable
) {
    val parentView = this.window.decorView
    overlayDrawable.setBounds(0, 0, parentView.width, parentView.height)
    parentView.overlay.add(overlayDrawable)
}

fun Activity.clearOverlay() {
    val overlay = this.window.decorView.overlay
    overlay.clear()
}