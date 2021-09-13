package api.calculations

import api.TotalSumChangedListenerTestImpl
import api.bigColumnNumbersForMaxComboSameSumExceptFewLast
import api.bigComboNumbers
import api.bigComboNumbersFirstExceptOne
import api.bigComboNumbersFirstExceptSqrt
import api.bigComboNumbersSecondExceptOne
import api.bigComboNumbersSecondExceptSqrt
import api.bigComboNumbersSum
import api.bigDifferentSumComboResult
import api.bigExpectedSumForMaxComboSameSum
import api.bigIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast
import api.bigIntersectNumberForMaxComboSameSum
import api.bigIntersectNumberForSingleComboAndAfterAutoCombo
import api.bigIntersectNumbersForDoubleDifferentSumCombo
import api.bigIntersectOneNumberForDoubleDifferentSumCombo
import api.bigIntersectRowRectangleNumbersForMaxComboSameSumExceptLast
import api.bigNumbersForSingleComboAndAfterAutoComboFirst
import api.bigNumbersForSingleComboAndAfterAutoComboSecond
import api.bigRectangleNumbersForMaxComboSameSumExceptFewLast
import api.bigRowNumbersForMaxComboSameSumExceptFewLast
import api.bigSumForSingleComboAndAfterAutoCombo
import api.createEmptyItemsMatrixFor
import api.generateRandomRectangleForColumn
import api.generateRandomRectangleForRow
import api.getIndexForRowColumn
import api.getRectangleForIndex
import api.mediumColumnNumbersForMaxComboSameSumExceptFewLast
import api.mediumComboNumbers
import api.mediumComboNumbersFirstExceptOne
import api.mediumComboNumbersFirstExceptSqrt
import api.mediumComboNumbersSecondExceptOne
import api.mediumComboNumbersSecondExceptSqrt
import api.mediumComboNumbersSum
import api.mediumDifferentSumComboResult
import api.mediumExpectedSumForMaxComboSameSum
import api.mediumIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast
import api.mediumIntersectNumberForMaxComboSameSum
import api.mediumIntersectNumberForSingleComboAndAfterAutoCombo
import api.mediumIntersectNumbersForDoubleDifferentSumCombo
import api.mediumIntersectOneNumberForDoubleDifferentSumCombo
import api.mediumIntersectRowRectangleNumbersForMaxComboSameSumExceptLast
import api.mediumNumbersForSingleComboAndAfterAutoComboFirst
import api.mediumNumbersForSingleComboAndAfterAutoComboSecond
import api.mediumRectangleNumbersForMaxComboSameSumExceptFewLast
import api.mediumRowNumbersForMaxComboSameSumExceptFewLast
import api.mediumSumForSingleComboAndAfterAutoCombo
import api.smallColumnNumbersForMaxComboSameSumExceptFewLast
import api.smallComboNumbers
import api.smallComboNumbersFirstExceptOne
import api.smallComboNumbersFirstExceptSqrt
import api.smallComboNumbersSecondExceptOne
import api.smallComboNumbersSecondExceptSqrt
import api.smallComboNumbersSum
import api.smallDifferentSumComboResult
import api.smallExpectedSumForMaxComboSameSum
import api.smallIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast
import api.smallIntersectNumberForMaxComboSameSum
import api.smallIntersectNumberForSingleComboAndAfterAutoCombo
import api.smallIntersectNumbersForDoubleDifferentSumCombo
import api.smallIntersectOneNumberForDoubleDifferentSumCombo
import api.smallIntersectRowRectangleNumbersForMaxComboSameSumExceptLast
import api.smallNumbersForSingleComboAndAfterAutoComboFirst
import api.smallNumbersForSingleComboAndAfterAutoComboSecond
import api.smallRectangleNumbersForMaxComboSameSumExceptFewLast
import api.smallRowNumbersForMaxComboSameSumExceptFewLast
import api.smallSumForSingleComboAndAfterAutoCombo
import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.api.play.dto.ImmutableNumbersMatrix
import com.ruslan.hlushan.game.play.api.calculations.toImmutableNumbersMatrix
import com.ruslan.hlushan.test.utils.generateFakePositiveInt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@SuppressWarnings("LargeClass")
class ItemsMatrixListenersTest {

