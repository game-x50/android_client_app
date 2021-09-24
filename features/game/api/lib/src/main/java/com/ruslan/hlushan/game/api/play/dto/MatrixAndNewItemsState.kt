package com.ruslan.hlushan.game.api.play.dto

data class MatrixAndNewItemsState(
        val immutableNumbersMatrix: ImmutableNumbersMatrix,
        val newItems: List<Int>
)