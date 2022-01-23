package com.ruslan.hlushan.game.api.play.dto

import org.threeten.bp.Instant

data class RemoteInfo(
        val remoteId: RemoteInfo.Id,
        val remoteActionId: RemoteInfo.ActionId,
        val remoteCreatedTimestamp: RemoteInfo.CreatedTimestamp,
        val lastRemoteSyncedTimestamp: RemoteInfo.LastSyncedTimestamp
) {
    init {
        if (remoteCreatedTimestamp.value > lastRemoteSyncedTimestamp.value) {
            throw IllegalRemoteTimestampsException(remoteCreatedTimestamp, lastRemoteSyncedTimestamp)
        }
    }

    @JvmInline
    value class Id(val value: String)

    @JvmInline
    value class ActionId(val value: String)

    @JvmInline
    value class CreatedTimestamp(val value: Instant) : Comparable<CreatedTimestamp> {

        override fun compareTo(other: CreatedTimestamp): Int =
                this.value.compareTo(other.value)

        companion object {

            fun min(): RemoteInfo.CreatedTimestamp =
                    RemoteInfo.CreatedTimestamp(
                            value = Instant.ofEpochMilli(0)
                    )

            fun now(): RemoteInfo.CreatedTimestamp =
                    RemoteInfo.CreatedTimestamp(
                            value = Instant.now()
                    )
        }
    }

    @JvmInline
    value class LastSyncedTimestamp(val value: Instant) {
        companion object {
            fun now(): RemoteInfo.LastSyncedTimestamp =
                    RemoteInfo.LastSyncedTimestamp(
                            value = Instant.now()
                    )
        }
    }
}