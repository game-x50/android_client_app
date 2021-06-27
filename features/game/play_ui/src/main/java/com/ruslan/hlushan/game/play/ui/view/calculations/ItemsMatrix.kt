package com.ruslan.hlushan.game.play.ui.view.calculations

import com.ruslan.hlushan.game.core.api.play.dto.GameSize
import com.ruslan.hlushan.game.core.api.play.dto.ImmutableNumbersMatrix
import com.ruslan.hlushan.game.play.ui.view.COMBO_SUMS
import com.ruslan.hlushan.game.play.ui.view.RectangleArea
import com.ruslan.hlushan.game.play.ui.view.centerX
import com.ruslan.hlushan.game.play.ui.view.centerY
import com.ruslan.hlushan.game.play.ui.view.containsInclusive
import com.ruslan.hlushan.game.play.ui.view.convertToFake
import com.ruslan.hlushan.game.play.ui.view.listeners.TotalSumChangedListener
import kotlin.math.sqrt

/**
 * @author Ruslan Hlushan on 8/31/18.
 */

internal class ItemsMatrix(
        val countRowsAndColumns: Int,
        private val allItemsFilledListener: (matrix: ImmutableNumbersMatrix) -> Unit,
        private val totalSumChangedListener: TotalSumChangedListener
) {

    val countBigRowsAndColumns: Int = sqrt(countRowsAndColumns.toDouble()).toInt()

    val items: List<RectangleArea> =
            (0 until (countRowsAndColumns * countRowsAndColumns))
                    .map { pos ->
                        RectangleArea.createDefault(position = pos, number = 0, isFake = true, drawBackground = true)
                    }
                    .toMutableList()

    val rowSums: List<RectangleArea> =
            (0 until countRowsAndColumns)
                    .map { pos ->
                        RectangleArea.createDefault(position = pos, number = 0, isFake = true, drawBackground = true)
                    }
                    .toMutableList()

    val columnSums: List<RectangleArea> =
            (0 until countRowsAndColumns)
                    .map { pos ->
                        RectangleArea.createDefault(position = pos, number = 0, isFake = true, drawBackground = true)
                    }
                    .toMutableList()

    val rectanglesSums: MutableList<RectangleArea> =
            (0 until countRowsAndColumns)
                    .map { pos ->
                        RectangleArea.createDefault(position = pos, number = 0, isFake = true, drawBackground = false)
                    }
                    .toMutableList()

    fun replaceFakeWith(rectangleArea: RectangleArea): Boolean {
        val index = items.indexOfFirst { item ->
            (item.isFake
             && item.containsInclusive(x = rectangleArea.centerX, y = rectangleArea.centerY))
        }

        return if (index in items.indices) {
            val neededItem = items[index]
            neededItem.isFake = false
            neededItem.number = rectangleArea.number

            recalculateChangedSums(index)
            recalculateIfThereAreCombos(index)
            true
        } else {
            false
        }
    }

    @SuppressWarnings("ComplexMethod")
    private fun recalculateIfThereAreCombos(wasChangedIndex: Int, initCombo: Int = 0) {
        var combo: Int = initCombo

        val actions: MutableList<(() -> Unit)> = mutableListOf()

        val changedRowIndex: Int = getChangedRow(wasChangedIndex)
        val changedRowSum: Int = rowSums[changedRowIndex].number
        val wasRowCombo: Boolean = (COMBO_SUMS.contains(changedRowSum)
                                    && (getNonFakeRowElements(changedRowIndex).size == countRowsAndColumns))
        if (wasRowCombo) {
            actions.add { getRow(changedRowIndex).forEach { area -> area.convertToFake() } }
            combo++
        }

        val changedColumnIndex: Int = getChangedColumn(wasChangedIndex)
        val changedColumnSum: Int = columnSums[changedColumnIndex].number
        val wasColumnCombo: Boolean = (COMBO_SUMS.contains(changedColumnSum)
                                       && (getNonFakeColumnElements(changedColumnIndex).size == countRowsAndColumns))
        if (wasColumnCombo) {
            actions.add { getColumn(changedColumnIndex).forEach { area -> area.convertToFake() } }
            combo++
        }

        val changedRectangleIndex: Int = getChangedRectangle(wasChangedIndex)
        val changedRectangleSum: Int = rectanglesSums[changedRectangleIndex].number
        @Suppress("MaxLineLength")
        val wasRectangleCombo: Boolean = (COMBO_SUMS.contains(changedRectangleSum)
                                          && (getNonFakeRectangleElements(changedRectangleIndex).size == countRowsAndColumns))
        if (wasRectangleCombo) {
            actions.add { getRectangle(changedRectangleIndex).forEach { area -> area.convertToFake() } }
            combo++
        }

        if (combo > initCombo) {
            actions.forEach { action -> action() }

            val changedItemSums: List<Int> = listOf(changedRowSum, changedColumnSum, changedRectangleSum)
            val changedItemComboSums: List<Int> = COMBO_SUMS.filter { comboSum -> changedItemSums.contains(comboSum) }
            val newNumberIndexInCombos: Int = (COMBO_SUMS.indexOf(changedItemComboSums.maxOrNull()!!) + combo - 1)

            val wasChangedItem = items[wasChangedIndex]
            wasChangedItem.isFake = false
            wasChangedItem.number = COMBO_SUMS[newNumberIndexInCombos]

            recalculateAllSums()

            if ((combo == 1) && !wasRectangleCombo) {
                recalculateIfThereAreCombos(wasChangedIndex = wasChangedIndex, initCombo = combo)
            } else {
                onSumChanged()
            }
        } else {
            onSumChanged()
        }
    }

    private fun onSumChanged() {
        if (isAllItemsFilled) {
            allItemsFilledListener(toImmutableNumbersMatrix())
        } else {
            totalSumChangedListener.onTotalSumChanged(totalSum)
        }
    }
}

