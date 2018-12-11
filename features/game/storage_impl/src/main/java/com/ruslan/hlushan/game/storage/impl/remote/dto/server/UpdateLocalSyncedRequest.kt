package com.ruslan.hlushan.game.storage.impl.remote.dto.server

import com.ruslan.hlushan.game.core.api.play.dto.RemoteInfo
import kotlinx.serialization.Serializable

@Serializable
internal data class UpdateLocalSyncedRequest(
        val remoteId: String,
        val remoteActionId: String
)

internal fun RemoteInfo.toUpdateLocalSyncedRequest(): UpdateLocalSyncedRequest =
        UpdateLocalSyncedRequest(
                remoteId = this.remoteId,
                remoteActionId = this.remoteActionId
        )