package com.ruslan.hlushan.core.ui.api.view

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt

class GridDrawable(
        @ColorInt linesColor: Int,
        private val distancePx: Int
) : Drawable() {

    private val paint = Paint().apply {
        strokeWidth = 1f
        color = linesColor
    }

    override fun draw(canvas: Canvas) {
        val boundsRect: Rect = bounds

        val boundsWidth = boundsRect.width()
        val boundsHeight = boundsRect.height()

        val boundsWidthFloat = boundsWidth.toFloat()
        val boundsHeightFloat = boundsHeight.toFloat()

        for (y in distancePx until boundsHeight step distancePx) {
            val floatY = y.toFloat()
            canvas.drawLine(0f, floatY, boundsWidthFloat, floatY, paint)
        }

        for (x in distancePx until boundsWidth step distancePx) {
            val floatX = x.toFloat()
            canvas.drawLine(floatX, 0f, floatX, boundsHeightFloat, paint)
        }
    }

    override fun setAlpha(alpha: Int) = Unit

    override fun setColorFilter(cf: ColorFilter?) = Unit

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}