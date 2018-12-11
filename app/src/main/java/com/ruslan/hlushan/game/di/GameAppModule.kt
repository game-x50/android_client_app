package com.ruslan.hlushan.game.di

import com.ruslan.hlushan.game.screens.routing.SettingsOutScreenCreatorImpl
import com.ruslan.hlushan.game.settings.ui.di.SettingsOutScreenCreator
import dagger.Binds
import dagger.Module

@Module
internal interface GameAppModule {

    @Binds
    fun provideSettingsOutScreenCreator(impl: SettingsOutScreenCreatorImpl): SettingsOutScreenCreator
}