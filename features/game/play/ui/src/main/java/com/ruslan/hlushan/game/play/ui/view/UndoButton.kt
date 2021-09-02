package com.ruslan.hlushan.game.play.ui.view

import android.graphics.RectF
import androidx.annotation.VisibleForTesting

internal data class UndoButton(
        val leftX: Float,
        val topY: Float,
        val rightX: Float,
        val bottomY: Float
) {

    val centerX: Float = ((leftX + rightX) / 2)

    val centerY: Float = ((topY + bottomY) / 2)

    val drawingHeight: Float = ((bottomY - topY) * ALLOWED_DRAWING_PERCENT)

    val drawingWidth: Float = ((rightX - leftX) * ALLOWED_DRAWING_PERCENT)

    companion object {

        @VisibleForTesting
        internal const val ALLOWED_DRAWING_PERCENT = 0.9f

        const val SYMBOL = "undo"

        fun createDefault(): UndoButton =
                UndoButton(leftX = 0f, topY = 0f, rightX = 0f, bottomY = 0f)
    }
}

internal fun UndoButton.contains(x: Float, y: Float): Boolean =
        RectF(this.leftX, this.topY, this.rightX, this.bottomY)
                .contains(x, y)