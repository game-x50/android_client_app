package com.ruslan.hlushan.game.storage.impl.remote.dto

import com.ruslan.hlushan.game.api.play.dto.GameRecord
import org.threeten.bp.Instant

internal sealed class UploadLocalModifiedRequest {

    data class Created(
            val record: GameRecord,
            val localCreateId: String,
            val lastLocalModifiedTimestamp: Instant
    ) : UploadLocalModifiedRequest()

    data class Updated(
            val record: GameRecord,
            val remoteId: String,
            val remoteActionId: String,
            val lastLocalModifiedTimestamp: Instant
    ) : UploadLocalModifiedRequest()

    data class Deleted(
            val localRecordId: Long,
            val remoteId: String,
            val remoteActionId: String
    ) : UploadLocalModifiedRequest()
}