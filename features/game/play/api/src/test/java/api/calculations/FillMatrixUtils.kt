package api.calculations

import api.getIndexForRowColumn
import api.getIndexesForColumn
import api.getIndexesForRectangle
import api.getIndexesForRow
import api.isIndexInColumn
import api.isIndexInRow
import com.ruslan.hlushan.core.extensions.removeFirst
import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.play.api.calculations.ItemsMatrix
import com.ruslan.hlushan.test.utils.generateFakePositiveInt

internal fun fillRow(row: Int, gameSize: GameSize, itemsMatrix: ItemsMatrix, rowNumbers: List<Int>): List<Int> {
    val cellsInMatrixIndexes = getIndexesForRow(row = row, gameSize = gameSize)
            .shuffled()

    cellsInMatrixIndexes.forEachIndexed { indexOfCellInColumn, indexOfCellInMatrix ->
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = rowNumbers[indexOfCellInColumn],
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }
    return cellsInMatrixIndexes
}

internal fun fillColumn(
        column: Int,
        gameSize: GameSize,
        itemsMatrix: ItemsMatrix,
        columnNumbers: List<Int>
): List<Int> {
    val cellsInMatrixIndexes = getIndexesForColumn(column = column, gameSize = gameSize)
            .shuffled()

    cellsInMatrixIndexes.forEachIndexed { indexOfCellInColumn, indexOfCellInMatrix ->
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = columnNumbers[indexOfCellInColumn],
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }
    return cellsInMatrixIndexes
}

internal fun fillRectangle(
        rectangle: Int,
        gameSize: GameSize,
        itemsMatrix: ItemsMatrix,
        rectangleNumbers: List<Int>
): List<Int> {
    val cellsInMatrixIndexes = getIndexesForRectangle(rectangle = rectangle, gameSize = gameSize)
            .shuffled()

    cellsInMatrixIndexes.forEachIndexed { indexOfCellInRectangle, indexOfCellInMatrix ->
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = rectangleNumbers[indexOfCellInRectangle],
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }
    return cellsInMatrixIndexes
}

@SuppressWarnings("LongParameterList")
internal fun fillRowColumn(
        itemsMatrix: ItemsMatrix,
        gameSize: GameSize,
        row: Int,
        column: Int,
        rowNumbersExceptLast: List<Int>,
        columnNumbersExceptLast: List<Int>,
        intersectNumber: Int
): Int {
    val rowCellsInMatrixIndexes = getIndexesForRow(row = row, gameSize = gameSize)
            .shuffled()
            .toMutableList()
    rowCellsInMatrixIndexes.removeFirst { rowCellIndex ->
        isIndexInColumn(index = rowCellIndex, column = column, gameSize = gameSize)
    }

    val columnCellsInMatrixIndexes = getIndexesForColumn(column = column, gameSize = gameSize)
            .shuffled()
            .toMutableList()
    columnCellsInMatrixIndexes.removeFirst { columnCellIndex ->
        isIndexInRow(index = columnCellIndex, row = row, gameSize = gameSize)
    }

    rowCellsInMatrixIndexes.forEachIndexed { indexOfCellInRow, indexOfCellInMatrix ->
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = rowNumbersExceptLast[indexOfCellInRow],
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }

    columnCellsInMatrixIndexes.forEachIndexed { indexOfCellInColumn, indexOfCellInMatrix ->
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = columnNumbersExceptLast[indexOfCellInColumn],
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }

    val lastInsertIndex = getIndexForRowColumn(row = row, column = column, gameSize = gameSize)
    val lastReplace = itemsMatrix.items[lastInsertIndex].copy(
            isFake = false,
            number = intersectNumber,
            position = generateFakePositiveInt()
    )
    itemsMatrix.replaceFakeWith(lastReplace)

    return lastInsertIndex
}

@SuppressWarnings("LongParameterList")
internal fun fillRowRectangle(
        itemsMatrix: ItemsMatrix,
        gameSize: GameSize,
        row: Int,
        rectangle: Int,
        rowNumbersExceptFewLast: List<Int>,
        rectangleNumbersExceptFewLast: List<Int>,
        intersectNumbers: List<Int>
): Int {
    val allRowInMatrixIndexes = getIndexesForRow(row = row, gameSize = gameSize)
            .shuffled()

    val allRectangleInMatrixIndexes = getIndexesForRectangle(rectangle = rectangle, gameSize = gameSize)
            .shuffled()

    val rowCellsInMatrixIndexes = allRowInMatrixIndexes.minus(allRectangleInMatrixIndexes)

    val rectangleCellsInMatrixIndexes = allRectangleInMatrixIndexes.minus(allRowInMatrixIndexes)

    val intersectingCellsInMatrixIndexes = allRowInMatrixIndexes.intersect(allRectangleInMatrixIndexes)

    rowCellsInMatrixIndexes.forEachIndexed { indexOfCellInRow, indexOfCellInMatrix ->
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = rowNumbersExceptFewLast[indexOfCellInRow],
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }

    rectangleCellsInMatrixIndexes.forEachIndexed { indexOfCellInRectangle, indexOfCellInMatrix ->
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = rectangleNumbersExceptFewLast[indexOfCellInRectangle],
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }

    intersectingCellsInMatrixIndexes.forEachIndexed { indexOfCellInIntersect, indexOfCellInMatrix ->
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = intersectNumbers[indexOfCellInIntersect],
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }

    val lastInsertIndex = intersectingCellsInMatrixIndexes.last()
    return lastInsertIndex
}