internal val ItemsMatrix.notFakeItems: List<RectangleArea> get() = this.items.filter { area -> !area.isFake }

internal val ItemsMatrix.isAllItemsFilled: Boolean get() = this.items.all { area -> !area.isFake }

private val ItemsMatrix.totalSum: Int get() = this.rowSums.sumOf { area -> area.number }

internal fun ItemsMatrix.updateFrom(immutableNumbersMatrix: ImmutableNumbersMatrix) {
    immutableNumbersMatrix.numbers.forEachIndexed { index, n ->
        val neededItem = this.items[index]
        neededItem.isFake = (n == null)
        neededItem.number = (n ?: 0)
    }
    recalculateAllSums()
}

internal fun ItemsMatrix.toImmutableNumbersMatrix(): ImmutableNumbersMatrix =
        ImmutableNumbersMatrix(
                this.items.map { area ->
                    if (area.isFake) {
                        null
                    } else {
                        area.number
                    }
                },
                GameSize.fromCountRowsAndColumns(this.countRowsAndColumns),
                this.totalSum
        )

internal fun ItemsMatrix.getRow(row: Int): List<RectangleArea> =
        if (row in 0..this.countRowsAndColumns) {
            val result = ArrayList<RectangleArea>(this.countBigRowsAndColumns)

            val firstIndexOfRow = (row * this.countRowsAndColumns)
            for (column in 0 until this.countRowsAndColumns) {
                result.add(this.items[firstIndexOfRow + column])
            }
            result
        } else {
            emptyList()
        }

internal fun ItemsMatrix.getColumn(column: Int): List<RectangleArea> =
        if (column in 0..this.countRowsAndColumns) {
            val result = ArrayList<RectangleArea>(this.countBigRowsAndColumns)

            for (row in 0 until this.countRowsAndColumns) {
                result.add(this.items[(row * this.countRowsAndColumns) + column])
            }

            result
        } else {
            emptyList()
        }

