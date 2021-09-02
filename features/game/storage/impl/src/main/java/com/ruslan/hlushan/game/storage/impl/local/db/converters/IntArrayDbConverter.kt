package com.ruslan.hlushan.game.storage.impl.local.db.converters

import androidx.room.TypeConverter

private const val SEPARATOR = ","

internal class IntArrayDbConverter {

    @TypeConverter
    fun fromStringToIntArray(value: String?): IntArray? =
            value?.split(SEPARATOR)
                    ?.mapNotNull { singleString -> singleString.toIntOrNull() }
                    ?.toIntArray()

    @TypeConverter
    fun fromIntArrayToString(intArray: IntArray?): String? = intArray?.joinToString(separator = SEPARATOR)
}