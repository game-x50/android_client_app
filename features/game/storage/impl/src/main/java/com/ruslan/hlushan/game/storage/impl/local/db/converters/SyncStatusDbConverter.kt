package com.ruslan.hlushan.game.storage.impl.local.db.converters

import androidx.room.TypeConverter
import com.ruslan.hlushan.game.core.api.play.dto.SyncStatus

internal class SyncStatusDbConverter {

    @TypeConverter
    fun fromStringToSyncStatus(value: String?): SyncStatus? = value?.let { nonNullValue ->
        SyncStatus.values()
                .firstOrNull { syncStatus -> syncStatus.databaseId == nonNullValue }
    }

    @TypeConverter
    fun fromSyncStatusToString(value: SyncStatus?): String? = value?.databaseId
}

private val SyncStatus.databaseId: String
    get() = when (this) {
        SyncStatus.SYNCED        -> "SYNCED"
        SyncStatus.WAITING       -> "WAITING"
        SyncStatus.SYNCHRONIZING -> "SYNCHRONIZING"
    }