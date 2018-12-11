package view

/**
 * @author Ruslan Hlushan on 2019-10-16
 */

fun smallComboNumbers(): List<Int> =
        listOf(1, 2, 3,
               4, 5, 6,
               7, 8, 9)

fun smallComboNumbersSum(): Int = 45

fun nextSmallComboNumbersSum(): Int = 50

fun smallComboNumbersFirstExceptOne(): List<Int> =
        listOf(1, 2, 3,
               4, 5, 6,
               7, 8)

fun smallComboNumbersSecondExceptOne(): List<Int> =
        listOf(10, 11, 9,
               32, 5, 18,
               3, 3)

fun smallIntersectOneNumberForDoubleDifferentSumCombo(): Int = 9

fun smallComboNumbersFirstExceptSqrt(): List<Int> =
        listOf(1, 2, 3,
               4, 5, 6)

fun smallComboNumbersSecondExceptSqrt(): List<Int> =
        listOf(19, 21, 3,
               14, 17, 2)

fun smallIntersectNumbersForDoubleDifferentSumCombo(): List<Int> =
        listOf(7, 8, 9)

fun smallDifferentSumComboResult(): Int = 120

fun mediumComboNumbers(): List<Int> =
        listOf(1, 2, 3, 4,
               5, 5, 10, 20,
               14, 16, 0, 6,
               24, 21, 39, 30)

fun mediumComboNumbersSum(): Int = 200

fun nextMediumComboNumbersSum(): Int = 300

fun mediumComboNumbersFirstExceptOne(): List<Int> =
        listOf(1, 2, 3, 4,
               5, 5, 10, 20,
               14, 16, 0, 6,
               24, 21, 39)

fun mediumComboNumbersSecondExceptOne(): List<Int> =
        listOf(31, 52, 38, 14,
               15, 25, 20, 35,
               64, 16, 24, 26,
               34, 27, 49)

fun mediumIntersectOneNumberForDoubleDifferentSumCombo(): Int = 30

fun mediumComboNumbersFirstExceptSqrt(): List<Int> =
        listOf(1, 2, 3, 4,
               5, 5, 10, 20,
               14, 16, 0, 6)

fun mediumComboNumbersSecondExceptSqrt(): List<Int> =
        listOf(31, 42, 38, 24,
               15, 25, 20, 35,
               74, 26, 24, 32)

fun mediumIntersectNumbersForDoubleDifferentSumCombo(): List<Int> =
        listOf(24, 21, 39, 30)

fun mediumDifferentSumComboResult(): Int = 750

fun bigComboNumbers(): List<Int> =
        listOf(1, 2, 3, 0, 4,
               5, 14, 21, 6, 8,
               25, 15, 50, 100, 150,
               120, 0, 9, 7, 16,
               30, 24, 100, 20, 20)

fun bigComboNumbersSum(): Int = 750

fun nextBigComboNumbersSum(): Int = 1_000

fun bigComboNumbersFirstExceptOne(): List<Int> =
        listOf(1, 2, 3, 0, 4,
               5, 14, 21, 6, 8,
               25, 15, 50, 100, 150,
               120, 0, 9, 7, 16,
               30, 24, 100, 20)

fun bigComboNumbersSecondExceptOne(): List<Int> =
        listOf(1, 52, 3, 200, 4,
               150, 14, 21, 6, 13,
               125, 15, 50, 100, 150,
               220, 150, 109, 107, 16,
               130, 124, 100, 120)

fun bigIntersectOneNumberForDoubleDifferentSumCombo(): Int = 20

fun bigComboNumbersFirstExceptSqrt(): List<Int> =
        listOf(1, 2, 3, 0, 4,
               5, 14, 21, 6, 8,
               25, 15, 50, 100, 150,
               120, 0, 9, 7, 16)

fun bigComboNumbersSecondExceptSqrt(): List<Int> =
        listOf(150, 34, 21, 36, 23,
               125, 35, 80, 120, 130,
               220, 150, 109, 87, 6,
               130, 124, 96, 130, 0)

fun bigIntersectNumbersForDoubleDifferentSumCombo(): List<Int> =
        listOf(30, 24, 100, 20, 20)

fun bigDifferentSumComboResult(): Int = 3_000

fun smallRowNumbersForMaxComboSameSumExceptFewLast(): List<Int> = listOf(1, 2, 3,
                                                                         4, 5, 6)

fun smallColumnNumbersForMaxComboSameSumExceptFewLast(): List<Int> = listOf(1, 2, 3,
                                                                            4, 7, 9)

fun smallRectangleNumbersForMaxComboSameSumExceptFewLast(): List<Int> = listOf(1, 2, 3, 4)

