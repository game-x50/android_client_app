package com.ruslan.hlushan.game.api.play.dto

import org.threeten.bp.Instant

data class RemoteInfo(
        val remoteId: String,
        val remoteActionId: String,
        val remoteCreatedTimestamp: Instant,
        val lastRemoteSyncedTimestamp: Instant
) {
    init {
        if (remoteCreatedTimestamp > lastRemoteSyncedTimestamp) {
            throw IllegalRemoteTimestampsException(remoteCreatedTimestamp, lastRemoteSyncedTimestamp)
        }
    }
}