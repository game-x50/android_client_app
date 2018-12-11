package com.ruslan.hlushan.game.play.ui.view

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.core.graphics.ColorUtils

//https://proandroiddev.com/accessibility-of-text-over-generic-background-color-e82e9546731a

@IntRange(from = 0x0, to = 0xFF)
private const val MAX_ALPHA = 0xFF

@SuppressWarnings("MagicNumber")
@ColorInt
internal fun calculateBackgroundColorFor(rectangleArea: RectangleArea): Int =
        if (rectangleArea.drawBackground) {
            (0x7F_FF_FF_FF - rectangleArea.number * 1_000_057).or(0x50_00_00_00)
        } else {
            Color.TRANSPARENT
        }

@ColorInt
internal fun calculateTextColorForBackground(
        @ColorInt backgroundColor: Int,
        @ColorInt colorOnTransparent: Int
): Int =
        if (backgroundColor == Color.TRANSPARENT) {
            colorOnTransparent
        } else {

            val whiteContrast = ColorUtils.calculateContrast(Color.WHITE, ColorUtils.setAlphaComponent(backgroundColor, MAX_ALPHA))
            val blackContrast = ColorUtils.calculateContrast(Color.BLACK, ColorUtils.setAlphaComponent(backgroundColor, MAX_ALPHA))

            if (whiteContrast > blackContrast) {
                Color.WHITE
            } else {
                Color.BLACK
            }
        }