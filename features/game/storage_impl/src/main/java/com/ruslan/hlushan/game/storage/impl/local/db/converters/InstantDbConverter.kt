package com.ruslan.hlushan.game.storage.impl.local.db.converters

import androidx.room.TypeConverter
import org.threeten.bp.Instant

internal class InstantDbConverter {

    @TypeConverter
    fun fromTimestampToInstant(value: Long?): Instant? =
            value?.let { nonNullValue -> Instant.ofEpochMilli(nonNullValue) }

    @TypeConverter
    fun fromInstantToTimestamp(instant: Instant?): Long? = instant?.toEpochMilli()
}