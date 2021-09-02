package com.ruslan.hlushan.game.api.play.dto

/**
 * @author Ruslan Hlushan on 10/15/18.
 */
data class ImmutableNumbersMatrix(
        val numbers: List<Int?>,
        val gameSize: GameSize,
        val totalSum: Int
) {
    companion object {
        fun emptyForSize(gameSize: GameSize) = ImmutableNumbersMatrix(
                numbers = (1..(gameSize.countRowsAndColumns * gameSize.countRowsAndColumns)).map { null },
                gameSize = gameSize,
                totalSum = 0
        )
    }
}