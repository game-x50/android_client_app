package com.ruslan.hlushan.game.storage.impl.remote.dto.server

import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.api.play.dto.ImmutableNumbersMatrix
import com.ruslan.hlushan.game.api.play.dto.MatrixAndNewItemsState
import kotlinx.serialization.Serializable

private const val EMPTY_NUMBER = -1

@Serializable
internal class RemoteApiGameState(
        val matrix: IntArray,
        val newItems: List<Int>
)

internal fun RemoteApiGameState.toMatrixAndNewItemsState(gameSize: GameSize): MatrixAndNewItemsState {
    val numbers: List<Int?> = this.matrix.map { value ->
        if (value != EMPTY_NUMBER) {
            value
        } else {
            null
        }
    }

    return MatrixAndNewItemsState(
            immutableNumbersMatrix = ImmutableNumbersMatrix(
                    numbers = numbers,
                    gameSize = gameSize,
                    totalSum = numbers.filterNotNull().sum()
            ),
            newItems = this.newItems
    )
}

internal fun MatrixAndNewItemsState.toRemoteApi(): RemoteApiGameState =
        RemoteApiGameState(
                matrix = this.immutableNumbersMatrix
                        .numbers
                        .map { value ->
                            value
                            ?: EMPTY_NUMBER
                        }.toIntArray(),
                newItems = this.newItems
        )