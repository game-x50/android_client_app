package com.ruslan.hlushan.game.play.api

import com.ruslan.hlushan.game.play.api.calculations.CellTextSizeParams

@SuppressWarnings("DataClassShouldBeImmutable", "LongParameterList")
data class RectangleArea(
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

        const val MOVED_ITEM_COEFFICIENT_PADDING_Y = 1.5f
        const val MOVED_ITEM_COEFFICIENT_PADDING_X = 0.5f

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

val RectangleArea.rightX: Float get() = (this.leftX + this.size)

val RectangleArea.bottomY: Float get() = (this.topY + this.size)

val RectangleArea.centerX: Float get() = (this.leftX + (this.size / 2))

val RectangleArea.centerY: Float get() = (this.topY + (this.size / 2))

@get:SuppressWarnings("MagicNumber")
val RectangleArea.textBackgroundRadius: Float
    get() = (this.size / 8)

internal fun RectangleArea.convertToFake() {
    this.isFake = true
    this.number = 0
}

internal fun RectangleArea.containsInclusive(x: Float, y: Float): Boolean =
        (this.leftX <= x && x <= this.rightX
         && this.topY <= y && y <= this.bottomY)