    @Test
    fun listeners() {

        val magicNumber = 7

        GameSize.values().forEach { gameSize ->

            val totalSumChangedListener = TotalSumChangedListenerTestImpl()

            var called: ImmutableNumbersMatrix? = null

            val listener: (matrix: ImmutableNumbersMatrix) -> Unit = { result ->
                called = result
            }

            val itemsMatrix = createEmptyItemsMatrixFor(
                    gameSize = gameSize,
                    setUpRectangleSizes = true,
                    allItemsFilledListener = listener,
                    totalSumChangedListener = totalSumChangedListener
            )

            itemsMatrix.items
                    .dropLast(1)
                    .forEach { emptyArea ->
                        itemsMatrix.replaceFakeWith(emptyArea.copy(number = magicNumber,
                                                                   position = generateFakePositiveInt()))

                        totalSumChangedListener.addNextExpectedNumber(magicNumber)
                    }

            assertNull(called)

            val last = itemsMatrix.items.last()
            itemsMatrix.replaceFakeWith(last.copy(number = magicNumber,
                                                  position = generateFakePositiveInt()))

            assertEquals(itemsMatrix.toImmutableNumbersMatrix(), called)
            totalSumChangedListener.assert()
        }
    }

    @Test
    fun rowCombo_SMALL() =
            rowCombo(
                    gameSize = GameSize.SMALL,
                    rowNumbers = smallComboNumbers(),
                    comboSum = smallComboNumbersSum()
            )

    @Test
    fun rowCombo_MEDIUM() =
            rowCombo(
                    gameSize = GameSize.MEDIUM,
                    rowNumbers = mediumComboNumbers(),
                    comboSum = mediumComboNumbersSum()
            )

    @Test
    fun rowCombo_BIG() =
            rowCombo(
                    gameSize = GameSize.BIG,
                    rowNumbers = bigComboNumbers(),
                    comboSum = bigComboNumbersSum()
            )

    @Test
    fun columnCombo_SMALL() =
            columnCombo(
                    gameSize = GameSize.SMALL,
                    columnNumbers = smallComboNumbers(),
                    comboSum = smallComboNumbersSum()
            )

    @Test
    fun columnCombo_MEDIUM() =
            columnCombo(
                    gameSize = GameSize.MEDIUM,
                    columnNumbers = mediumComboNumbers(),
                    comboSum = mediumComboNumbersSum()
            )

    @Test
    fun columnCombo_BIG() =
            columnCombo(
                    gameSize = GameSize.BIG,
                    columnNumbers = bigComboNumbers(),
                    comboSum = bigComboNumbersSum()
            )

    @Test
    fun rectangleCombo_SMALL() =
            rectangleCombo(
                    gameSize = GameSize.SMALL,
                    rectangleNumbers = smallComboNumbers(),
                    comboSum = smallComboNumbersSum()
            )

    @Test
    fun rectangleCombo_MEDIUM() =
            rectangleCombo(
                    gameSize = GameSize.MEDIUM,
                    rectangleNumbers = mediumComboNumbers(),
                    comboSum = mediumComboNumbersSum()
            )

    @Test
    fun rectangleCombo_BIG() =
            rectangleCombo(
                    gameSize = GameSize.BIG,
                    rectangleNumbers = bigComboNumbers(),
                    comboSum = bigComboNumbersSum()
            )

    @Test
    fun rowColumnCombo_SMALL() =
            rowColumnCombo(
                    gameSize = GameSize.SMALL,
                    rowNumbersExceptLast = smallComboNumbersFirstExceptOne(),
                    columnNumbersExceptLast = smallComboNumbersSecondExceptOne(),
                    intersectNumber = smallIntersectOneNumberForDoubleDifferentSumCombo(),
                    comboSum = smallDifferentSumComboResult()
            )

