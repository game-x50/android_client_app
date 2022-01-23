package com.ruslan.hlushan.game.storage.impl.remote.dto

import com.ruslan.hlushan.game.api.play.dto.RemoteInfo

internal sealed class UpdateLocalNonModifiedResponse {

    abstract val remoteId: RemoteInfo.Id

    data class NoChanges(
            override val remoteId: RemoteInfo.Id,
            val lastRemoteSyncedTimestamp: RemoteInfo.LastSyncedTimestamp
    ) : UpdateLocalNonModifiedResponse()

    data class Changed(
            override val remoteId: RemoteInfo.Id,
            val remoteRecord: RemoteRecord
    ) : UpdateLocalNonModifiedResponse()

    data class Deleted(override val remoteId: RemoteInfo.Id) : UpdateLocalNonModifiedResponse()

    data class Fail(override val remoteId: RemoteInfo.Id) : UpdateLocalNonModifiedResponse()
}