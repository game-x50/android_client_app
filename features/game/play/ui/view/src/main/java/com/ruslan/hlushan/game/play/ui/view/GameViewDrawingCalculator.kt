package com.ruslan.hlushan.game.play.ui.view

import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import com.ruslan.hlushan.core.extensions.countDigits
import com.ruslan.hlushan.core.extensions.divRoundingUpToLargerInt
import com.ruslan.hlushan.core.extensions.ifNotNull
import com.ruslan.hlushan.core.value.holder.MutableValueHolder
import com.ruslan.hlushan.game.play.api.COMBO_SUMS
import com.ruslan.hlushan.game.play.api.RectangleArea
import com.ruslan.hlushan.game.play.api.UndoButton
import com.ruslan.hlushan.game.play.api.calculations.CellTextSizeParams
import com.ruslan.hlushan.game.play.api.calculations.GameViewDimensions
import com.ruslan.hlushan.game.play.api.calculations.GameViewParams
import com.ruslan.hlushan.game.play.api.calculations.GridLine
import com.ruslan.hlushan.game.play.api.calculations.ItemsMatrix
import com.ruslan.hlushan.game.play.api.calculations.countRowsAndColumns
import com.ruslan.hlushan.game.play.api.calculations.getColumn
import com.ruslan.hlushan.game.play.api.calculations.getRow
import kotlin.math.min

//TODO: #write_unit_tests
//TODO: avoid 'it'
//TODO: add named params where needed
@SuppressWarnings("MagicNumber", "MaxLineLength", "LargeClass")
internal object GameViewDrawingCalculator {

    private const val DEFAULT_HEIGHT = 1000

    private const val ALLOWED_TEXT_SPACE_PERCENT = 0.8f

    private fun countNewElementRows(gameViewParams: GameViewParams): Int =
            gameViewParams.countNewElements.divRoundingUpToLargerInt(gameViewParams.countRowsAndColumnsIncludeSums)

    private fun totalRowsCountOfNewElementsWithMarginBetweenTableAndNewElementsWithoutLines(gameViewParams: GameViewParams): Double {
        val countNewElementRows = countNewElementRows(gameViewParams)
        return (countNewElementRows + gameViewParams.marginInCellsBetweenTableAndNewElementsLine)
    }

    private fun withToHeightRatioWithoutLines(gameViewParams: GameViewParams): Double =
            ((gameViewParams.countRowsAndColumnsIncludeSums)
             / (gameViewParams.countRowsAndColumnsIncludeSums + totalRowsCountOfNewElementsWithMarginBetweenTableAndNewElementsWithoutLines(gameViewParams)))

    fun calculateGameViewWith(widthMeasureSpec: Int, gameViewParams: GameViewParams): Int {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)

