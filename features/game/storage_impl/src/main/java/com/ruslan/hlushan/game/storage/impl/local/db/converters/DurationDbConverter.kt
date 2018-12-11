package com.ruslan.hlushan.game.storage.impl.local.db.converters

import androidx.room.TypeConverter
import org.threeten.bp.Duration

internal class DurationDbConverter {

    @TypeConverter
    fun fromLongToDuration(value: Long?): Duration? = value?.let { nonNullValue -> Duration.ofMillis(nonNullValue) }

    @TypeConverter
    fun fromDurationToLong(value: Duration?): Long? = value?.toMillis()
}