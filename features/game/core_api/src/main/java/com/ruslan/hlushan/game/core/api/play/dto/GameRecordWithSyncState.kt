package com.ruslan.hlushan.game.core.api.play.dto

data class GameRecordWithSyncState(
        val record: GameRecord,
        val syncState: RecordSyncState
) {

    interface Order {

        enum class Variant {
            TOTAL_SUM,
            LAST_MODIFIED_TIMESTAMP;

            companion object {
                fun fromOrdinal(ordinal: Int): Variant? =
                        Variant.values().getOrNull(ordinal)
            }
        }

        data class Params(val variant: Variant, val type: OrderType)
    }
}