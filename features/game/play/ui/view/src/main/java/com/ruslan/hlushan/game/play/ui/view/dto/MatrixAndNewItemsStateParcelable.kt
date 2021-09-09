package com.ruslan.hlushan.game.play.ui.view.dto

import android.os.Parcelable
import com.ruslan.hlushan.game.api.play.dto.MatrixAndNewItemsState
import kotlinx.parcelize.Parcelize

@Parcelize
class MatrixAndNewItemsStateParcelable(
        val immutableNumbersMatrix: ImmutableNumbersMatrixParcelable,
        val newItems: List<Int>
) : Parcelable {

    fun toOriginal(): MatrixAndNewItemsState =
            MatrixAndNewItemsState(
                    immutableNumbersMatrix = this.immutableNumbersMatrix.toOriginal(),
                    newItems = this.newItems
            )
}

internal fun MatrixAndNewItemsState.toParcelable(): MatrixAndNewItemsStateParcelable =
        MatrixAndNewItemsStateParcelable(
                immutableNumbersMatrix = this.immutableNumbersMatrix.toParcelable(),
                newItems = this.newItems
        )