private fun ItemsMatrix.getRectangle(rectangle: Int): List<RectangleArea> =
        if (rectangle in 0..this.countRowsAndColumns) {
            val bigRow: Int = (rectangle / this.countBigRowsAndColumns)
            val bigColumn: Int = (rectangle % this.countBigRowsAndColumns)
            val elementsOffset: Int = ((bigRow * this.countBigRowsAndColumns * this.countRowsAndColumns)
                                       + (bigColumn * this.countBigRowsAndColumns))

            val result = ArrayList<RectangleArea>(this.countBigRowsAndColumns)

            for (row in 0 until this.countBigRowsAndColumns) {
                for (column in 0 until this.countBigRowsAndColumns) {
                    result.add(this.items[elementsOffset + (this.countRowsAndColumns * row) + column])
                }
            }

            result
        } else {
            emptyList()
        }

private fun ItemsMatrix.recalculateChangedSums(wasChangedIndex: Int) {
    recalculateChangedRowSum(wasChangedIndex)
    recalculateChangedColumnSum(wasChangedIndex)
    recalculateChangedRectangleSum(wasChangedIndex)
}

private fun ItemsMatrix.recalculateAllSums() {
    for (i in 0 until this.countRowsAndColumns) {
        val newSumRowI: Int = getNonFakeRowElements(i).sumOf { area -> area.number }
        this.rowSums[i].number = newSumRowI

        val newSumColumnI: Int = getNonFakeColumnElements(i).sumOf { area -> area.number }
        this.columnSums[i].number = newSumColumnI

        val newSumRectangleI: Int = getNonFakeRectangleElements(i).sumOf { area -> area.number }
        this.rectanglesSums[i].number = newSumRectangleI
    }
}

private fun ItemsMatrix.recalculateChangedRowSum(wasChangedIndex: Int) {
    val rowToRecalculate: Int = getChangedRow(wasChangedIndex)
    if (rowToRecalculate in this.rowSums.indices) {
        val newSum = getNonFakeRowElements(rowToRecalculate).sumOf { area -> area.number }
        this.rowSums[rowToRecalculate].number = newSum
    }
}

private fun ItemsMatrix.recalculateChangedColumnSum(wasChangedIndex: Int) {
    val columnToRecalculate: Int = getChangedColumn(wasChangedIndex)
    if (columnToRecalculate in this.columnSums.indices) {
        val newSum = getNonFakeColumnElements(columnToRecalculate).sumOf { area -> area.number }
        this.columnSums[columnToRecalculate].number = newSum
    }
}

private fun ItemsMatrix.recalculateChangedRectangleSum(wasChangedIndex: Int) {
    val rectangleToRecalculate: Int = getChangedRectangle(wasChangedIndex)
    if (rectangleToRecalculate in this.rectanglesSums.indices) {
        val newSum = getNonFakeRectangleElements(rectangleToRecalculate).sumOf { area -> area.number }
        this.rectanglesSums[rectangleToRecalculate].number = newSum
    }
}

private fun ItemsMatrix.getChangedRow(wasChangedIndex: Int): Int = (wasChangedIndex / this.countRowsAndColumns)

private fun ItemsMatrix.getChangedColumn(wasChangedIndex: Int): Int = (wasChangedIndex % this.countRowsAndColumns)

private fun ItemsMatrix.getChangedRectangle(wasChangedIndex: Int): Int {
    val bigRow: Int = (getChangedRow(wasChangedIndex) / this.countBigRowsAndColumns)
    val bigColumn: Int = (getChangedColumn(wasChangedIndex) / this.countBigRowsAndColumns)
    return ((bigRow * this.countBigRowsAndColumns) + bigColumn)
}

private fun ItemsMatrix.getNonFakeRowElements(row: Int): List<RectangleArea> =
        getRow(row).filter { area -> !area.isFake }

private fun ItemsMatrix.getNonFakeColumnElements(column: Int): List<RectangleArea> =
        getColumn(column).filter { area -> !area.isFake }

private fun ItemsMatrix.getNonFakeRectangleElements(rectangle: Int): List<RectangleArea> =
        getRectangle(rectangle).filter { area -> !area.isFake }