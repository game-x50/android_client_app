package com.ruslan.hlushan.game.storage.impl.remote.dto

import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo

internal sealed class UploadLocalModifiedRequest {

    data class Created(
            val record: GameRecord,
            val localCreateId: RecordSyncState.LocalCreateId,
            val lastLocalModifiedTimestamp: RecordSyncState.LastLocalModifiedTimestamp
    ) : UploadLocalModifiedRequest()

    data class Updated(
            val record: GameRecord,
            val remoteId: RemoteInfo.Id,
            val remoteActionId: RemoteInfo.ActionId,
            val lastLocalModifiedTimestamp: RecordSyncState.LastLocalModifiedTimestamp
    ) : UploadLocalModifiedRequest()

    data class Deleted(
            val localRecordId: Long,
            val remoteId: RemoteInfo.Id,
            val remoteActionId: RemoteInfo.ActionId
    ) : UploadLocalModifiedRequest()
}