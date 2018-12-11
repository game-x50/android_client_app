package view

import com.ruslan.hlushan.game.core.api.play.dto.GameSize
import com.ruslan.hlushan.game.core.api.play.dto.ImmutableNumbersMatrix
import com.ruslan.hlushan.game.play.ui.view.RectangleArea
import com.ruslan.hlushan.game.play.ui.view.calculations.ItemsMatrix
import com.ruslan.hlushan.game.play.ui.view.calculations.updateFrom
import com.ruslan.hlushan.game.play.ui.view.listeners.TotalSumChangedListener
import com.ruslan.hlushan.test.utils.generateFakeBool
import kotlin.math.sqrt

/**
 * @author Ruslan Hlushan on 2019-10-09
 */

const val TEST_CELL_SIZE: Float = 29f

internal fun createFakeAllItemsFilledListener(): ((matrix: ImmutableNumbersMatrix) -> Unit) = {}

internal fun createFakeTotalSumChangedListener(): TotalSumChangedListener =
        TotalSumChangedListener { Unit }

internal fun generateFakeImmutableNumbersMatrix(
        gameSize: GameSize,
        emptyRow: Int?,
        emptyColumn: Int?,
        emptyRectangle: Int?
): ImmutableNumbersMatrix {

    val numbers = (0 until (gameSize.countRowsAndColumns * gameSize.countRowsAndColumns))
            .map { index ->
                val shouldBeRowEmpty: Boolean = (emptyRow != null
                                                 && isIndexInRow(index = index, row = emptyRow, gameSize = gameSize))
                val shouldBeColumnEmpty: Boolean = (emptyColumn != null
                                                    && isIndexInColumn(index = index, column = emptyColumn, gameSize = gameSize))
                val shouldBeRectangleEmpty: Boolean = (emptyRectangle != null
                                                       && isIndexInRectangle(index = index, rectangle = emptyRectangle, gameSize = gameSize))

                if (shouldBeRowEmpty || shouldBeColumnEmpty || shouldBeRectangleEmpty) {
                    null
                } else {
                    1.takeIf { generateFakeBool() }
                }
            }

    return ImmutableNumbersMatrix(numbers = numbers, gameSize = gameSize, totalSum = numbers.filterNotNull().sum())
}

internal fun createEmptyRectanglesFor(gameSize: GameSize, setUpRectangleSizes: Boolean): MutableList<RectangleArea> {
    val rectangles = (0 until (gameSize.countRowsAndColumns * gameSize.countRowsAndColumns))
            .map { position -> RectangleArea.createDefault(position = position, number = 0, isFake = true, drawBackground = true) }
            .toMutableList()

    if (setUpRectangleSizes) {
        setUpRectangleSizes(rectangles)
    }

    return rectangles
}

internal fun getIndexForRowColumn(row: Int, column: Int, gameSize: GameSize) =
        ((row * gameSize.countRowsAndColumns) + column)

internal fun getRowForIndex(index: Int, gameSize: GameSize): Int = (index / gameSize.countRowsAndColumns)

internal fun getColumnForIndex(index: Int, gameSize: GameSize): Int = (index % gameSize.countRowsAndColumns)

internal fun getRectangleForIndex(index: Int, gameSize: GameSize): Int {
    val countBigRowsAndColumns: Int = sqrt(gameSize.countRowsAndColumns.toDouble()).toInt()

    val bigRow: Int = getRowForIndex(index, gameSize) / countBigRowsAndColumns
    val bigColumn: Int = getColumnForIndex(index, gameSize) / countBigRowsAndColumns
    return ((bigRow * countBigRowsAndColumns) + bigColumn)
}

internal fun isIndexInRow(index: Int, row: Int, gameSize: GameSize): Boolean =
        (getRowForIndex(index = index, gameSize = gameSize) == row)

internal fun isIndexInColumn(index: Int, column: Int, gameSize: GameSize): Boolean =
        (getColumnForIndex(index = index, gameSize = gameSize) == column)

internal fun isIndexInRectangle(index: Int, rectangle: Int, gameSize: GameSize): Boolean =
        (getRectangleForIndex(index = index, gameSize = gameSize) == rectangle)

internal fun getIndexesForRow(row: Int, gameSize: GameSize): List<Int> {
    val columnsIndexes = (0 until gameSize.countRowsAndColumns)

    return columnsIndexes.mapIndexed { indexOfColumn, column ->
        getIndexForRowColumn(row = row, column = column, gameSize = gameSize)
    }
}

