package com.ruslan.hlushan.game.storage.impl.di

import com.ruslan.hlushan.game.api.GameSettings
import com.ruslan.hlushan.game.api.play.ClearAllLocalGamesInfoUseCase
import com.ruslan.hlushan.game.api.play.PlayRecordsInteractor
import com.ruslan.hlushan.game.api.sync.StartSyncUseCase
import com.ruslan.hlushan.game.storage.impl.ClearAllLocalGamesInfoUseCaseImpl
import com.ruslan.hlushan.game.storage.impl.GameSettingsImpl
import com.ruslan.hlushan.game.storage.impl.PlayRecordsInteractorImpl
import com.ruslan.hlushan.game.storage.impl.StartSyncUseCaseImpl
import com.ruslan.hlushan.game.storage.impl.SyncInteractor
import com.ruslan.hlushan.game.storage.impl.SyncInteractorImpl
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepository
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepositoryImpl
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepositoryStorage
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepositoryStorageImpl
import com.ruslan.hlushan.game.storage.impl.remote.SyncRemoteRepository
import com.ruslan.hlushan.game.storage.impl.remote.SyncRemoteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Reusable
import javax.inject.Singleton

@Module(
        includes = [
            GameDatabaseModule::class,
            SyncRemoteHttpApiModule::class
        ]
)
internal interface RecordsModule {

    @Binds
    fun provideLocalRecordsRepositoryStorage(impl: LocalRecordsRepositoryStorageImpl): LocalRecordsRepositoryStorage

    @Binds
    @Singleton
    fun provideLocalRecordsRepository(impl: LocalRecordsRepositoryImpl): LocalRecordsRepository

    @Binds
    @Singleton
    fun provideSyncRemoteRepository(impl: SyncRemoteRepositoryImpl): SyncRemoteRepository

    @Binds
    @Singleton
    fun provideGameSettings(impl: GameSettingsImpl): GameSettings

    @Binds
    @Reusable
    fun providePlayRecordsInteractor(impl: PlayRecordsInteractorImpl): PlayRecordsInteractor

    @Binds
    fun provideSyncInteractor(impl: SyncInteractorImpl): SyncInteractor

    @Binds
    fun provideStartSyncUseCase(impl: StartSyncUseCaseImpl): StartSyncUseCase

    @Binds
    fun provideClearAllLocalGamesInfoUseCase(impl: ClearAllLocalGamesInfoUseCaseImpl): ClearAllLocalGamesInfoUseCase
}