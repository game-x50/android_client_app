package com.ruslan.hlushan.game.storage.impl.local.db.converters

import androidx.room.TypeConverter

private const val SEPARATOR = ","

internal class IntListDbConverter {

    @TypeConverter
    fun fromStringToIntList(value: String?): List<Int>? =
            value?.split(SEPARATOR)
                    ?.mapNotNull { singleString -> singleString.toIntOrNull() }

    @TypeConverter
    fun fromIntListToString(list: List<Int>?): String? = list?.joinToString(separator = SEPARATOR)
}