fun smallIntersectRowRectangleNumbersForMaxComboSameSumExceptLast(): List<Int> = listOf(7, 9)

fun smallIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast(): List<Int> = listOf(5, 6)

fun smallIntersectNumberForMaxComboSameSum(): Int = 8

fun smallExpectedSumForMaxComboSameSum(): Int = 75

fun mediumRowNumbersForMaxComboSameSumExceptFewLast(): List<Int> = listOf(1, 2, 3, 4,
                                                                          5, 6, 11, 24,
                                                                          0, 3, 3, 4)

fun mediumColumnNumbersForMaxComboSameSumExceptFewLast(): List<Int> = listOf(1, 2, 3, 4,
                                                                             7, 9, 10, 25,
                                                                             0, 2, 2, 5)

fun mediumRectangleNumbersForMaxComboSameSumExceptFewLast(): List<Int> = listOf(1, 2, 3, 4, 12, 8, 2, 0, 12)

fun mediumIntersectRowRectangleNumbersForMaxComboSameSumExceptLast(): List<Int> = listOf(7, 9, 10)

fun mediumIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast(): List<Int> = listOf(5, 6, 11)

fun mediumIntersectNumberForMaxComboSameSum(): Int = 8

fun mediumExpectedSumForMaxComboSameSum(): Int = 200

fun bigRowNumbersForMaxComboSameSumExceptFewLast(): List<Int> = listOf(2, 1, 7, 6, 5,
                                                                       4, 0, 2, 1, 11,
                                                                       4, 5, 10, 9, 7,
                                                                       3, 1, 6, 5, 6)

fun bigColumnNumbersForMaxComboSameSumExceptFewLast(): List<Int> = listOf(3, 0, 6, 7, 4,
                                                                          6, 10, 10, 1, 1,
                                                                          11, 3, 1, 6, 6,
                                                                          6, 5, 4, 3, 7)

fun bigRectangleNumbersForMaxComboSameSumExceptFewLast(): List<Int> = listOf(1, 2, 9, 4,
                                                                             6, 4, 4, 10,
                                                                             0, 2, 12, 3,
                                                                             7, 3, 6, 6)

fun bigIntersectRowRectangleNumbersForMaxComboSameSumExceptLast(): List<Int> = listOf(5, 4, 5, 7)

fun bigIntersectColumnRectangleNumbersForMaxComboSameSumExceptLast(): List<Int> = listOf(3, 8, 1, 4)

fun bigIntersectNumberForMaxComboSameSum(): Int = 4

fun bigExpectedSumForMaxComboSameSum(): Int = 300

fun smallNumbersForSingleComboAndAfterAutoComboSecond(): List<Int> = listOf(1, 2, 3,
                                                                            4, 5, 6,
                                                                            2, 7)

fun smallNumbersForSingleComboAndAfterAutoComboFirst(): List<Int> = listOf(1, 2, 3,
                                                                           4, 5, 6,
                                                                           7, 9)

fun smallIntersectNumberForSingleComboAndAfterAutoCombo(): Int = 8

fun smallSumForSingleComboAndAfterAutoCombo(): Int = 100

fun mediumNumbersForSingleComboAndAfterAutoComboFirst(): List<Int> = listOf(1, 2, 3, 4,
                                                                            5, 5, 10, 20,
                                                                            14, 16, 0, 6,
                                                                            24, 21, 39)

fun mediumNumbersForSingleComboAndAfterAutoComboSecond(): List<Int> = listOf(11, 0, 13, 45,
                                                                             25, 25, 10, 20,
                                                                             14, 16, 0, 26,
                                                                             24, 41, 30)

fun mediumIntersectNumberForSingleComboAndAfterAutoCombo(): Int = 30

fun mediumSumForSingleComboAndAfterAutoCombo(): Int = 750

fun bigNumbersForSingleComboAndAfterAutoComboFirst(): List<Int> = listOf(1, 2, 3, 0, 4,
                                                                         5, 14, 21, 6, 8,
                                                                         25, 15, 50, 100, 150,
                                                                         120, 0, 9, 7, 16,
                                                                         30, 24, 100, 40)

fun bigNumbersForSingleComboAndAfterAutoComboSecond(): List<Int> = listOf(1, 2, 3, 0, 4,
                                                                          5, 14, 21, 6, 8,
                                                                          25, 15, 50, 100, 150,
                                                                          120, 0, 9, 7, 16,
                                                                          30, 24, 100, 20)

fun bigIntersectNumberForSingleComboAndAfterAutoCombo(): Int = 20

fun bigSumForSingleComboAndAfterAutoCombo(): Int = 2_000