package com.ruslan.hlushan.game.storage.impl.di

import android.content.Context
import com.ruslan.hlushan.game.storage.impl.local.db.GameDatabase
import com.ruslan.hlushan.game.storage.impl.local.db.dao.GameRecordsDAO
import com.ruslan.hlushan.third_party.androidx.room.utils.DatabaseViewInfo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal object GameDatabaseModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideGameDatabase(context: Context): GameDatabase = GameDatabase.create(context)

    @JvmStatic
    @Provides
    @Singleton
    fun provideRecordsDAO(gameDatabase: GameDatabase): GameRecordsDAO = gameDatabase.gameRecordsDAO()

    @JvmStatic
    @Provides
    fun provideDatabases(): List<DatabaseViewInfo> = listOf(GameDatabase.createInfo())
}