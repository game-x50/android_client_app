package com.ruslan.hlushan.game.play.ui.di

import com.ruslan.hlushan.game.play.ui.GameScopeMarkerRepository
import dagger.Module
import dagger.Provides

@Module
internal object AssistedGamePlayUiViewModelsModule {

    @JvmStatic
    @PlayUiScope
    @Provides
    fun provideGameScopeMarkerRepository(): GameScopeMarkerRepository = GameScopeMarkerRepository()
}