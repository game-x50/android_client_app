
package view.calculations

import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.play.ui.view.RectangleArea
import com.ruslan.hlushan.game.play.ui.view.calculations.updateFrom
import com.ruslan.hlushan.test.utils.generateFakePositiveInt
import org.junit.Assert.assertEquals
import org.junit.Test
import view.bigColumnNumbersForMaxComboSameSumExceptFewLast
import view.bigComboNumbers
import view.bigComboNumbersFirstExceptOne
import view.bigComboNumbersFirstExceptSqrt
import view.bigComboNumbersSecondExceptOne
import view.bigComboNumbersSecondExceptSqrt
import view.bigComboNumbersSum
import view.bigDifferentSumComboResult
import view.bigExpectedSumForMaxComboSameSum
import view.bigIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast
import view.bigIntersectNumberForMaxComboSameSum
import view.bigIntersectNumberForSingleComboAndAfterAutoCombo
import view.bigIntersectNumbersForDoubleDifferentSumCombo
import view.bigIntersectOneNumberForDoubleDifferentSumCombo
import view.bigIntersectRowRectangleNumbersForMaxComboSameSumExceptLast
import view.bigNumbersForSingleComboAndAfterAutoComboFirst
import view.bigNumbersForSingleComboAndAfterAutoComboSecond
import view.bigRectangleNumbersForMaxComboSameSumExceptFewLast
import view.bigRowNumbersForMaxComboSameSumExceptFewLast
import view.bigSumForSingleComboAndAfterAutoCombo
import view.createEmptyItemsMatrixFor
import view.generateFakeImmutableNumbersMatrix
import view.generateRandomRectangleForColumn
import view.generateRandomRectangleForRow
import view.getIndexForRowColumn
import view.getRectangleForIndex
import view.mediumColumnNumbersForMaxComboSameSumExceptFewLast
import view.mediumComboNumbers
import view.mediumComboNumbersFirstExceptOne
import view.mediumComboNumbersFirstExceptSqrt
import view.mediumComboNumbersSecondExceptOne
import view.mediumComboNumbersSecondExceptSqrt
import view.mediumComboNumbersSum
import view.mediumDifferentSumComboResult
import view.mediumExpectedSumForMaxComboSameSum
import view.mediumIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast
import view.mediumIntersectNumberForMaxComboSameSum
import view.mediumIntersectNumberForSingleComboAndAfterAutoCombo
import view.mediumIntersectNumbersForDoubleDifferentSumCombo
import view.mediumIntersectOneNumberForDoubleDifferentSumCombo
import view.mediumIntersectRowRectangleNumbersForMaxComboSameSumExceptLast
import view.mediumNumbersForSingleComboAndAfterAutoComboFirst
import view.mediumNumbersForSingleComboAndAfterAutoComboSecond
import view.mediumRectangleNumbersForMaxComboSameSumExceptFewLast
import view.mediumRowNumbersForMaxComboSameSumExceptFewLast
import view.mediumSumForSingleComboAndAfterAutoCombo
import view.nextBigComboNumbersSum
import view.nextMediumComboNumbersSum
import view.nextSmallComboNumbersSum
import view.smallColumnNumbersForMaxComboSameSumExceptFewLast
import view.smallComboNumbers
import view.smallComboNumbersFirstExceptOne
import view.smallComboNumbersFirstExceptSqrt
import view.smallComboNumbersSecondExceptOne
import view.smallComboNumbersSecondExceptSqrt
import view.smallComboNumbersSum
import view.smallDifferentSumComboResult
import view.smallExpectedSumForMaxComboSameSum
import view.smallIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast
import view.smallIntersectNumberForMaxComboSameSum
import view.smallIntersectNumberForSingleComboAndAfterAutoCombo
import view.smallIntersectNumbersForDoubleDifferentSumCombo
import view.smallIntersectOneNumberForDoubleDifferentSumCombo
import view.smallIntersectRowRectangleNumbersForMaxComboSameSumExceptLast
import view.smallNumbersForSingleComboAndAfterAutoComboFirst
import view.smallNumbersForSingleComboAndAfterAutoComboSecond
import view.smallRectangleNumbersForMaxComboSameSumExceptFewLast
import view.smallRowNumbersForMaxComboSameSumExceptFewLast
import view.smallSumForSingleComboAndAfterAutoCombo
import kotlin.math.sqrt

