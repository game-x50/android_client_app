package com.ruslan.hlushan.game.play.ui.dto

import android.os.Parcelable
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.OrderType
import kotlinx.parcelize.Parcelize

@Parcelize
internal class GameRecordWithSyncStateOrderParamsParcelable(
        val variant: GameRecordWithSyncState.Order.Variant,
        val type: OrderType
) : Parcelable {

    fun toOriginal(): GameRecordWithSyncState.Order.Params =
            GameRecordWithSyncState.Order.Params(
                    variant = this.variant,
                    type = this.type
            )
}

internal fun GameRecordWithSyncState.Order.Params.toParcelable(): GameRecordWithSyncStateOrderParamsParcelable =
        GameRecordWithSyncStateOrderParamsParcelable(
                variant = this.variant,
                type = this.type
        )