    @Test
    fun rowColumnCombo_MEDIUM() =
            rowColumnCombo(
                    gameSize = GameSize.MEDIUM,
                    rowNumbersExceptLast = mediumComboNumbersFirstExceptOne(),
                    columnNumbersExceptLast = mediumComboNumbersSecondExceptOne(),
                    intersectNumber = mediumIntersectOneNumberForDoubleDifferentSumCombo(),
                    comboSum = mediumDifferentSumComboResult()
            )

    @Test
    fun rowColumnCombo_BIG() =
            rowColumnCombo(
                    gameSize = GameSize.BIG,
                    rowNumbersExceptLast = bigComboNumbersFirstExceptOne(),
                    columnNumbersExceptLast = bigComboNumbersSecondExceptOne(),
                    intersectNumber = bigIntersectOneNumberForDoubleDifferentSumCombo(),
                    comboSum = bigDifferentSumComboResult()
            )

    @Test
    fun rowRectangleCombo_SMALL() =
            rowRectangleCombo(
                    gameSize = GameSize.SMALL,
                    rowNumbersExceptFewLast = smallComboNumbersFirstExceptSqrt(),
                    rectangleNumbersExceptFewLast = smallComboNumbersSecondExceptSqrt(),
                    intersectNumbers = smallIntersectNumbersForDoubleDifferentSumCombo(),
                    comboSum = smallDifferentSumComboResult()
            )

    @Test
    fun rowRectangleCombo_MEDIUM() =
            rowRectangleCombo(
                    gameSize = GameSize.MEDIUM,
                    rowNumbersExceptFewLast = mediumComboNumbersFirstExceptSqrt(),
                    rectangleNumbersExceptFewLast = mediumComboNumbersSecondExceptSqrt(),
                    intersectNumbers = mediumIntersectNumbersForDoubleDifferentSumCombo(),
                    comboSum = mediumDifferentSumComboResult()
            )

    @Test
    fun rowRectangleCombo_BIG() =
            rowRectangleCombo(
                    gameSize = GameSize.BIG,
                    rowNumbersExceptFewLast = bigComboNumbersFirstExceptSqrt(),
                    rectangleNumbersExceptFewLast = bigComboNumbersSecondExceptSqrt(),
                    intersectNumbers = bigIntersectNumbersForDoubleDifferentSumCombo(),
                    comboSum = bigDifferentSumComboResult()
            )

    @Test
    fun columnRectangleCombo_SMALL() =
            columnRectangleCombo(
                    gameSize = GameSize.SMALL,
                    columnNumbersExceptFewLast = smallComboNumbersFirstExceptSqrt(),
                    rectangleNumbersExceptFewLast = smallComboNumbersSecondExceptSqrt(),
                    intersectNumbers = smallIntersectNumbersForDoubleDifferentSumCombo(),
                    comboSum = smallDifferentSumComboResult()
            )

    @Test
    fun columnRectangleCombo_MEDIUM() =
            columnRectangleCombo(
                    gameSize = GameSize.MEDIUM,
                    columnNumbersExceptFewLast = mediumComboNumbersFirstExceptSqrt(),
                    rectangleNumbersExceptFewLast = mediumComboNumbersSecondExceptSqrt(),
                    intersectNumbers = mediumIntersectNumbersForDoubleDifferentSumCombo(),
                    comboSum = mediumDifferentSumComboResult()
            )

    @Test
    fun columnRectangleCombo_BIG() =
            columnRectangleCombo(
                    gameSize = GameSize.BIG,
                    columnNumbersExceptFewLast = bigComboNumbersFirstExceptSqrt(),
                    rectangleNumbersExceptFewLast = bigComboNumbersSecondExceptSqrt(),
                    intersectNumbers = bigIntersectNumbersForDoubleDifferentSumCombo(),
                    comboSum = bigDifferentSumComboResult()
            )

