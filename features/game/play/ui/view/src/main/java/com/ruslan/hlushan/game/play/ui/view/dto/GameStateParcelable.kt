package com.ruslan.hlushan.game.play.ui.view.dto

import android.os.Parcelable
import com.ruslan.hlushan.game.api.play.dto.GameState
import com.ruslan.hlushan.game.api.play.dto.MatrixAndNewItemsState
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameStateParcelable(
        val current: MatrixAndNewItemsStateParcelable,
        val stack: List<MatrixAndNewItemsStateParcelable>
) : Parcelable {

    fun toOriginal(): GameState =
            GameState(
                    current = this.current.toOriginal(),
                    stack = this.stack.map(MatrixAndNewItemsStateParcelable::toOriginal)
            )
}

fun GameState.toParcelable(): GameStateParcelable =
        GameStateParcelable(
                current = this.current.toParcelable(),
                stack = this.stack.map(MatrixAndNewItemsState::toParcelable)
        )