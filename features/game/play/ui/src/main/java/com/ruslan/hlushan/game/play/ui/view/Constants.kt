@file:SuppressWarnings("MagicNumber")

package com.ruslan.hlushan.game.play.ui.view

val COMBO_SUMS: IntArray = intArrayOf(
        36,
        45,
        50,
        75,
        100,
        120,
        200,
        300,
        500,
        750,
        1_000,
        1_500,
        2_000,
        3_000,
        5_000,
        7_500,
        10_000
)

@SuppressWarnings("SpreadOperator")
val AVAILABLE_NUMBERS: IntArray = intArrayOf(
        *(0..10).map { n -> n }.toIntArray(),
        15, 20, 25, 30,
        *COMBO_SUMS.dropLast(3).toIntArray()
)