@SuppressWarnings("LongParameterList")
internal fun fillColumnRectangle(
        itemsMatrix: ItemsMatrix,
        gameSize: GameSize,
        column: Int,
        rectangle: Int,
        columnNumbersExceptFewLast: List<Int>,
        rectangleNumbersExceptFewLast: List<Int>,
        intersectNumbers: List<Int>
): Int {
    val allColumnInMatrixIndexes = getIndexesForColumn(column = column, gameSize = gameSize)
            .shuffled()

    val allRectangleInMatrixIndexes = getIndexesForRectangle(rectangle = rectangle, gameSize = gameSize)
            .shuffled()

    val columnCellsInMatrixIndexes = allColumnInMatrixIndexes.minus(allRectangleInMatrixIndexes)

    val rectangleCellsInMatrixIndexes = allRectangleInMatrixIndexes.minus(allColumnInMatrixIndexes)

    val intersectingCellsInMatrixIndexes = allColumnInMatrixIndexes.intersect(allRectangleInMatrixIndexes)

    columnCellsInMatrixIndexes.forEachIndexed { indexOfCellInColumn, indexOfCellInMatrix ->
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = columnNumbersExceptFewLast[indexOfCellInColumn],
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }

    rectangleCellsInMatrixIndexes.forEachIndexed { indexOfCellInRectangle, indexOfCellInMatrix ->
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = rectangleNumbersExceptFewLast[indexOfCellInRectangle],
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }

    intersectingCellsInMatrixIndexes.forEachIndexed { indexOfCellInIntersect, indexOfCellInMatrix ->
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = intersectNumbers[indexOfCellInIntersect],
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }

    val lastInsertIndex = intersectingCellsInMatrixIndexes.last()
    return lastInsertIndex
}

@SuppressWarnings("LongParameterList", "LongMethod")
internal fun fillRowColumnRectangle(
        itemsMatrix: ItemsMatrix,
        gameSize: GameSize,
        row: Int,
        column: Int,
        rectangle: Int,
        lastInsertIndex: Int,
        rowNumbersExceptFewLast: List<Int>,
        columnNumbersExceptFewLast: List<Int>,
        rectangleNumbersExceptFewLast: List<Int>,
        intersectRowRectangleNumbersExceptLast: List<Int>,
        intersectColumnRectangleNumbersExceptLast: List<Int>,
        lastIntersectNumber: Int
) {

    val allRowInMatrixIndexes = getIndexesForRow(row = row, gameSize = gameSize)

    val allColumnInMatrixIndexes = getIndexesForColumn(column = column, gameSize = gameSize)

    val allRectangleInMatrixIndexes = getIndexesForRectangle(rectangle = rectangle, gameSize = gameSize)

    val rowCellsInMatrixIndexes = allRowInMatrixIndexes
            .minus(allColumnInMatrixIndexes)
            .minus(allRectangleInMatrixIndexes)
            .shuffled()

    val columnCellsInMatrixIndexes = allColumnInMatrixIndexes
            .minus(allRowInMatrixIndexes)
            .minus(allRectangleInMatrixIndexes)
            .shuffled()

    val rectangleCellsInMatrixIndexes = allRectangleInMatrixIndexes
            .minus(allRowInMatrixIndexes)
            .minus(allColumnInMatrixIndexes)
            .shuffled()

    val intersectRowRectangleExceptLastCellsInMatrixIndexes = allRowInMatrixIndexes
            .intersect(allRectangleInMatrixIndexes)
            .minus(lastInsertIndex)
            .shuffled()

    val intersectColumnRectangleExceptLastCellsInMatrixIndexes = allColumnInMatrixIndexes
            .intersect(allRectangleInMatrixIndexes)
            .minus(lastInsertIndex)
            .shuffled()

    rowCellsInMatrixIndexes.forEachIndexed { indexOfCellInRow, indexOfCellInMatrix ->
        val number = rowNumbersExceptFewLast[indexOfCellInRow]
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = number,
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }

    columnCellsInMatrixIndexes.forEachIndexed { indexOfCellInColumn, indexOfCellInMatrix ->
        val number = columnNumbersExceptFewLast[indexOfCellInColumn]
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = number,
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }

    rectangleCellsInMatrixIndexes.forEachIndexed { indexOfCellInRectangle, indexOfCellInMatrix ->
        val number = rectangleNumbersExceptFewLast[indexOfCellInRectangle]
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = number,
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }

    intersectRowRectangleExceptLastCellsInMatrixIndexes.forEachIndexed { indexOfCellInIntersectRowRectangle,
                                                                         indexOfCellInMatrix ->
        val number = intersectRowRectangleNumbersExceptLast[indexOfCellInIntersectRowRectangle]
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = number,
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }

    intersectColumnRectangleExceptLastCellsInMatrixIndexes.forEachIndexed { indexOfCellInIntersectColumnRectangle,
                                                                            indexOfCellInMatrix ->
        val number = intersectColumnRectangleNumbersExceptLast[indexOfCellInIntersectColumnRectangle]
        val replace = itemsMatrix.items[indexOfCellInMatrix].copy(
                isFake = false,
                number = number,
                position = generateFakePositiveInt()
        )
        itemsMatrix.replaceFakeWith(replace)
    }

    val lastReplace = itemsMatrix.items[lastInsertIndex].copy(
            isFake = false,
            number = lastIntersectNumber,
            position = generateFakePositiveInt()
    )
    itemsMatrix.replaceFakeWith(lastReplace)
}