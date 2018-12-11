package com.ruslan.hlushan.game.storage.impl.remote.dto

import org.threeten.bp.Instant

internal sealed class UpdateLocalNonModifiedResponse {

    abstract val remoteId: String

    data class NoChanges(override val remoteId: String, val lastRemoteSyncedTimestamp: Instant) : UpdateLocalNonModifiedResponse()

    data class Changed(override val remoteId: String, val remoteRecord: RemoteRecord) : UpdateLocalNonModifiedResponse()

    data class Deleted(override val remoteId: String) : UpdateLocalNonModifiedResponse()

    data class Fail(override val remoteId: String) : UpdateLocalNonModifiedResponse()
}