    @Suppress("MaxLineLength")
    @Test
    fun rowColumnRectangleCombo_SMALL() =
            rowColumnRectangleCombo(
                    gameSize = GameSize.SMALL,
                    rowNumbersExceptFewLast = smallRowNumbersForMaxComboSameSumExceptFewLast(),
                    columnNumbersExceptFewLast = smallColumnNumbersForMaxComboSameSumExceptFewLast(),
                    rectangleNumbersExceptFewLast = smallRectangleNumbersForMaxComboSameSumExceptFewLast(),
                    intersectRowRectangleNumbersExceptLast = smallIntersectRowRectangleNumbersForMaxComboSameSumExceptLast(),
                    intersectColumnRectangleNumbersExceptLast = smallIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast(),
                    lastIntersectNumber = smallIntersectNumberForMaxComboSameSum(),
                    comboSum = smallExpectedSumForMaxComboSameSum()
            )

    @Suppress("MaxLineLength")
    @Test
    fun rowColumnRectangleCombo_MEDIUM() =
            rowColumnRectangleCombo(
                    gameSize = GameSize.MEDIUM,
                    rowNumbersExceptFewLast = mediumRowNumbersForMaxComboSameSumExceptFewLast(),
                    columnNumbersExceptFewLast = mediumColumnNumbersForMaxComboSameSumExceptFewLast(),
                    rectangleNumbersExceptFewLast = mediumRectangleNumbersForMaxComboSameSumExceptFewLast(),
                    intersectRowRectangleNumbersExceptLast = mediumIntersectRowRectangleNumbersForMaxComboSameSumExceptLast(),
                    intersectColumnRectangleNumbersExceptLast = mediumIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast(),
                    lastIntersectNumber = mediumIntersectNumberForMaxComboSameSum(),
                    comboSum = mediumExpectedSumForMaxComboSameSum()
            )

    @Suppress("MaxLineLength")
    @Test
    fun rowColumnRectangleCombo_BIG() =
            rowColumnRectangleCombo(
                    gameSize = GameSize.BIG,
                    rowNumbersExceptFewLast = bigRowNumbersForMaxComboSameSumExceptFewLast(),
                    columnNumbersExceptFewLast = bigColumnNumbersForMaxComboSameSumExceptFewLast(),
                    rectangleNumbersExceptFewLast = bigRectangleNumbersForMaxComboSameSumExceptFewLast(),
                    intersectRowRectangleNumbersExceptLast = bigIntersectRowRectangleNumbersForMaxComboSameSumExceptLast(),
                    intersectColumnRectangleNumbersExceptLast = bigIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast(),
                    lastIntersectNumber = bigIntersectNumberForMaxComboSameSum(),
                    comboSum = bigExpectedSumForMaxComboSameSum()
            )

    @Test
    fun singleComboRowAndAfterAutoComboThisColumn_SMALL() =
            rowColumnCombo(
                    gameSize = GameSize.SMALL,
                    rowNumbersExceptLast = smallNumbersForSingleComboAndAfterAutoComboFirst(),
                    columnNumbersExceptLast = smallNumbersForSingleComboAndAfterAutoComboSecond(),
                    intersectNumber = smallIntersectNumberForSingleComboAndAfterAutoCombo(),
                    comboSum = smallSumForSingleComboAndAfterAutoCombo()
            )

    @Test
    fun singleComboRowAndAfterAutoComboThisColumn_MEDIUM() =
            rowColumnCombo(
                    gameSize = GameSize.MEDIUM,
                    rowNumbersExceptLast = mediumNumbersForSingleComboAndAfterAutoComboFirst(),
                    columnNumbersExceptLast = mediumNumbersForSingleComboAndAfterAutoComboSecond(),
                    intersectNumber = mediumIntersectNumberForSingleComboAndAfterAutoCombo(),
                    comboSum = mediumSumForSingleComboAndAfterAutoCombo()
            )

    @Test
    fun singleComboRowAndAfterAutoComboThisColumn_BIG() =
            rowColumnCombo(
                    gameSize = GameSize.BIG,
                    rowNumbersExceptLast = bigNumbersForSingleComboAndAfterAutoComboFirst(),
                    columnNumbersExceptLast = bigNumbersForSingleComboAndAfterAutoComboSecond(),
                    intersectNumber = bigIntersectNumberForSingleComboAndAfterAutoCombo(),
                    comboSum = bigSumForSingleComboAndAfterAutoCombo()
            )

