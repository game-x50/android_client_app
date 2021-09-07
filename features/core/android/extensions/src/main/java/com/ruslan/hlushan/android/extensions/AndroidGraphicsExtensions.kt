package com.ruslan.hlushan.android.extensions

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.view.Gravity
import kotlin.math.pow
import kotlin.math.sqrt

fun applyGravity(gravity: Int, width: Float, height: Float, container: RectF): RectF {
    val containerInt = container.run { Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt()) }
    val outRectInt = Rect()
    Gravity.apply(gravity, width.toInt(), height.toInt(), containerInt, outRectInt)
    return RectF(
            outRectInt.left.toFloat(),
            outRectInt.top.toFloat(),
            outRectInt.right.toFloat(),
            outRectInt.bottom.toFloat()
    )
}

fun RectF.copy(leftX: Float = left, topY: Float = top, rightX: Float = right, bottomY: Float = bottom) =
        RectF(leftX, topY, rightX, bottomY)

fun RectF.containsInclusive(point: PointF): Boolean =
        (this.left <= this.right && this.top <= this.bottom
         && point.x in this.left..this.right && point.y in this.top..this.bottom)

val RectF.diagonal: Float
    get() = sqrt(width().toDouble().pow(2.toDouble()) + height().toDouble().pow(2.toDouble())).toFloat()

fun Paint.getPositionYForCenterY(centerY: Float): Float = (centerY - (descent() + ascent()) / 2)

fun Canvas.drawTextFromTopToBottom(text: String, topY: Float, textPadding: Float, leftX: Float, textPaint: Paint) {
    val textPaintDiff = (textPaint.descent() - textPaint.ascent())

    var yLeftTop = (topY - textPaint.ascent() + textPadding)
    val leftTopX = (leftX + textPadding)
    for (line in text.split("\n")) {
        this.drawText(line, leftTopX, yLeftTop, textPaint)
        yLeftTop += textPaintDiff
    }
}

fun Canvas.drawTextFromBottomToTop(text: String, bottomY: Float, textPadding: Float, rightX: Float, textPaint: Paint) {
    val textPaintDiff = (textPaint.descent() - textPaint.ascent())

    var yBottomRight = (bottomY - textPaint.descent() - textPadding)
    val bottomRightX = (rightX - textPadding)
    for (line in text.split("\n").reversed()) {
        this.drawText(line, bottomRightX, yBottomRight, textPaint)
        yBottomRight -= textPaintDiff
    }
}

fun Float.toDp(): Float = (this / Resources.getSystem().displayMetrics.density)

fun Float.toPx(): Float = (this * Resources.getSystem().displayMetrics.density)