        return when (widthMode) {
            View.MeasureSpec.EXACTLY, View.MeasureSpec.AT_MOST -> widthSize
            else                                               -> (DEFAULT_HEIGHT * withToHeightRatioWithoutLines(gameViewParams)).toInt()
        }
    }

    fun calculateGameViewHeight(heightMeasureSpec: Int): Int {
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        return when (heightMode) {
            View.MeasureSpec.EXACTLY, View.MeasureSpec.AT_MOST -> heightSize
            else                                               -> DEFAULT_HEIGHT
        }
    }

    fun measureViewSize(viewHeight: Int, viewWidth: Int, gameViewParams: GameViewParams, gameDrawer: GameDrawer): GameViewDimensions {

        val withToHeightRatioWithoutLines = withToHeightRatioWithoutLines(gameViewParams)
        val countOfNewElementsHorizontalLines = countNewElementRows(gameViewParams) + 1
        val totalHorizontalLinesSize = (gameViewParams.totalGameLinesSize + (countOfNewElementsHorizontalLines * gameViewParams.tinyLine))

        val maximalUsingViewWidth =
                if (((viewHeight - totalHorizontalLinesSize).toDouble() * withToHeightRatioWithoutLines + gameViewParams.totalGameLinesSize) > viewWidth.toDouble()) {
                    viewWidth
                } else {
                    (((viewHeight - totalHorizontalLinesSize) * withToHeightRatioWithoutLines) + gameViewParams.totalGameLinesSize).toInt()
                }

        val cellSize = ((maximalUsingViewWidth - gameViewParams.totalGameLinesSize) / gameViewParams.countRowsAndColumnsIncludeSums)

        val usingViewWithWithoutLines = (cellSize * gameViewParams.countRowsAndColumnsIncludeSums)
        val usingViewWidth = (usingViewWithWithoutLines + gameViewParams.totalGameLinesSize)
        val usingViewHeight = ((usingViewWithWithoutLines / withToHeightRatioWithoutLines) + gameViewParams.totalGameLinesSize).toInt()

        val horizontalNonUsingSpace = viewWidth - usingViewWidth
        val verticalNonUsingSpace = viewHeight - usingViewHeight

        val tableViewSize = usingViewWidth - cellSize - gameViewParams.tinyLine

        return GameViewDimensions(
                tableViewSizeWithoutSums = tableViewSize,
                usingViewHeight = usingViewHeight,
                usingViewWidth = usingViewWidth,
                horizontalNonUsingSpace = horizontalNonUsingSpace,
                verticalNonUsingSpace = verticalNonUsingSpace,
                cellSizeWithoutLines = cellSize,
                cellTextSizeParams = calculateCellTextSizeParams(cellSize, gameDrawer.testPaint)
        )
    }

    fun fillGameViewTable(
            itemsMatrix: ItemsMatrix,
            gameViewDimensions: GameViewDimensions,
            gameViewParams: GameViewParams,
            tableGridLines: MutableList<GridLine>
    ) {

        calculateAndFullTableCellsWithLinesCoordinates(itemsMatrix, gameViewDimensions, gameViewParams, tableGridLines)
        fullRectangleSumsCoordinates(itemsMatrix.rectanglesSums, gameViewDimensions, gameViewParams)
    }

    private fun calculateAndFullTableCellsWithLinesCoordinates(
            itemsMatrix: ItemsMatrix,
            gameViewDimensions: GameViewDimensions,
            gameViewParams: GameViewParams,
            tableGridLines: MutableList<GridLine>
    ) {
        tableGridLines.clear()

        initTableCellsSizesAndRowColumnSumsNonChangedCoordinates(itemsMatrix, gameViewDimensions, gameViewParams)

        for (i in 1..gameViewParams.countRowsAndColumnsIncludeSums) {

            val isBold = (((i - 1) % gameViewParams.countBigRowsAndColumns) == 0)

            val lineThick = if (isBold) {
                gameViewParams.boldLine
            } else {
                gameViewParams.tinyLine
            }

            val countOfBoldLinesBeforeLine = ((i + 1) / gameViewParams.countBigRowsAndColumns)
            val countOfTinyLinesBeforeLine = (i - countOfBoldLinesBeforeLine)

            val paddingBeforeInsideTable = ((gameViewDimensions.cellSizeWithoutLines * (i)).toFloat()
                                            + (countOfBoldLinesBeforeLine * gameViewParams.boldLine)
                                            + (countOfTinyLinesBeforeLine * gameViewParams.tinyLine))

            val leftOfVerticalLines = (gameViewDimensions.leftRightNonUsingPadding + paddingBeforeInsideTable)
            val topOfVerticalLines = gameViewDimensions.topBottomNonUsingPadding

            val leftOfHorizontalLines = gameViewDimensions.leftRightNonUsingPadding
            val topOfHorizontalLines = (gameViewDimensions.topBottomNonUsingPadding + paddingBeforeInsideTable)

            fullTableCellsWithLinesCoordinates(
                    itemsMatrix = itemsMatrix,
                    tableGridLines = tableGridLines,
                    rowOrColumnPosition = (i - 1),
                    lineThick = lineThick,
                    leftOfVerticalLines = leftOfVerticalLines,
                    topOfVerticalLines = topOfVerticalLines,
                    leftOfHorizontalLines = leftOfHorizontalLines,
                    topOfHorizontalLines = topOfHorizontalLines,
                    lineLength = gameViewDimensions.tableViewSizeWithSumsAndLines
            )
        }
    }

    private fun initTableCellsSizesAndRowColumnSumsNonChangedCoordinates(
            itemsMatrix: ItemsMatrix,
            gameViewDimensions: GameViewDimensions,
            gameViewParams: GameViewParams
    ) {
        itemsMatrix.items.forEach {
            it.size = gameViewDimensions.cellSizeWithoutLines.toFloat()
            it.cellTextSizeParams = gameViewDimensions.cellTextSizeParams
        }
        itemsMatrix.rowSums.forEach {
            it.leftX = gameViewDimensions.leftRightNonUsingPadding + gameViewParams.tinyLine
            it.size = gameViewDimensions.cellSizeWithoutLines.toFloat()
            it.cellTextSizeParams = gameViewDimensions.cellTextSizeParams
        }
        itemsMatrix.columnSums.forEach {
            it.topY = gameViewDimensions.topBottomNonUsingPadding + gameViewParams.tinyLine
            it.size = gameViewDimensions.cellSizeWithoutLines.toFloat()
            it.cellTextSizeParams = gameViewDimensions.cellTextSizeParams
        }
    }

    @SuppressWarnings("LongParameterList")
    private fun fullTableCellsWithLinesCoordinates(
            itemsMatrix: ItemsMatrix,
            tableGridLines: MutableList<GridLine>,
            rowOrColumnPosition: Int,
            lineThick: Int,
            leftOfVerticalLines: Float,
            topOfVerticalLines: Float,
            leftOfHorizontalLines: Float,
            topOfHorizontalLines: Float,
            lineLength: Int
    ) {

        tableGridLines.add(GridLine(
                leftOfLine = leftOfVerticalLines,
                topOfLine = topOfVerticalLines,
                width = lineThick.toFloat(),
                height = lineLength.toFloat()
        ))

        tableGridLines.add(GridLine(
                leftOfLine = leftOfHorizontalLines,
                topOfLine = topOfHorizontalLines,
                width = lineLength.toFloat(),
                height = lineThick.toFloat()
        ))

        if (rowOrColumnPosition in 0 until itemsMatrix.countRowsAndColumns) {

            val leftPaddingForTexts = (leftOfVerticalLines + lineThick)
            itemsMatrix.getColumn(rowOrColumnPosition).forEach { area ->
                area.leftX = leftPaddingForTexts
            }
            itemsMatrix.columnSums[rowOrColumnPosition].leftX = leftPaddingForTexts

            val topPaddingForTexts = (topOfHorizontalLines + lineThick)
            itemsMatrix.getRow(rowOrColumnPosition).forEach { area ->
                area.topY = topPaddingForTexts
            }
            itemsMatrix.rowSums[rowOrColumnPosition].topY = topPaddingForTexts
        }
    }

    private fun fullRectangleSumsCoordinates(
            rectanglesSums: List<RectangleArea>,
            gameViewDimensions: GameViewDimensions,
            gameViewParams: GameViewParams
    ) {

        val rectangleSumTextViewSize = ((gameViewParams.countBigRowsAndColumns * gameViewDimensions.cellSizeWithoutLines)
                                        + ((gameViewParams.countBigRowsAndColumns - 1) * gameViewParams.tinyLine))

        val rectangleSumCellToCellRatio = (rectangleSumTextViewSize / gameViewDimensions.cellSizeWithoutLines)

        val rectangleSumsCellTextSizeParams = CellTextSizeParams(gameViewDimensions.cellTextSizeParams.sizesByTextLength
                                                                         .mapValues { it.value * rectangleSumCellToCellRatio })

        rectanglesSums.forEach {
            it.size = rectangleSumTextViewSize.toFloat()
            it.cellTextSizeParams = rectangleSumsCellTextSizeParams
        }

        val defaultStartPaddingInsideTable = (gameViewParams.tinyLine
                                              + gameViewDimensions.cellSizeWithoutLines
                                              + gameViewParams.boldLine)

        val paddingForOneRectangleSum = (rectangleSumTextViewSize + gameViewParams.boldLine)

        for (i in 0 until gameViewParams.countRowsAndColumns) {

            val bigColumn = (i % gameViewParams.countBigRowsAndColumns)
            rectanglesSums[i].leftX = (gameViewDimensions.leftRightNonUsingPadding
                                       + defaultStartPaddingInsideTable
                                       + (paddingForOneRectangleSum * bigColumn))

            val bigRow = (i / gameViewParams.countBigRowsAndColumns)
            rectanglesSums[i].topY = (gameViewDimensions.topBottomNonUsingPadding
                                      + defaultStartPaddingInsideTable
                                      + (paddingForOneRectangleSum * bigRow))
        }
    }

    fun fillGameViewNewItems(
            newItemsSet: List<RectangleArea>,
            gameViewDimensions: GameViewDimensions,
            gameViewParams: GameViewParams,
            newElementsGrid: MutableList<GridLine>
    ) {
        newElementsGrid.clear()

        val countNewElements = newItemsSet.size

        val countRows = countNewElementRows(gameViewParams)

        newItemsSet.forEach {
            it.size = gameViewDimensions.cellSizeWithoutLines.toFloat()
            it.cellTextSizeParams = gameViewDimensions.cellTextSizeParams
        }

        var drawedElements = 0
        for (row in 0 until countRows) {

            val nonDrawedElementsCount = countNewElements - drawedElements
            val countColumnsInRow = when {
                (nonDrawedElementsCount % (countRows - row)) == 0                      -> nonDrawedElementsCount / (countRows - row)
                nonDrawedElementsCount > gameViewParams.countRowsAndColumnsIncludeSums -> gameViewParams.countRowsAndColumnsIncludeSums
                else                                                                   -> nonDrawedElementsCount
            }

            fullNewElementsRow(gameViewDimensions, gameViewParams, countColumnsInRow, row, newElementsGrid, newItemsSet, drawedElements)

            drawedElements += countColumnsInRow
        }
    }

    fun fillUndoButton(gameViewDimensions: GameViewDimensions, undoButtonHolder: MutableValueHolder<UndoButton>) {
        val leftX = gameViewDimensions.leftRightNonUsingPadding
        val topY = gameViewDimensions.topBottomNonUsingPadding
        undoButtonHolder.value = UndoButton(
                leftX = leftX,
                topY = topY,
                rightX = (leftX + gameViewDimensions.cellSizeWithoutLines),
                bottomY = (topY + gameViewDimensions.cellSizeWithoutLines)
        )
    }

    @SuppressWarnings("LongParameterList")
    private fun fullNewElementsRow(
            gameViewDimensions: GameViewDimensions,
            gameViewParams: GameViewParams,
            countColumnsInRow: Int,
            row: Int,
            newElementsGrid: MutableList<GridLine>,
            newItemsSet: List<RectangleArea>,
            drawedElements: Int
    ) {
        val cellWithTinyLine = gameViewDimensions.cellSizeWithoutLines + gameViewParams.tinyLine

        val leftPadding = gameViewDimensions.leftRightNonUsingPadding + (((gameViewParams.countRowsAndColumnsIncludeSums - countColumnsInRow) * cellWithTinyLine) / 2f)
        val topPadding = (gameViewDimensions.topBottomNonUsingPadding
                          + gameViewDimensions.tableViewSizeWithSumsAndLines + gameViewDimensions.cellSizeWithoutLines * gameViewParams.marginInCellsBetweenTableAndNewElementsLine
                          + (row * cellWithTinyLine)).toFloat()

        for (column in 0..countColumnsInRow) {
            val leftOfVerticalLines = leftPadding + (cellWithTinyLine * column).toFloat()
            val topOfVerticalLines = topPadding

            newElementsGrid.add(GridLine(
                    leftOfLine = leftOfVerticalLines,
                    topOfLine = topOfVerticalLines,
                    width = gameViewParams.tinyLine.toFloat(),
                    height = cellWithTinyLine.toFloat()
            ))

            ifNotNull(newItemsSet.getOrNull(drawedElements + column)) { area ->
                area.leftX = (leftOfVerticalLines + gameViewParams.tinyLine)
                area.topY = (topOfVerticalLines + gameViewParams.tinyLine)
            }
        }

        val leftOfHorizontalLines = leftPadding
        val widthOfHorizontalLines = (countColumnsInRow * cellWithTinyLine) + gameViewParams.tinyLine

        if (row == 0) {
            val topOfTopHorizontalLine = topPadding
            newElementsGrid.add(GridLine(
                    leftOfLine = leftOfHorizontalLines,
                    topOfLine = topOfTopHorizontalLine,
                    width = widthOfHorizontalLines.toFloat(),
                    height = gameViewParams.tinyLine.toFloat()
            ))
        }

        val topOfHorizontalLines = topPadding + cellWithTinyLine
        newElementsGrid.add(GridLine(
                leftOfLine = leftOfHorizontalLines,
                topOfLine = topOfHorizontalLines,
                width = widthOfHorizontalLines.toFloat(),
                height = gameViewParams.tinyLine.toFloat()
        ))
    }

    private fun calculateCellTextSizeParams(cellSize: Int, testPaint: Paint): CellTextSizeParams {
        val map = mutableMapOf<Int, Float>()

        val allowedTextAreaSize = (cellSize * ALLOWED_TEXT_SPACE_PERCENT)

        val gap = 3
        (1..(COMBO_SUMS.last().countDigits() + gap)).forEach {
            map[it] = getNormalTextSizeForParams(testPaint, allowedTextAreaSize, allowedTextAreaSize, it)
        }

        return CellTextSizeParams(map)
    }

    private fun getNormalTextSizeForParams(paint: Paint, desiredWidth: Float, desiredHeight: Float, textLength: Int): Float {
        val newText = StringBuilder().also { builder ->
            repeat(textLength) {
                builder.append("0")
            }
        }.toString()

        return getTextSizeForSizes(paint, desiredWidth, desiredHeight, newText)
    }

    fun getTextSizeForSizes(paint: Paint, desiredWidth: Float, desiredHeight: Float, text: String): Float {
        val testTextSize = 48f
        paint.textSize = testTextSize

        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)

        val xRatio = (desiredWidth / bounds.width())
        val yRatio = (desiredHeight / bounds.height())

        return (testTextSize * min(yRatio, xRatio))
    }
}