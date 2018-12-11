package com.ruslan.hlushan.game.storage.impl.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ruslan.hlushan.core.api.dto.DatabaseViewInfo
import com.ruslan.hlushan.game.storage.impl.local.db.converters.DurationDbConverter
import com.ruslan.hlushan.game.storage.impl.local.db.converters.GameSizeDbConverter
import com.ruslan.hlushan.game.storage.impl.local.db.converters.InstantDbConverter
import com.ruslan.hlushan.game.storage.impl.local.db.converters.IntArrayDbConverter
import com.ruslan.hlushan.game.storage.impl.local.db.converters.IntListDbConverter
import com.ruslan.hlushan.game.storage.impl.local.db.converters.LocalActionTypeDbConverter
import com.ruslan.hlushan.game.storage.impl.local.db.converters.SyncStatusDbConverter
import com.ruslan.hlushan.game.storage.impl.local.db.dao.GameRecordsDAO
import com.ruslan.hlushan.game.storage.impl.local.db.entities.GameStateDb
import com.ruslan.hlushan.game.storage.impl.local.db.entities.MatrixAndNewItemsStateDb

@SuppressWarnings("UnnecessaryAbstractClass")
@Database(
        entities = [
            GameStateDb::class,
            MatrixAndNewItemsStateDb::class
        ],
        version = 2,
        exportSchema = true
)
@TypeConverters(
        InstantDbConverter::class,
        DurationDbConverter::class,
        IntArrayDbConverter::class,
        IntListDbConverter::class,
        SyncStatusDbConverter::class,
        LocalActionTypeDbConverter::class,
        GameSizeDbConverter::class
)
internal abstract class GameDatabase : RoomDatabase() {

    @SuppressWarnings("ClassOrdering")
    companion object {

        private const val DB_NAME = "GameDatabase.db"

        fun create(context: Context): GameDatabase =
                Room.databaseBuilder(context.applicationContext, GameDatabase::class.java, DB_NAME)
                        .fallbackToDestructiveMigration()
                        .build()

        fun createInfo(): DatabaseViewInfo =
                DatabaseViewInfo(
                        clazz = GameDatabase::class.java,
                        name = DB_NAME
                )
    }

    abstract fun gameRecordsDAO(): GameRecordsDAO
}