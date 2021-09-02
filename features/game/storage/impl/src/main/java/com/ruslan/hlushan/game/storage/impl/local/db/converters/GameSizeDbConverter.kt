package com.ruslan.hlushan.game.storage.impl.local.db.converters

import androidx.room.TypeConverter
import com.ruslan.hlushan.game.api.play.dto.GameSize

internal class GameSizeDbConverter {

    @TypeConverter
    fun fromIntToGameSize(value: Int?): GameSize? = value?.let { nonNullValue ->
        GameSize.values()
                .firstOrNull { gameSize -> gameSize.databaseId == nonNullValue }
    }

    @TypeConverter
    fun fromGameSizeToInt(value: GameSize?): Int? = value?.databaseId
}

private val GameSize.databaseId: Int get() = this.countRowsAndColumns