    @Test
    fun singleComboColumnAndAfterAutoComboThisRow_SMALL() =
            rowColumnCombo(
                    gameSize = GameSize.SMALL,
                    rowNumbersExceptLast = smallNumbersForSingleComboAndAfterAutoComboSecond(),
                    columnNumbersExceptLast = smallNumbersForSingleComboAndAfterAutoComboFirst(),
                    intersectNumber = smallIntersectNumberForSingleComboAndAfterAutoCombo(),
                    comboSum = smallSumForSingleComboAndAfterAutoCombo()
            )

    @Test
    fun singleComboColumnAndAfterAutoComboThisRow_MEDIUM() =
            rowColumnCombo(
                    gameSize = GameSize.MEDIUM,
                    rowNumbersExceptLast = mediumNumbersForSingleComboAndAfterAutoComboSecond(),
                    columnNumbersExceptLast = mediumNumbersForSingleComboAndAfterAutoComboFirst(),
                    intersectNumber = mediumIntersectNumberForSingleComboAndAfterAutoCombo(),
                    comboSum = mediumSumForSingleComboAndAfterAutoCombo()
            )

    @Test
    fun singleComboColumnAndAfterAutoComboThisRow_BIG() =
            rowColumnCombo(
                    gameSize = GameSize.BIG,
                    rowNumbersExceptLast = bigNumbersForSingleComboAndAfterAutoComboSecond(),
                    columnNumbersExceptLast = bigNumbersForSingleComboAndAfterAutoComboFirst(),
                    intersectNumber = bigIntersectNumberForSingleComboAndAfterAutoCombo(),
                    comboSum = bigSumForSingleComboAndAfterAutoCombo()
            )

    private fun rowCombo(gameSize: GameSize, rowNumbers: List<Int>, comboSum: Int) {
        var called: ImmutableNumbersMatrix? = null

        val listener: (matrix: ImmutableNumbersMatrix) -> Unit = { result ->
            called = result
        }

        val totalSumChangedListener = TotalSumChangedListenerTestImpl()

        val itemsMatrix = createEmptyItemsMatrixFor(
                gameSize = gameSize,
                setUpRectangleSizes = true,
                allItemsFilledListener = listener,
                totalSumChangedListener = totalSumChangedListener
        )

        val row = (generateFakePositiveInt() % gameSize.countRowsAndColumns)

        fillRow(row = row, gameSize = gameSize, itemsMatrix = itemsMatrix, rowNumbers = rowNumbers)

        totalSumChangedListener.addNumbersForCombo(numbersBeforeCombo = rowNumbers.dropLast(1), finalCombo = comboSum)

        assertNull(called)
        totalSumChangedListener.assert()
    }

    private fun columnCombo(gameSize: GameSize, columnNumbers: List<Int>, comboSum: Int) {
        var called: ImmutableNumbersMatrix? = null

        val listener: (matrix: ImmutableNumbersMatrix) -> Unit = { result ->
            called = result
        }

        val totalSumChangedListener = TotalSumChangedListenerTestImpl()

        val itemsMatrix = createEmptyItemsMatrixFor(
                gameSize = gameSize,
                setUpRectangleSizes = true,
                allItemsFilledListener = listener,
                totalSumChangedListener = totalSumChangedListener
        )

        val column = (generateFakePositiveInt() % gameSize.countRowsAndColumns)

        fillColumn(column = column, gameSize = gameSize, itemsMatrix = itemsMatrix, columnNumbers = columnNumbers)

        totalSumChangedListener.addNumbersForCombo(
                numbersBeforeCombo = columnNumbers.dropLast(1),
                finalCombo = comboSum
        )

        assertNull(called)
        totalSumChangedListener.assert()
    }

