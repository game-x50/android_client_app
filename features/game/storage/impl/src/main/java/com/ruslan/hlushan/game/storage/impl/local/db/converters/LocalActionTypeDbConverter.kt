package com.ruslan.hlushan.game.storage.impl.local.db.converters

import androidx.room.TypeConverter
import com.ruslan.hlushan.game.storage.impl.local.db.entities.LocalActionTypeDb

internal class LocalActionTypeDbConverter {

    @TypeConverter
    fun fromStringToLocalAction(value: String?): LocalActionTypeDb? = value?.let { nonNullValue ->
        LocalActionTypeDb.values()
                .firstOrNull { localAction -> localAction.databaseId == nonNullValue }
    }

    @TypeConverter
    fun fromLocalActionToString(value: LocalActionTypeDb?): String? = value?.databaseId
}

private val LocalActionTypeDb.databaseId: String
    get() = when (this) {
        LocalActionTypeDb.CREATE -> "C"
        LocalActionTypeDb.UPDATE -> "U"
        LocalActionTypeDb.DELETE -> "D"
    }