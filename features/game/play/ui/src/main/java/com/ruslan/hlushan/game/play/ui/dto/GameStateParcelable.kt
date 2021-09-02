package com.ruslan.hlushan.game.play.ui.dto

import android.os.Parcelable
import com.ruslan.hlushan.game.core.api.play.dto.GameState
import com.ruslan.hlushan.game.core.api.play.dto.MatrixAndNewItemsState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class GameStateParcelable(
        val current: MatrixAndNewItemsStateParcelable,
        val stack: List<MatrixAndNewItemsStateParcelable>
) : Parcelable {

    fun toOriginal(): GameState =
            GameState(
                    current = this.current.toOriginal(),
                    stack = this.stack.map(MatrixAndNewItemsStateParcelable::toOriginal)
            )
}

internal fun GameState.toParcelable(): GameStateParcelable =
        GameStateParcelable(
                current = this.current.toParcelable(),
                stack = this.stack.map(MatrixAndNewItemsState::toParcelable)
        )