package com.ruslan.hlushan.game.core.api.play.dto

/**
 * @author Ruslan Hlushan on 10/16/18.
 */
data class MatrixAndNewItemsState(
        val immutableNumbersMatrix: ImmutableNumbersMatrix,
        val newItems: List<Int>
)