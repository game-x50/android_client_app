package com.ruslan.hlushan.game.play.api.calculations

import com.ruslan.hlushan.game.api.play.dto.GameSize
import kotlin.math.sqrt

data class GameViewParams(
        val gameSize: GameSize,
        val countNewElements: Int,
        val tinyLine: Int = 1,
        val boldLine: Int = 3,
        val marginInCellsBetweenTableAndNewElementsLine: Double = 0.5
) {

    val countBigRowsAndColumns: Int = sqrt(countRowsAndColumns.toDouble()).toInt()

    val countRowsAndColumnsIncludeSums: Int = (countRowsAndColumns + 1)

    private val countOfAllLines: Int = (countRowsAndColumnsIncludeSums + 1)

    private val countOfBoldLines: Int = (countBigRowsAndColumns + 1)

    private val countOfTinyLines: Int = (countOfAllLines - countOfBoldLines)

    val totalGameLinesSize: Int = ((countOfTinyLines * tinyLine) + (countOfBoldLines * boldLine))
}

val GameViewParams.countRowsAndColumns: Int get() = this.gameSize.countRowsAndColumns