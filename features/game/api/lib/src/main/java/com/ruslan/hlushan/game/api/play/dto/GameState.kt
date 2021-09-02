package com.ruslan.hlushan.game.api.play.dto

data class GameState(
        val current: MatrixAndNewItemsState,
        val stack: List<MatrixAndNewItemsState>
)

val GameState.wasPlayed: Boolean get() = (current.immutableNumbersMatrix.numbers.any { number -> number != null })