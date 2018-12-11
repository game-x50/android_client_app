package com.ruslan.hlushan.game.play.ui.view

import com.ruslan.hlushan.game.play.ui.view.calculations.CellTextSizeParams

/**
 * @author Ruslan Hlushan on 8/30/18.
 */

@SuppressWarnings("DataClassShouldBeImmutable", "LongParameterList")
internal data class RectangleArea(
        val drawBackground: Boolean,
        val position: Int,
        var leftX: Float,
        var topY: Float,
        var size: Float,
        var number: Int,
        var cellTextSizeParams: CellTextSizeParams,
        var isFake: Boolean
) {

    companion object {

        fun createDefault(position: Int, number: Int, isFake: Boolean, drawBackground: Boolean): RectangleArea =
                RectangleArea(
                        leftX = 0f,
                        topY = 0f,
                        size = 0f,
                        position = position,
                        number = number,
                        cellTextSizeParams = CellTextSizeParams.createDefault(),
                        isFake = isFake,
                        drawBackground = drawBackground
                )
    }
}

internal val RectangleArea.rightX: Float get() = (this.leftX + this.size)

internal val RectangleArea.bottomY: Float get() = (this.topY + this.size)

internal val RectangleArea.centerX: Float get() = (this.leftX + (this.size / 2))

internal val RectangleArea.centerY: Float get() = (this.topY + (this.size / 2))

@get:SuppressWarnings("MagicNumber")
internal val RectangleArea.textBackgroundRadius: Float
    get() = (this.size / 8)

internal fun RectangleArea.convertToFake() {
    this.isFake = true
    this.number = 0
}

internal fun RectangleArea.containsInclusive(x: Float, y: Float): Boolean =
        (this.leftX <= x && x <= this.rightX
         && this.topY <= y && y <= this.bottomY)