    private fun rectangleCombo(gameSize: GameSize, rectangleNumbers: List<Int>, comboSum: Int) {
        var called: ImmutableNumbersMatrix? = null

        val listener: (matrix: ImmutableNumbersMatrix) -> Unit = { result ->
            called = result
        }

        val totalSumChangedListener = TotalSumChangedListenerTestImpl()

        val itemsMatrix = createEmptyItemsMatrixFor(
                gameSize = gameSize,
                setUpRectangleSizes = true,
                allItemsFilledListener = listener,
                totalSumChangedListener = totalSumChangedListener
        )

        val rectangle = (generateFakePositiveInt() % gameSize.countRowsAndColumns)

        fillRectangle(
                rectangle = rectangle,
                gameSize = gameSize,
                itemsMatrix = itemsMatrix,
                rectangleNumbers = rectangleNumbers
        )

        totalSumChangedListener.addNumbersForCombo(
                numbersBeforeCombo = rectangleNumbers.dropLast(1),
                finalCombo = comboSum
        )

        assertNull(called)
        totalSumChangedListener.assert()
    }

    private fun rowColumnCombo(
            gameSize: GameSize,
            rowNumbersExceptLast: List<Int>,
            columnNumbersExceptLast: List<Int>,
            intersectNumber: Int,
            comboSum: Int
    ) {
        var called: ImmutableNumbersMatrix? = null

        val listener: (matrix: ImmutableNumbersMatrix) -> Unit = { result ->
            called = result
        }

        val totalSumChangedListener = TotalSumChangedListenerTestImpl()

        val itemsMatrix = createEmptyItemsMatrixFor(
                gameSize = gameSize,
                setUpRectangleSizes = true,
                allItemsFilledListener = listener,
                totalSumChangedListener = totalSumChangedListener
        )

        val row = (generateFakePositiveInt() % gameSize.countRowsAndColumns)
        val column = (generateFakePositiveInt() % gameSize.countRowsAndColumns)

        fillRowColumn(
                itemsMatrix = itemsMatrix,
                gameSize = gameSize,
                row = row,
                column = column,
                rowNumbersExceptLast = rowNumbersExceptLast,
                columnNumbersExceptLast = columnNumbersExceptLast,
                intersectNumber = intersectNumber
        )

        totalSumChangedListener.addNumbersForCombo(
                numbersBeforeCombo = (rowNumbersExceptLast + columnNumbersExceptLast),
                finalCombo = comboSum
        )

        assertNull(called)
        totalSumChangedListener.assert()
    }

    private fun rowRectangleCombo(
            gameSize: GameSize,
            rowNumbersExceptFewLast: List<Int>,
            rectangleNumbersExceptFewLast: List<Int>,
            intersectNumbers: List<Int>,
            comboSum: Int
    ) {

        var called: ImmutableNumbersMatrix? = null

        val listener: (matrix: ImmutableNumbersMatrix) -> Unit = { result ->
            called = result
        }

        val totalSumChangedListener = TotalSumChangedListenerTestImpl()

        val itemsMatrix = createEmptyItemsMatrixFor(
                gameSize = gameSize,
                setUpRectangleSizes = true,
                allItemsFilledListener = listener,
                totalSumChangedListener = totalSumChangedListener
        )

        val row = (generateFakePositiveInt() % gameSize.countRowsAndColumns)

        val rectangle = generateRandomRectangleForRow(row, gameSize)

        fillRowRectangle(
                itemsMatrix = itemsMatrix,
                gameSize = gameSize,
                row = row,
                rectangle = rectangle,
                rowNumbersExceptFewLast = rowNumbersExceptFewLast,
                rectangleNumbersExceptFewLast = rectangleNumbersExceptFewLast,
                intersectNumbers = intersectNumbers
        )

        totalSumChangedListener.addNumbersForCombo(
                numbersBeforeCombo = (rowNumbersExceptFewLast + rectangleNumbersExceptFewLast
                                      + intersectNumbers.dropLast(1)),
                finalCombo = comboSum
        )

        assertNull(called)
        totalSumChangedListener.assert()
    }

