package com.ruslan.hlushan.android.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

fun Context.getBitmapFromVectorDrawable(@DrawableRes vectorDrawableResId: Int): Bitmap {

    var drawable: Drawable = ContextCompat.getDrawable(this, vectorDrawableResId)
                             ?: throw IllegalArgumentException("Invalid drawable res $vectorDrawableResId")

    @SuppressLint("ObsoleteSdkInt")
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        drawable = DrawableCompat.wrap(drawable).mutate()
    }

    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
                                     drawable.intrinsicHeight,
                                     Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}