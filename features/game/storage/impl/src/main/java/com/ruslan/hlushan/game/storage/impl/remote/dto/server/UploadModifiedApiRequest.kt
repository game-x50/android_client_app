package com.ruslan.hlushan.game.storage.impl.remote.dto.server

import com.ruslan.hlushan.game.storage.impl.remote.dto.UploadLocalModifiedRequest
import com.ruslan.hlushan.parsing.impl.utils.parsing.InstantAsEpochMillisSerializer
import kotlinx.serialization.Serializable
import org.threeten.bp.Instant

@SuppressWarnings("LongParameterList")
@Serializable
internal class UploadModifiedApiRequest
private constructor(
        val type: LocalActionTypeApi,
        val localId: Long,
        val id: String?,
        val createdId: String?,
        @Serializable(with = InstantAsEpochMillisSerializer::class)
        val lastLocalModifiedTimestamp: Instant?,
        val lastActionId: String?,
        val baseGameInfo: BaseRemoteApiGameInfo.Impl?
) {

    companion object {
        fun from(request: UploadLocalModifiedRequest): UploadModifiedApiRequest =
                when (request) {
                    is UploadLocalModifiedRequest.Created -> UploadModifiedApiRequest(
                            type = LocalActionTypeApi.CREATE,
                            localId = request.record.id,
                            id = null,
                            createdId = request.localCreateId,
                            lastLocalModifiedTimestamp = request.lastLocalModifiedTimestamp,
                            lastActionId = null,
                            baseGameInfo = request.record.toBaseRemoteApiGameInfo()
                    )
                    is UploadLocalModifiedRequest.Updated -> UploadModifiedApiRequest(
                            type = LocalActionTypeApi.UPDATE,
                            localId = request.record.id,
                            id = request.remoteId,
                            createdId = null,
                            lastLocalModifiedTimestamp = request.lastLocalModifiedTimestamp,
                            lastActionId = request.remoteActionId,
                            baseGameInfo = request.record.toBaseRemoteApiGameInfo()
                    )
                    is UploadLocalModifiedRequest.Deleted -> UploadModifiedApiRequest(
                            type = LocalActionTypeApi.DELETE,
                            localId = request.localRecordId,
                            id = request.remoteId,
                            createdId = null,
                            lastLocalModifiedTimestamp = null,
                            lastActionId = request.remoteActionId,
                            baseGameInfo = null
                    )
                }
    }
}