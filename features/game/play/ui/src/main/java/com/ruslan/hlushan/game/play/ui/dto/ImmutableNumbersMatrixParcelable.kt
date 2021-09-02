package com.ruslan.hlushan.game.play.ui.dto

import android.os.Parcelable
import com.ruslan.hlushan.game.core.api.play.dto.GameSize
import com.ruslan.hlushan.game.core.api.play.dto.ImmutableNumbersMatrix
import kotlinx.parcelize.Parcelize

@Parcelize
internal class ImmutableNumbersMatrixParcelable(
        val numbers: List<Int?>,
        val gameSize: GameSize,
        val totalSum: Int
) : Parcelable {

    fun toOriginal(): ImmutableNumbersMatrix =
            ImmutableNumbersMatrix(
                    numbers = this.numbers,
                    gameSize = this.gameSize,
                    totalSum = this.totalSum
            )
}

internal fun ImmutableNumbersMatrix.toParcelable(): ImmutableNumbersMatrixParcelable =
        ImmutableNumbersMatrixParcelable(
                numbers = this.numbers,
                gameSize = this.gameSize,
                totalSum = this.totalSum
        )