package view.calculations

import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.api.play.dto.ImmutableNumbersMatrix
import com.ruslan.hlushan.game.play.ui.view.RectangleArea
import com.ruslan.hlushan.game.play.ui.view.bottomY
import com.ruslan.hlushan.game.play.ui.view.calculations.CellTextSizeParams
import com.ruslan.hlushan.game.play.ui.view.calculations.getColumn
import com.ruslan.hlushan.game.play.ui.view.calculations.getRow
import com.ruslan.hlushan.game.play.ui.view.calculations.isAllItemsFilled
import com.ruslan.hlushan.game.play.ui.view.calculations.notFakeItems
import com.ruslan.hlushan.game.play.ui.view.calculations.toImmutableNumbersMatrix
import com.ruslan.hlushan.game.play.ui.view.calculations.updateFrom
import com.ruslan.hlushan.game.play.ui.view.rightX
import com.ruslan.hlushan.test.utils.generateFakePositiveInt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import view.TEST_CELL_SIZE
import view.TotalSumChangedListenerTestImpl
import view.assertForImmutableMatrix
import view.createEmptyItemsMatrixFor
import view.createEmptyRectanglesFor
import view.generateFakeImmutableNumbersMatrix
import view.getIndexForRowColumn
import view.isIndexInColumn
import view.isIndexInRow

/**
 * @author Ruslan Hlushan on 2019-09-05
 */
@Suppress("LargeClass")
class ItemsMatrixTest {

    @Suppress("LongMethod")
    @Test
    fun initStateForDifferentTypes() {
        GameSize.values().forEach { gameSize ->

            val itemsMatrix = createEmptyItemsMatrixFor(gameSize, setUpRectangleSizes = false)

            val countRowsAndColumnsRange = (0 until gameSize.countRowsAndColumns)

            val allValuesRange = (0 until (gameSize.countRowsAndColumns * gameSize.countRowsAndColumns))

            assertFalse(itemsMatrix.isAllItemsFilled)
            assertEquals(
                    allValuesRange.map { n ->
                        RectangleArea.createDefault(
                                position = n,
                                number = 0,
                                isFake = true,
                                drawBackground = true
                        )
                    },
                    itemsMatrix.items
            )
            assertEquals(emptyList<RectangleArea>(), itemsMatrix.notFakeItems)

            countRowsAndColumnsRange.forEach { rowOrColumn ->

                assertEquals(
                        countRowsAndColumnsRange.map { localRowOrColumn ->
                            val position = ((gameSize.countRowsAndColumns * localRowOrColumn) + rowOrColumn)
                            RectangleArea.createDefault(
                                    position = position,
                                    number = 0,
                                    isFake = true,
                                    drawBackground = true
                            )
                        },
                        itemsMatrix.getColumn(rowOrColumn)
                )

                assertEquals(
                        countRowsAndColumnsRange.map { localRowOrColumn ->
                            val position = ((gameSize.countRowsAndColumns * rowOrColumn) + localRowOrColumn)
                            RectangleArea.createDefault(
                                    position = position,
                                    number = 0,
                                    isFake = true,
                                    drawBackground = true
                            )
                        },
                        itemsMatrix.getRow(rowOrColumn)
                )
            }

            assertEquals(
                    countRowsAndColumnsRange.map { n ->
                        RectangleArea.createDefault(
                                position = n,
                                number = 0,
                                isFake = true,
                                drawBackground = true
                        )
                    },
                    itemsMatrix.columnSums
            )
            assertEquals(
                    countRowsAndColumnsRange.map { n ->
                        RectangleArea.createDefault(
                                position = n,
                                number = 0,
                                isFake = true,
                                drawBackground = true
                        )
                    },
                    itemsMatrix.rowSums
            )
            assertEquals(
                    countRowsAndColumnsRange.map { n ->
                        RectangleArea.createDefault(
                                position = n,
                                number = 0,
                                isFake = true,
                                drawBackground = false
                        )
                    },
                    itemsMatrix.rectanglesSums
            )

            assertEquals(
                    ImmutableNumbersMatrix.emptyForSize(gameSize),
                    itemsMatrix.toImmutableNumbersMatrix()
            )
        }
    }

    @Test
    fun toImmutableNumbersMatrixAndUpdateFrom() = assertForImmutableMatrix { expectedImmutableMatrix,
                                                                             itemsMatrix,
                                                                             gameSize ->
        assertEquals(expectedImmutableMatrix, itemsMatrix.toImmutableNumbersMatrix())
    }

    @Test
    fun getRow() = assertForImmutableMatrix { expectedImmutableMatrix, itemsMatrix, gameSize ->
        for (row in 0 until gameSize.countRowsAndColumns) {
            val expected = expectedImmutableMatrix.numbers.mapIndexedNotNull { index, number ->
                val isInRow = isIndexInRow(index = index, row = row, gameSize = gameSize)

                if (isInRow) {
                    RectangleArea.createDefault(
                            position = index,
                            number = (number ?: 0),
                            isFake = (number == null),
                            drawBackground = true
                    )
                } else {
                    null
                }
            }

            assertEquals(expected, itemsMatrix.getRow(row))
        }
    }

