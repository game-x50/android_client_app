package com.ruslan.hlushan.game.settings.ui.di

import com.github.terrakok.cicerone.Screen

interface SettingsOutScreenCreator {

    fun createProfileScreen(): Screen

    fun createTopScreen(): Screen
}

interface SettingsOutScreenCreatorProvider {

    fun provideSettingsOutScreenCreator(): SettingsOutScreenCreator
}