internal fun getIndexesForColumn(column: Int, gameSize: GameSize): List<Int> {
    val rowsIndexes = (0 until gameSize.countRowsAndColumns)

    return rowsIndexes.mapIndexed { indexOfRow, row ->
        getIndexForRowColumn(row = row, column = column, gameSize = gameSize)
    }
}

internal fun getIndexesForRectangle(rectangle: Int, gameSize: GameSize): List<Int> {
    val countBigRowsAndColumns: Int = sqrt(gameSize.countRowsAndColumns.toDouble()).toInt()

    val bigRow: Int = (rectangle / countBigRowsAndColumns)
    val bigColumn: Int = (rectangle % countBigRowsAndColumns)
    val elementsOffset: Int = ((bigRow * countBigRowsAndColumns * gameSize.countRowsAndColumns)
                               + (bigColumn * countBigRowsAndColumns))

    val result = mutableListOf<Int>()

    for (row in 0 until countBigRowsAndColumns) {
        for (column in 0 until countBigRowsAndColumns) {
            result.add(elementsOffset + (gameSize.countRowsAndColumns * row) + column)
        }
    }

    return result
}

internal fun generateRandomRectangleForRow(row: Int, gameSize: GameSize): Int {
    val intersectIndex = getIndexesForRow(row = row, gameSize = gameSize)
            .shuffled()
            .first()

    return getRectangleForIndex(index = intersectIndex, gameSize = gameSize)
}

internal fun generateRandomRectangleForColumn(column: Int, gameSize: GameSize): Int {
    val intersectIndex = getIndexesForColumn(column = column, gameSize = gameSize)
            .shuffled()
            .first()

    return getRectangleForIndex(index = intersectIndex, gameSize = gameSize)
}

internal fun createEmptyItemsMatrixFor(
        gameSize: GameSize, setUpRectangleSizes: Boolean,
        allItemsFilledListener: (matrix: ImmutableNumbersMatrix) -> Unit = createFakeAllItemsFilledListener(),
        totalSumChangedListener: TotalSumChangedListener = createFakeTotalSumChangedListener()
): ItemsMatrix {
    val emptyItemsMatrix = ItemsMatrix(
            countRowsAndColumns = gameSize.countRowsAndColumns,
            allItemsFilledListener = allItemsFilledListener,
            totalSumChangedListener = totalSumChangedListener
    )

    if (setUpRectangleSizes) {
        setUpRectangleSizes(emptyItemsMatrix)
    }

    return emptyItemsMatrix
}

internal fun setUpRectangleSizes(itemsMatrix: ItemsMatrix) = setUpRectangleSizes(itemsMatrix.items)

internal fun setUpRectangleSizes(rectangles: List<RectangleArea>) {
    val defaultOffsetX: Float = 100f
    val defaultOffsetY: Float = 120f
    val diff: Float = 1f

    val countRowsAndColumns = sqrt(rectangles.size.toDouble()).toInt()

    for (row in 0 until countRowsAndColumns) {
        for (column in 0 until countRowsAndColumns) {

            val rectangle = rectangles[(row * countRowsAndColumns) + column]

            rectangle.leftX = (defaultOffsetX + ((TEST_CELL_SIZE + diff) * column))
            rectangle.topY = (defaultOffsetY + ((TEST_CELL_SIZE + diff) * row))
            rectangle.size = TEST_CELL_SIZE
        }
    }
}

internal fun assertForImmutableMatrix(assert: (ImmutableNumbersMatrix, ItemsMatrix, GameSize) -> Unit) {
    GameSize.values().forEach { size ->

        val itemsMatrix = ItemsMatrix(
                countRowsAndColumns = size.countRowsAndColumns,
                allItemsFilledListener = createFakeAllItemsFilledListener(),
                totalSumChangedListener = createFakeTotalSumChangedListener())

        val defaultImmutableNumbersMatrix = ImmutableNumbersMatrix.emptyForSize(size)

        assert(defaultImmutableNumbersMatrix, itemsMatrix, size)

        itemsMatrix.updateFrom(defaultImmutableNumbersMatrix)
        assert(defaultImmutableNumbersMatrix, itemsMatrix, size)

        repeat(100) { _ ->
            val nextImmutableNumbersMatrix = generateFakeImmutableNumbersMatrix(
                    gameSize = size,
                    emptyRow = null,
                    emptyColumn = null,
                    emptyRectangle = null
            )
            itemsMatrix.updateFrom(nextImmutableNumbersMatrix)
            assert(nextImmutableNumbersMatrix, itemsMatrix, size)
        }
    }
}