    @Test
    fun getColumn() = assertForImmutableMatrix { expectedImmutableMatrix, itemsMatrix, gameSize ->
        for (column in 0 until gameSize.countRowsAndColumns) {
            val expected = expectedImmutableMatrix.numbers.mapIndexedNotNull { index, number ->
                val isInColumn = isIndexInColumn(index = index, column = column, gameSize = gameSize)

                if (isInColumn) {
                    RectangleArea.createDefault(
                            position = index,
                            number = (number ?: 0),
                            isFake = (number == null),
                            drawBackground = true
                    )
                } else {
                    null
                }
            }

            assertEquals(expected, itemsMatrix.getColumn(column))
        }
    }

    @Test
    fun getInvalidRow() = assertForImmutableMatrix { expectedImmutableMatrix, itemsMatrix, gameSize ->
        getInvalidRowColumnIndexesForSize(gameSize)
                .forEach { invalidRow ->
                    assertEquals(emptyList<RectangleArea>(), itemsMatrix.getRow(invalidRow))
                }
    }

    @Test
    fun getInvalidColumn() = assertForImmutableMatrix { expectedImmutableMatrix, itemsMatrix, gameSize ->
        getInvalidRowColumnIndexesForSize(gameSize)
                .forEach { invalidColumn ->
                    assertEquals(emptyList<RectangleArea>(), itemsMatrix.getColumn(invalidColumn))
                }
    }

    @SuppressWarnings("LongMethod")
    @Test
    fun replaceFakeWith_NonInsideAnyCell() {
        val cellTextSizeParams = CellTextSizeParams.createDefault()

        GameSize.values().forEach { gameSize ->

            val totalSumChangedListener = TotalSumChangedListenerTestImpl()

            val emptyItemsMatrix = createEmptyItemsMatrixFor(
                    gameSize,
                    totalSumChangedListener = totalSumChangedListener,
                    setUpRectangleSizes = true
            )

            val expected = createEmptyRectanglesFor(gameSize, setUpRectangleSizes = true)

            val leftMinX = emptyItemsMatrix.getRow(0).first().leftX
            val rightMaxX = emptyItemsMatrix.getRow(0).last().rightX
            val topMinY = emptyItemsMatrix.getColumn(0).first().topY
            val bottomMaxY = emptyItemsMatrix.getColumn(0).last().bottomY

            val minNonInsideX = (leftMinX - (TEST_CELL_SIZE / 2) - 1)
            for (yForLeftSide in (topMinY - 1).toInt()..(bottomMaxY + 1).toInt()) {
                emptyItemsMatrix.replaceFakeWith(RectangleArea(
                        leftX = minNonInsideX,
                        topY = yForLeftSide.toFloat(),
                        size = TEST_CELL_SIZE,
                        position = 0,
                        number = generateFakePositiveInt(),
                        cellTextSizeParams = cellTextSizeParams,
                        isFake = false,
                        drawBackground = true
                ))

                assertEquals(expected, emptyItemsMatrix.items)
            }

            val maxNonInsideX = (rightMaxX + (TEST_CELL_SIZE / 2) + 1)
            for (yForRightSide in (topMinY - 1).toInt()..(bottomMaxY + 1).toInt()) {
                emptyItemsMatrix.replaceFakeWith(RectangleArea(
                        leftX = maxNonInsideX,
                        topY = yForRightSide.toFloat(),
                        size = TEST_CELL_SIZE,
                        position = 0,
                        number = generateFakePositiveInt(),
                        cellTextSizeParams = cellTextSizeParams,
                        isFake = false,
                        drawBackground = true
                ))

                assertEquals(expected, emptyItemsMatrix.items)
            }

            val minNonInsideY = (topMinY - (TEST_CELL_SIZE / 2) - 1)
            for (xForTopSide in (leftMinX - 1).toInt()..(rightMaxX + 1).toInt()) {
                emptyItemsMatrix.replaceFakeWith(RectangleArea(
                        leftX = xForTopSide.toFloat(),
                        topY = minNonInsideY,
                        size = TEST_CELL_SIZE,
                        position = 0,
                        number = generateFakePositiveInt(),
                        cellTextSizeParams = cellTextSizeParams,
                        isFake = false,
                        drawBackground = true
                ))

                assertEquals(expected, emptyItemsMatrix.items)
            }

            val maxNonInsideY = (bottomMaxY + (TEST_CELL_SIZE / 2) + 1)
            for (xForBottomSide in (leftMinX - 1).toInt()..(rightMaxX + 1).toInt()) {
                emptyItemsMatrix.replaceFakeWith(RectangleArea(
                        leftX = xForBottomSide.toFloat(),
                        topY = maxNonInsideY,
                        size = TEST_CELL_SIZE,
                        position = 0,
                        number = generateFakePositiveInt(),
                        cellTextSizeParams = cellTextSizeParams,
                        isFake = false,
                        drawBackground = true
                ))

                assertEquals(expected, emptyItemsMatrix.items)
                totalSumChangedListener.assert()
            }
        }
    }