/**
 * @author Ruslan Hlushan on 2019-10-09
 */
@SuppressWarnings("MaxLineLength", "LargeClass")
class ItemsMatrixComboTest {

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboRow_SMALL() =
            assert_replaceFakeWith_InsideAnyCell_WithSingleComboRow(
                    gameSize = GameSize.SMALL,
                    rowNumbers = smallComboNumbers(),
                    rowsSum = smallComboNumbersSum()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboRow_MEDIUM() =
            assert_replaceFakeWith_InsideAnyCell_WithSingleComboRow(
                    gameSize = GameSize.MEDIUM,
                    rowNumbers = mediumComboNumbers(),
                    rowsSum = mediumComboNumbersSum()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboRow_BIG() =
            assert_replaceFakeWith_InsideAnyCell_WithSingleComboRow(
                    gameSize = GameSize.BIG,
                    rowNumbers = bigComboNumbers(),
                    rowsSum = bigComboNumbersSum()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboColumn_SMALL() =
            assert_replaceFakeWith_InsideAnyCell_WithSingleComboColumn(
                    gameSize = GameSize.SMALL,
                    columnNumbers = smallComboNumbers(),
                    columnsSum = smallComboNumbersSum()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboColumn_MEDIUM() =
            assert_replaceFakeWith_InsideAnyCell_WithSingleComboColumn(
                    gameSize = GameSize.MEDIUM,
                    columnNumbers = mediumComboNumbers(),
                    columnsSum = mediumComboNumbersSum()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboColumn_BIG() =
            assert_replaceFakeWith_InsideAnyCell_WithSingleComboColumn(
                    gameSize = GameSize.BIG,
                    columnNumbers = bigComboNumbers(),
                    columnsSum = bigComboNumbersSum()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboRectangle_SMALL() =
            assert_replaceFakeWith_InsideAnyCell_WithSingleComboRectangle(
                    gameSize = GameSize.SMALL,
                    rectangleNumbers = smallComboNumbers(),
                    rectanglesSum = smallComboNumbersSum()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboRectangle_MEDIUM() =
            assert_replaceFakeWith_InsideAnyCell_WithSingleComboRectangle(
                    gameSize = GameSize.MEDIUM,
                    rectangleNumbers = mediumComboNumbers(),
                    rectanglesSum = mediumComboNumbersSum()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboRectangle_BIG() =
            assert_replaceFakeWith_InsideAnyCell_WithSingleComboRectangle(
                    gameSize = GameSize.BIG,
                    rectangleNumbers = bigComboNumbers(),
                    rectanglesSum = bigComboNumbersSum()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowColumn_SameSum_SMALL() {
        val comboNumbers = smallComboNumbers()
                .shuffled()
        val numbersExceptLast = comboNumbers
                .dropLast(1)

        replaceFakeWith_InsideAnyCell_WithComboRowColumn(
                gameSize = GameSize.SMALL,
                rowNumbersExceptLast = numbersExceptLast,
                columnNumbersExceptLast = numbersExceptLast,
                intersectNumber = comboNumbers.last(),
                expectedSum = nextSmallComboNumbersSum()
        )
    }

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowColumn_SameSum_MEDIUM() {
        val comboNumbers = mediumComboNumbers()
                .shuffled()
        val numbersExceptLast = comboNumbers
                .dropLast(1)

        replaceFakeWith_InsideAnyCell_WithComboRowColumn(
                gameSize = GameSize.MEDIUM,
                rowNumbersExceptLast = numbersExceptLast,
                columnNumbersExceptLast = numbersExceptLast,
                intersectNumber = comboNumbers.last(),
                expectedSum = nextMediumComboNumbersSum()
        )
    }

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowColumn_SameSum_BIG() {
        val comboNumbers = bigComboNumbers()
                .shuffled()
        val numbersExceptLast = comboNumbers
                .dropLast(1)

        replaceFakeWith_InsideAnyCell_WithComboRowColumn(
                gameSize = GameSize.BIG,
                rowNumbersExceptLast = numbersExceptLast,
                columnNumbersExceptLast = numbersExceptLast,
                intersectNumber = comboNumbers.last(),
                expectedSum = nextBigComboNumbersSum()
        )
    }

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowColumn_DifferentSums_SMALL() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumn(
                    gameSize = GameSize.SMALL,
                    rowNumbersExceptLast = smallComboNumbersFirstExceptOne(),
                    columnNumbersExceptLast = smallComboNumbersSecondExceptOne(),
                    intersectNumber = smallIntersectOneNumberForDoubleDifferentSumCombo(),
                    expectedSum = smallDifferentSumComboResult()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowColumn_DifferentSums_MEDIUM() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumn(
                    gameSize = GameSize.MEDIUM,
                    rowNumbersExceptLast = mediumComboNumbersFirstExceptOne(),
                    columnNumbersExceptLast = mediumComboNumbersSecondExceptOne(),
                    intersectNumber = mediumIntersectOneNumberForDoubleDifferentSumCombo(),
                    expectedSum = mediumDifferentSumComboResult()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowColumn_DifferentSums_BIG() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumn(
                    gameSize = GameSize.BIG,
                    rowNumbersExceptLast = bigComboNumbersFirstExceptOne(),
                    columnNumbersExceptLast = bigComboNumbersSecondExceptOne(),
                    intersectNumber = bigIntersectOneNumberForDoubleDifferentSumCombo(),
                    expectedSum = bigDifferentSumComboResult()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowRectangle_SameSum_SMALL() {
        val gameSize = GameSize.SMALL
        val bigCountRowsAndColumns = sqrt(gameSize.countRowsAndColumns.toDouble()).toInt()

        val comboNumbers = smallComboNumbers()
                .shuffled()

        val numbersExceptLast = comboNumbers
                .dropLast(bigCountRowsAndColumns)

        replaceFakeWith_InsideAnyCell_WithComboRowRectangle(
                gameSize = gameSize,
                rowNumbersExceptFewLast = numbersExceptLast,
                rectangleNumbersExceptFewLast = numbersExceptLast,
                intersectNumbers = comboNumbers.takeLast(bigCountRowsAndColumns),
                expectedSum = nextSmallComboNumbersSum()
        )
    }

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowRectangle_SameSum_MEDIUM() {
        val gameSize = GameSize.MEDIUM
        val bigCountRowsAndColumns = sqrt(gameSize.countRowsAndColumns.toDouble()).toInt()

        val comboNumbers = mediumComboNumbers().shuffled()

        val numbersExceptLast = comboNumbers
                .dropLast(bigCountRowsAndColumns)

        replaceFakeWith_InsideAnyCell_WithComboRowRectangle(
                gameSize = gameSize,
                rowNumbersExceptFewLast = numbersExceptLast,
                rectangleNumbersExceptFewLast = numbersExceptLast,
                intersectNumbers = comboNumbers.takeLast(bigCountRowsAndColumns),
                expectedSum = nextMediumComboNumbersSum()
        )
    }

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowRectangle_SameSum_BIG() {
        val gameSize = GameSize.BIG
        val bigCountRowsAndColumns = sqrt(gameSize.countRowsAndColumns.toDouble()).toInt()

        val comboNumbers = bigComboNumbers().shuffled()

        val numbersExceptLast = comboNumbers
                .dropLast(bigCountRowsAndColumns)

        replaceFakeWith_InsideAnyCell_WithComboRowRectangle(
                gameSize = gameSize,
                rowNumbersExceptFewLast = numbersExceptLast,
                rectangleNumbersExceptFewLast = numbersExceptLast,
                intersectNumbers = comboNumbers.takeLast(bigCountRowsAndColumns),
                expectedSum = nextBigComboNumbersSum()
        )
    }

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowRectangle_DifferentSums_SMALL() =
            replaceFakeWith_InsideAnyCell_WithComboRowRectangle(
                    gameSize = GameSize.SMALL,
                    rowNumbersExceptFewLast = smallComboNumbersFirstExceptSqrt(),
                    rectangleNumbersExceptFewLast = smallComboNumbersSecondExceptSqrt(),
                    intersectNumbers = smallIntersectNumbersForDoubleDifferentSumCombo(),
                    expectedSum = smallDifferentSumComboResult()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowRectangle_DifferentSums_MEDIUM() =
            replaceFakeWith_InsideAnyCell_WithComboRowRectangle(
                    gameSize = GameSize.MEDIUM,
                    rowNumbersExceptFewLast = mediumComboNumbersFirstExceptSqrt(),
                    rectangleNumbersExceptFewLast = mediumComboNumbersSecondExceptSqrt(),
                    intersectNumbers = mediumIntersectNumbersForDoubleDifferentSumCombo(),
                    expectedSum = mediumDifferentSumComboResult()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowRectangle_DifferentSums_BIG() =
            replaceFakeWith_InsideAnyCell_WithComboRowRectangle(
                    gameSize = GameSize.BIG,
                    rowNumbersExceptFewLast = bigComboNumbersFirstExceptSqrt(),
                    rectangleNumbersExceptFewLast = bigComboNumbersSecondExceptSqrt(),
                    intersectNumbers = bigIntersectNumbersForDoubleDifferentSumCombo(),
                    expectedSum = bigDifferentSumComboResult()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboColumnRectangle_SameSum_SMALL() {
        val gameSize = GameSize.SMALL
        val bigCountRowsAndColumns = sqrt(gameSize.countRowsAndColumns.toDouble()).toInt()

        val comboNumbers = smallComboNumbers().shuffled()

        val numbersExceptLast = comboNumbers
                .dropLast(bigCountRowsAndColumns)

        replaceFakeWith_InsideAnyCell_WithComboColumnRectangle(
                gameSize = gameSize,
                columnNumbersExceptFewLast = numbersExceptLast,
                rectangleNumbersExceptFewLast = numbersExceptLast,
                intersectNumbers = comboNumbers.takeLast(bigCountRowsAndColumns),
                expectedSum = nextSmallComboNumbersSum()
        )
    }

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboColumnRectangle_SameSum_MEDIUM() {
        val gameSize = GameSize.MEDIUM
        val bigCountRowsAndColumns = sqrt(gameSize.countRowsAndColumns.toDouble()).toInt()

        val comboNumbers = mediumComboNumbers().shuffled()

        val numbersExceptLast = comboNumbers
                .dropLast(bigCountRowsAndColumns)

        replaceFakeWith_InsideAnyCell_WithComboColumnRectangle(
                gameSize = gameSize,
                columnNumbersExceptFewLast = numbersExceptLast,
                rectangleNumbersExceptFewLast = numbersExceptLast,
                intersectNumbers = comboNumbers.takeLast(bigCountRowsAndColumns),
                expectedSum = nextMediumComboNumbersSum()
        )
    }

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboColumnRectangle_SameSum_BIG() {
        val gameSize = GameSize.BIG
        val bigCountRowsAndColumns = sqrt(gameSize.countRowsAndColumns.toDouble()).toInt()

        val comboNumbers = bigComboNumbers().shuffled()

        val numbersExceptLast = comboNumbers
                .dropLast(bigCountRowsAndColumns)

        replaceFakeWith_InsideAnyCell_WithComboColumnRectangle(
                gameSize = gameSize,
                columnNumbersExceptFewLast = numbersExceptLast,
                rectangleNumbersExceptFewLast = numbersExceptLast,
                intersectNumbers = comboNumbers.takeLast(bigCountRowsAndColumns),
                expectedSum = nextBigComboNumbersSum()
        )
    }

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboColumnRectangle_DifferentSums_SMALL() =
            replaceFakeWith_InsideAnyCell_WithComboColumnRectangle(
                    gameSize = GameSize.SMALL,
                    columnNumbersExceptFewLast = smallComboNumbersFirstExceptSqrt(),
                    rectangleNumbersExceptFewLast = smallComboNumbersSecondExceptSqrt(),
                    intersectNumbers = smallIntersectNumbersForDoubleDifferentSumCombo(),
                    expectedSum = smallDifferentSumComboResult()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboColumnRectangle_DifferentSums_MEDIUM() =
            replaceFakeWith_InsideAnyCell_WithComboColumnRectangle(
                    gameSize = GameSize.MEDIUM,
                    columnNumbersExceptFewLast = mediumComboNumbersFirstExceptSqrt(),
                    rectangleNumbersExceptFewLast = mediumComboNumbersSecondExceptSqrt(),
                    intersectNumbers = mediumIntersectNumbersForDoubleDifferentSumCombo(),
                    expectedSum = mediumDifferentSumComboResult()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboColumnRectangle_DifferentSums_BIG() =
            replaceFakeWith_InsideAnyCell_WithComboColumnRectangle(
                    gameSize = GameSize.BIG,
                    columnNumbersExceptFewLast = bigComboNumbersFirstExceptSqrt(),
                    rectangleNumbersExceptFewLast = bigComboNumbersSecondExceptSqrt(),
                    intersectNumbers = bigIntersectNumbersForDoubleDifferentSumCombo(),
                    expectedSum = bigDifferentSumComboResult()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowColumnRectangle_SameSum_SMALL() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumnRectangle(
                    gameSize = GameSize.SMALL,
                    rowNumbersExceptFewLast = smallRowNumbersForMaxComboSameSumExceptFewLast(),
                    columnNumbersExceptFewLast = smallColumnNumbersForMaxComboSameSumExceptFewLast(),
                    rectangleNumbersExceptFewLast = smallRectangleNumbersForMaxComboSameSumExceptFewLast(),
                    intersectRowRectangleNumbersExceptLast = smallIntersectRowRectangleNumbersForMaxComboSameSumExceptLast(),
                    intersectColumnRectangleNumbersExceptLast = smallIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast(),
                    lastIntersectNumber = smallIntersectNumberForMaxComboSameSum(),
                    expectedSum = smallExpectedSumForMaxComboSameSum()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowColumnRectangle_SameSum_MEDIUM() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumnRectangle(
                    gameSize = GameSize.MEDIUM,
                    rowNumbersExceptFewLast = mediumRowNumbersForMaxComboSameSumExceptFewLast(),
                    columnNumbersExceptFewLast = mediumColumnNumbersForMaxComboSameSumExceptFewLast(),
                    rectangleNumbersExceptFewLast = mediumRectangleNumbersForMaxComboSameSumExceptFewLast(),
                    intersectRowRectangleNumbersExceptLast = mediumIntersectRowRectangleNumbersForMaxComboSameSumExceptLast(),
                    intersectColumnRectangleNumbersExceptLast = mediumIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast(),
                    lastIntersectNumber = mediumIntersectNumberForMaxComboSameSum(),
                    expectedSum = mediumExpectedSumForMaxComboSameSum()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowColumnRectangle_SameSum_BIG() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumnRectangle(
                    gameSize = GameSize.BIG,
                    rowNumbersExceptFewLast = bigRowNumbersForMaxComboSameSumExceptFewLast(),
                    columnNumbersExceptFewLast = bigColumnNumbersForMaxComboSameSumExceptFewLast(),
                    rectangleNumbersExceptFewLast = bigRectangleNumbersForMaxComboSameSumExceptFewLast(),
                    intersectRowRectangleNumbersExceptLast = bigIntersectRowRectangleNumbersForMaxComboSameSumExceptLast(),
                    intersectColumnRectangleNumbersExceptLast = bigIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast(),
                    lastIntersectNumber = bigIntersectNumberForMaxComboSameSum(),
                    expectedSum = bigExpectedSumForMaxComboSameSum()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowColumnRectangle_DifferentSums_SMALL() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumnRectangle(
                    gameSize = GameSize.SMALL,
                    rowNumbersExceptFewLast = listOf(
                            1, 2, 3,
                            4, 5, 6
                    ),
                    columnNumbersExceptFewLast = listOf(
                            1, 2, 3,
                            9, 7, 9
                    ),
                    rectangleNumbersExceptFewLast = listOf(11, 7, 13, 9),
                    intersectRowRectangleNumbersExceptLast = listOf(7, 9),
                    intersectColumnRectangleNumbersExceptLast = listOf(5, 6),
                    lastIntersectNumber = 8,
                    expectedSum = 120
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowColumnRectangle_DifferentSums_MEDIUM() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumnRectangle(
                    gameSize = GameSize.MEDIUM,
                    rowNumbersExceptFewLast = listOf(
                            1, 2, 3, 4,
                            5, 16, 21, 24,
                            0, 3, 3, 4
                    ),
                    columnNumbersExceptFewLast = listOf(
                            1, 2, 3, 4,
                            7, 9, 10, 25,
                            0, 2, 2, 5
                    ),
                    rectangleNumbersExceptFewLast = listOf(21, 22, 13, 24, 27, 15, 2, 8, 12),
                    intersectRowRectangleNumbersExceptLast = listOf(7, 9, 10),
                    intersectColumnRectangleNumbersExceptLast = listOf(5, 6, 11),
                    lastIntersectNumber = 8,
                    expectedSum = 500
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithComboRowColumnRectangle_DifferentSums_BIG() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumnRectangle(
                    gameSize = GameSize.BIG,
                    rowNumbersExceptFewLast = listOf(
                            2, 1, 7, 6, 5,
                            4, 0, 2, 1, 11,
                            4, 5, 40, 29, 27,
                            3, 1, 11, 5, 11
                    ),
                    columnNumbersExceptFewLast = listOf(
                            3, 0, 6, 7, 4,
                            6, 10, 10, 1, 1,
                            11, 3, 1, 6, 6,
                            6, 5, 4, 3, 7
                    ),
                    rectangleNumbersExceptFewLast = listOf(
                            101, 82, 59, 44,
                            16, 4, 4, 10,
                            30, 22, 12, 43,
                            7, 13, 6, 6
                    ),
                    intersectRowRectangleNumbersExceptLast = listOf(5, 4, 5, 7),
                    intersectColumnRectangleNumbersExceptLast = listOf(3, 8, 1, 4),
                    lastIntersectNumber = 4,
                    expectedSum = 1_000
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboRowAndAfterAutoComboThisColumn_SMALL() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumn(
                    gameSize = GameSize.SMALL,
                    rowNumbersExceptLast = smallNumbersForSingleComboAndAfterAutoComboFirst(),
                    columnNumbersExceptLast = smallNumbersForSingleComboAndAfterAutoComboSecond(),
                    intersectNumber = smallIntersectNumberForSingleComboAndAfterAutoCombo(),
                    expectedSum = smallSumForSingleComboAndAfterAutoCombo()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboRowAndAfterAutoComboThisColumn_MEDIUM() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumn(
                    gameSize = GameSize.MEDIUM,
                    rowNumbersExceptLast = mediumNumbersForSingleComboAndAfterAutoComboFirst(),
                    columnNumbersExceptLast = mediumNumbersForSingleComboAndAfterAutoComboSecond(),
                    intersectNumber = mediumIntersectNumberForSingleComboAndAfterAutoCombo(),
                    expectedSum = mediumSumForSingleComboAndAfterAutoCombo()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboRowAndAfterAutoComboThisColumn_BIG() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumn(
                    gameSize = GameSize.BIG,
                    rowNumbersExceptLast = bigNumbersForSingleComboAndAfterAutoComboFirst(),
                    columnNumbersExceptLast = bigNumbersForSingleComboAndAfterAutoComboSecond(),
                    intersectNumber = bigIntersectNumberForSingleComboAndAfterAutoCombo(),
                    expectedSum = bigSumForSingleComboAndAfterAutoCombo()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboColumnAndAfterAutoComboThisRow_SMALL() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumn(
                    gameSize = GameSize.SMALL,
                    rowNumbersExceptLast = smallNumbersForSingleComboAndAfterAutoComboSecond(),
                    columnNumbersExceptLast = smallNumbersForSingleComboAndAfterAutoComboFirst(),
                    intersectNumber = smallIntersectNumberForSingleComboAndAfterAutoCombo(),
                    expectedSum = smallSumForSingleComboAndAfterAutoCombo()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboColumnAndAfterAutoComboThisRow_MEDIUM() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumn(
                    gameSize = GameSize.MEDIUM,
                    rowNumbersExceptLast = mediumNumbersForSingleComboAndAfterAutoComboSecond(),
                    columnNumbersExceptLast = mediumNumbersForSingleComboAndAfterAutoComboFirst(),
                    intersectNumber = mediumIntersectNumberForSingleComboAndAfterAutoCombo(),
                    expectedSum = mediumSumForSingleComboAndAfterAutoCombo()
            )

    @Test
    fun replaceFakeWith_InsideAnyCell_WithSingleComboColumnAndAfterAutoComboThisRow_BIG() =
            replaceFakeWith_InsideAnyCell_WithComboRowColumn(
                    gameSize = GameSize.BIG,
                    rowNumbersExceptLast = bigNumbersForSingleComboAndAfterAutoComboSecond(),
                    columnNumbersExceptLast = bigNumbersForSingleComboAndAfterAutoComboFirst(),
                    intersectNumber = bigIntersectNumberForSingleComboAndAfterAutoCombo(),
                    expectedSum = bigSumForSingleComboAndAfterAutoCombo()
            )

    private fun assert_replaceFakeWith_InsideAnyCell_WithSingleComboRow(
            gameSize: GameSize,
            rowNumbers: List<Int>,
            rowsSum: Int
    ) {
        val itemsMatrix = createEmptyItemsMatrixFor(gameSize, setUpRectangleSizes = true)

        val row = (generateFakePositiveInt() % gameSize.countRowsAndColumns)

        itemsMatrix.updateFrom(generateFakeImmutableNumbersMatrix(
                gameSize,
                emptyRow = row,
                emptyColumn = null,
                emptyRectangle = null
        ))

        val expected = itemsMatrix.items.map(RectangleArea::copy)
                .toMutableList()

        val cellsInMatrixIndexes = fillRow(row = row, gameSize = gameSize, itemsMatrix = itemsMatrix, rowNumbers = rowNumbers)

        val lastInsertIndex = cellsInMatrixIndexes.last()
        expected[lastInsertIndex] = expected[lastInsertIndex]
                .copy(
                        isFake = false,
                        number = rowsSum
                )

        assertEquals(expected, itemsMatrix.items)
    }

    private fun assert_replaceFakeWith_InsideAnyCell_WithSingleComboColumn(
            gameSize: GameSize,
            columnNumbers: List<Int>,
            columnsSum: Int
    ) {
        val itemsMatrix = createEmptyItemsMatrixFor(gameSize, setUpRectangleSizes = true)

        val column = (generateFakePositiveInt() % gameSize.countRowsAndColumns)

        itemsMatrix.updateFrom(generateFakeImmutableNumbersMatrix(
                gameSize,
                emptyRow = null,
                emptyColumn = column,
                emptyRectangle = null
        ))

        val expected = itemsMatrix.items.map(RectangleArea::copy)
                .toMutableList()

        val cellsInMatrixIndexes = fillColumn(
                column = column,
                gameSize = gameSize,
                itemsMatrix = itemsMatrix,
                columnNumbers = columnNumbers
        )

        val lastInsertIndex = cellsInMatrixIndexes.last()
        expected[lastInsertIndex] = expected[lastInsertIndex].copy(isFake = false, number = columnsSum)

        assertEquals(expected, itemsMatrix.items)
    }

    private fun assert_replaceFakeWith_InsideAnyCell_WithSingleComboRectangle(
            gameSize: GameSize,
            rectangleNumbers: List<Int>,
            rectanglesSum: Int
    ) {
        val itemsMatrix = createEmptyItemsMatrixFor(gameSize, setUpRectangleSizes = true)

        val rectangle = (generateFakePositiveInt() % gameSize.countRowsAndColumns)

        itemsMatrix.updateFrom(generateFakeImmutableNumbersMatrix(
                gameSize,
                emptyRow = null,
                emptyColumn = null,
                emptyRectangle = rectangle)
        )

        val expected = itemsMatrix.items.map(RectangleArea::copy)
                .toMutableList()

        val cellsInMatrixIndexes = fillRectangle(
                rectangle = rectangle,
                gameSize = gameSize,
                itemsMatrix = itemsMatrix,
                rectangleNumbers = rectangleNumbers
        )

        val lastInsertIndex = cellsInMatrixIndexes.last()
        expected[lastInsertIndex] = expected[lastInsertIndex].copy(isFake = false, number = rectanglesSum)

        assertEquals(expected, itemsMatrix.items)
    }

    private fun replaceFakeWith_InsideAnyCell_WithComboRowColumn(
            gameSize: GameSize,
            rowNumbersExceptLast: List<Int>,
            columnNumbersExceptLast: List<Int>,
            intersectNumber: Int,
            expectedSum: Int
    ) {
        val itemsMatrix = createEmptyItemsMatrixFor(gameSize, setUpRectangleSizes = true)

        val row = (generateFakePositiveInt() % gameSize.countRowsAndColumns)
        val column = (generateFakePositiveInt() % gameSize.countRowsAndColumns)

        itemsMatrix.updateFrom(generateFakeImmutableNumbersMatrix(
                gameSize,
                emptyRow = row,
                emptyColumn = column,
                emptyRectangle = null)
        )

        val expected = itemsMatrix.items.map(RectangleArea::copy)
                .toMutableList()

        val lastInsertIndex = fillRowColumn(
                itemsMatrix = itemsMatrix,
                gameSize = gameSize,
                row = row,
                column = column,
                rowNumbersExceptLast = rowNumbersExceptLast,
                columnNumbersExceptLast = columnNumbersExceptLast,
                intersectNumber = intersectNumber
        )
        expected[lastInsertIndex] = expected[lastInsertIndex].copy(isFake = false, number = expectedSum)

        assertEquals(expected, itemsMatrix.items)
    }

    private fun replaceFakeWith_InsideAnyCell_WithComboRowRectangle(
            gameSize: GameSize,
            rowNumbersExceptFewLast: List<Int>,
            rectangleNumbersExceptFewLast: List<Int>,
            intersectNumbers: List<Int>,
            expectedSum: Int
    ) {
        val itemsMatrix = createEmptyItemsMatrixFor(gameSize, setUpRectangleSizes = true)

        val row = (generateFakePositiveInt() % gameSize.countRowsAndColumns)

        val rectangle = generateRandomRectangleForRow(row, gameSize)

        itemsMatrix.updateFrom(generateFakeImmutableNumbersMatrix(
                gameSize,
                emptyRow = row,
                emptyColumn = null,
                emptyRectangle = rectangle)
        )

        val expected = itemsMatrix.items.map(RectangleArea::copy)
                .toMutableList()

        val lastInsertIndex = fillRowRectangle(
                itemsMatrix = itemsMatrix,
                gameSize = gameSize,
                row = row,
                rectangle = rectangle,
                rowNumbersExceptFewLast = rowNumbersExceptFewLast,
                rectangleNumbersExceptFewLast = rectangleNumbersExceptFewLast,
                intersectNumbers = intersectNumbers
        )

        expected[lastInsertIndex] = expected[lastInsertIndex].copy(isFake = false, number = expectedSum)

        assertEquals(expected, itemsMatrix.items)
    }

    private fun replaceFakeWith_InsideAnyCell_WithComboColumnRectangle(
            gameSize: GameSize,
            columnNumbersExceptFewLast: List<Int>,
            rectangleNumbersExceptFewLast: List<Int>,
            intersectNumbers: List<Int>,
            expectedSum: Int
    ) {
        val itemsMatrix = createEmptyItemsMatrixFor(gameSize, setUpRectangleSizes = true)

        val column = (generateFakePositiveInt() % gameSize.countRowsAndColumns)

        val rectangle = generateRandomRectangleForColumn(column, gameSize)

        itemsMatrix.updateFrom(generateFakeImmutableNumbersMatrix(
                gameSize,
                emptyRow = null,
                emptyColumn = column,
                emptyRectangle = rectangle
        ))

        val expected = itemsMatrix.items.map(RectangleArea::copy)
                .toMutableList()

        val lastInsertIndex = fillColumnRectangle(
                itemsMatrix = itemsMatrix,
                gameSize = gameSize,
                column = column,
                rectangle = rectangle,
                columnNumbersExceptFewLast = columnNumbersExceptFewLast,
                rectangleNumbersExceptFewLast = rectangleNumbersExceptFewLast,
                intersectNumbers = intersectNumbers
        )

        expected[lastInsertIndex] = expected[lastInsertIndex].copy(isFake = false, number = expectedSum)

        assertEquals(expected, itemsMatrix.items)
    }

    @SuppressWarnings("LongParameterList")
    private fun replaceFakeWith_InsideAnyCell_WithComboRowColumnRectangle(
            gameSize: GameSize,
            rowNumbersExceptFewLast: List<Int>,
            columnNumbersExceptFewLast: List<Int>,
            rectangleNumbersExceptFewLast: List<Int>,
            intersectRowRectangleNumbersExceptLast: List<Int>,
            intersectColumnRectangleNumbersExceptLast: List<Int>,
            lastIntersectNumber: Int,
            expectedSum: Int
    ) {
        val itemsMatrix = createEmptyItemsMatrixFor(gameSize, setUpRectangleSizes = true)

        val row = (generateFakePositiveInt() % gameSize.countRowsAndColumns)
        val column = (generateFakePositiveInt() % gameSize.countRowsAndColumns)

        val lastInsertIndex = getIndexForRowColumn(row = row, column = column, gameSize = gameSize)

        val rectangle = getRectangleForIndex(index = lastInsertIndex, gameSize = gameSize)

        itemsMatrix.updateFrom(generateFakeImmutableNumbersMatrix(
                gameSize,
                emptyRow = row,
                emptyColumn = column,
                emptyRectangle = rectangle
        ))

        val expected = itemsMatrix.items.map(RectangleArea::copy)
                .toMutableList()

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

        expected[lastInsertIndex] = expected[lastInsertIndex].copy(isFake = false, number = expectedSum)

        assertEquals(expected, itemsMatrix.items)
    }
}