    private fun columnRectangleCombo(
            gameSize: GameSize,
            columnNumbersExceptFewLast: List<Int>,
            rectangleNumbersExceptFewLast: List<Int>,
            intersectNumbers: List<Int>,
            comboSum: Int
    ) {

        var called: ImmutableNumbersMatrix? = null

        val listener: (matrix: ImmutableNumbersMatrix) -> Unit = { result ->
            called = result
        }

        val totalSumChangedListener = TotalSumChangedListenerTestImpl()

        val itemsMatrix = createEmptyItemsMatrixFor(
                gameSize = gameSize,
                setUpRectangleSizes = true,
                allItemsFilledListener = listener,
                totalSumChangedListener = totalSumChangedListener
        )

        val column = (generateFakePositiveInt() % gameSize.countRowsAndColumns)

        val rectangle = generateRandomRectangleForColumn(column, gameSize)

        fillColumnRectangle(
                itemsMatrix = itemsMatrix,
                gameSize = gameSize,
                column = column,
                rectangle = rectangle,
                columnNumbersExceptFewLast = columnNumbersExceptFewLast,
                rectangleNumbersExceptFewLast = rectangleNumbersExceptFewLast,
                intersectNumbers = intersectNumbers)

        totalSumChangedListener.addNumbersForCombo(
                numbersBeforeCombo = (columnNumbersExceptFewLast + rectangleNumbersExceptFewLast
                                      + intersectNumbers.dropLast(1)),
                finalCombo = comboSum
        )

        assertNull(called)
        totalSumChangedListener.assert()
    }

    @SuppressWarnings("LongParameterList")
    private fun rowColumnRectangleCombo(
            gameSize: GameSize,
            rowNumbersExceptFewLast: List<Int>,
            columnNumbersExceptFewLast: List<Int>,
            rectangleNumbersExceptFewLast: List<Int>,
            intersectRowRectangleNumbersExceptLast: List<Int>,
            intersectColumnRectangleNumbersExceptLast: List<Int>,
            lastIntersectNumber: Int,
            comboSum: Int
    ) {
        var called: ImmutableNumbersMatrix? = null

        val listener: (matrix: ImmutableNumbersMatrix) -> Unit = { result ->
            called = result
        }

        val totalSumChangedListener = TotalSumChangedListenerTestImpl()

        val itemsMatrix = createEmptyItemsMatrixFor(
                gameSize = gameSize,
                setUpRectangleSizes = true,
                allItemsFilledListener = listener,
                totalSumChangedListener = totalSumChangedListener
        )

        val row = (generateFakePositiveInt() % gameSize.countRowsAndColumns)
        val column = (generateFakePositiveInt() % gameSize.countRowsAndColumns)

        val lastInsertIndex = getIndexForRowColumn(row = row, column = column, gameSize = gameSize)

        val rectangle = getRectangleForIndex(index = lastInsertIndex, gameSize = gameSize)

        fillRowColumnRectangle(
                itemsMatrix = itemsMatrix,
                gameSize = gameSize,
                row = row,
                column = column,
                rectangle = rectangle,
                lastInsertIndex = lastInsertIndex,
                rowNumbersExceptFewLast = rowNumbersExceptFewLast,
                columnNumbersExceptFewLast = columnNumbersExceptFewLast,
                rectangleNumbersExceptFewLast = rectangleNumbersExceptFewLast,
                intersectRowRectangleNumbersExceptLast = intersectRowRectangleNumbersExceptLast,
                intersectColumnRectangleNumbersExceptLast = intersectColumnRectangleNumbersExceptLast,
                lastIntersectNumber = lastIntersectNumber
        )

        @Suppress("MaxLineLength")
        totalSumChangedListener.addNumbersForCombo(
                numbersBeforeCombo = (rowNumbersExceptFewLast
                                      + columnNumbersExceptFewLast
                                      + rectangleNumbersExceptFewLast
                                      + intersectRowRectangleNumbersExceptLast
                                      + intersectColumnRectangleNumbersExceptLast),
                finalCombo = comboSum
        )

        assertNull(called)
        totalSumChangedListener.assert()
    }
}