    @SuppressWarnings("NestedBlockDepth")
    @Test
    fun replaceFakeWith_InsideAnyEmptyCell() {
        GameSize.values().forEach { gameSize ->

            val totalSumChangedListener = TotalSumChangedListenerTestImpl()

            val itemsMatrix = createEmptyItemsMatrixFor(
                    gameSize,
                    totalSumChangedListener = totalSumChangedListener,
                    setUpRectangleSizes = true
            )

            val expected = createEmptyRectanglesFor(gameSize, setUpRectangleSizes = true)

            assertEquals(expected, itemsMatrix.items)

            for (row in 0 until gameSize.countRowsAndColumns) {
                for (column in 0 until gameSize.countRowsAndColumns) {

                    val index = getIndexForRowColumn(row = row, column = column, gameSize = gameSize)

                    val fakeRectangle = itemsMatrix.items[index]

                    val newNumber = generateFakePositiveInt()

                    itemsMatrix.replaceFakeWith(fakeRectangle.copy(
                            isFake = false,
                            position = generateFakePositiveInt(),
                            number = newNumber
                    ))

                    if (!((row == (gameSize.countRowsAndColumns - 1))
                          && (column == (gameSize.countRowsAndColumns - 1)))) {
                        totalSumChangedListener.addNextExpectedNumber(newNumber)
                    }

                    expected[index] = expected[index].copy(isFake = false, number = newNumber)

                    assertEquals(expected, itemsMatrix.items)
                }
            }

            totalSumChangedListener.assert()
        }
    }

    @Test
    @SuppressWarnings("MaxLineLength")
    fun replaceFakeWith_InsideAnyNonEmptyCell() {
        val lessThenHalfOfCell = ((TEST_CELL_SIZE / 2) - 1)
        val positionOfReplace = 0

        repeat(10) { _ ->

            GameSize.values().forEach { gameSize ->

                val totalSumChangedListener = TotalSumChangedListenerTestImpl()

                val partiallyFilledItemsMatrix = createEmptyItemsMatrixFor(
                        gameSize,
                        totalSumChangedListener = totalSumChangedListener,
                        setUpRectangleSizes = true
                )

                val immutableNumbersMatrix = generateFakeImmutableNumbersMatrix(
                        gameSize,
                        emptyRow = null,
                        emptyColumn = null,
                        emptyRectangle = null
                )

                partiallyFilledItemsMatrix.updateFrom(immutableNumbersMatrix)

                val expected = partiallyFilledItemsMatrix.items.map { rect -> rect.copy() }

                partiallyFilledItemsMatrix.notFakeItems
                        .forEach { nonFakeRectangleArea ->
                            partiallyFilledItemsMatrix.replaceFakeWith(nonFakeRectangleArea.copy(
                                    number = generateFakePositiveInt(),
                                    position = positionOfReplace
                            ))
                            assertEquals(expected, partiallyFilledItemsMatrix.items)

                            partiallyFilledItemsMatrix.replaceFakeWith(nonFakeRectangleArea.copy(
                                    leftX = (nonFakeRectangleArea.leftX - lessThenHalfOfCell),
                                    number = generateFakePositiveInt(),
                                    position = positionOfReplace
                            ))
                            assertEquals(expected, partiallyFilledItemsMatrix.items)

                            partiallyFilledItemsMatrix.replaceFakeWith(nonFakeRectangleArea.copy(
                                    leftX = (nonFakeRectangleArea.leftX + lessThenHalfOfCell),
                                    number = generateFakePositiveInt(),
                                    position = positionOfReplace
                            ))
                            assertEquals(expected, partiallyFilledItemsMatrix.items)

                            partiallyFilledItemsMatrix.replaceFakeWith(nonFakeRectangleArea.copy(
                                    topY = (nonFakeRectangleArea.topY - lessThenHalfOfCell),
                                    number = generateFakePositiveInt(),
                                    position = positionOfReplace
                            ))
                            assertEquals(expected, partiallyFilledItemsMatrix.items)

                            partiallyFilledItemsMatrix.replaceFakeWith(nonFakeRectangleArea.copy(
                                    topY = (nonFakeRectangleArea.topY + lessThenHalfOfCell),
                                    number = generateFakePositiveInt(),
                                    position = positionOfReplace
                            ))
                            assertEquals(expected, partiallyFilledItemsMatrix.items)
                        }

                totalSumChangedListener.assert()
            }
        }
    }

    private fun getInvalidRowColumnIndexesForSize(gameSize: GameSize): IntArray =
            intArrayOf(-1, gameSize.countRowsAndColumns + 1)
}