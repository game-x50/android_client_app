package com.ruslan.hlushan.game.play.ui.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.ColorInt
import com.ruslan.hlushan.android.extensions.getPositionYForCenterY
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.game.play.api.RectangleArea
import com.ruslan.hlushan.game.play.api.UndoButton
import com.ruslan.hlushan.game.play.api.bottomY
import com.ruslan.hlushan.game.play.api.calculations.GameGrid
import com.ruslan.hlushan.game.play.api.calculations.GridLine
import com.ruslan.hlushan.game.play.api.calculations.getCellTextSizeForText
import com.ruslan.hlushan.game.play.api.centerX
import com.ruslan.hlushan.game.play.api.centerY
import com.ruslan.hlushan.game.play.api.rightX
import com.ruslan.hlushan.game.play.api.textBackgroundRadius

@UiMainThread
internal class GameDrawer(
        @ColorInt private val textColorOnTransparent: Int,
        @ColorInt gridColor: Int
) {

    private val rectangleAreaBackgroundPaint: Paint = Paint().apply {
        isAntiAlias = true
    }

    private val rectangleAreaTextPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    private val gridPaint: Paint = Paint().apply {
        isAntiAlias = true
        color = gridColor
    }

    private val undoButtonPaint = Paint().apply {
        isAntiAlias = true
        color = textColorOnTransparent
        textAlign = Paint.Align.CENTER
    }

    val testPaint = Paint()

    fun onLayout(undoButton: UndoButton) {
        undoButtonPaint.textSize = GameViewDrawingCalculator.getTextSizeForSizes(
                paint = undoButtonPaint,
                desiredWidth = undoButton.drawingWidth,
                desiredHeight = undoButton.drawingHeight,
                text = UndoButton.SYMBOL
        )
    }

    fun drawRectangleArea(rectangleArea: RectangleArea, canvas: Canvas) {
        @ColorInt val backgroundColor = drawBackground(rectangleArea, canvas)
        drawText(rectangleArea, canvas, backgroundColor)
    }

    fun drawGameGrid(gameGrid: GameGrid, canvas: Canvas) {
        gameGrid.tableGridLines
                .forEach { gridLine -> drawGridLine(gridLine, canvas) }

        gameGrid.newElementsGrid
                .forEach { gridLine -> drawGridLine(gridLine, canvas) }
    }

    fun drawUndoButton(undoButton: UndoButton, canvas: Canvas) {
        val yPos = undoButtonPaint.getPositionYForCenterY(undoButton.centerY)
        canvas.drawText(UndoButton.SYMBOL, undoButton.centerX, yPos, undoButtonPaint)
    }

    @ColorInt
    private fun drawBackground(rectangleArea: RectangleArea, canvas: Canvas): Int {
        @ColorInt val color = calculateBackgroundColorFor(rectangleArea)
        rectangleAreaBackgroundPaint.color = color
        canvas.drawRoundRect(
                RectF(rectangleArea.leftX, rectangleArea.topY, rectangleArea.rightX, rectangleArea.bottomY),
                rectangleArea.textBackgroundRadius,
                rectangleArea.textBackgroundRadius,
                rectangleAreaBackgroundPaint
        )

        return color
    }

    private fun drawText(rectangleArea: RectangleArea, canvas: Canvas, @ColorInt backgroundColor: Int) {
        rectangleAreaTextPaint.color = calculateTextColorForBackground(
                backgroundColor = backgroundColor,
                colorOnTransparent = textColorOnTransparent
        )
        rectangleAreaTextPaint.textSize = rectangleArea.cellTextSizeParams.getCellTextSizeForText(
                rectangleArea.number.toString()
        )
        val yPos = rectangleAreaTextPaint.getPositionYForCenterY(rectangleArea.centerY)
        canvas.drawText(rectangleArea.number.toString(), rectangleArea.centerX, yPos, rectangleAreaTextPaint)
    }

    private fun drawGridLine(gridLine: GridLine, canvas: Canvas) =
            canvas.drawRect(
                    gridLine.leftOfLine,
                    gridLine.topOfLine,
                    gridLine.rightOfLine,
                    gridLine.bottomOfLine,
                    gridPaint
            )
}