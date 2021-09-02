package com.ruslan.hlushan.game.api.di.providers

import com.ruslan.hlushan.game.api.GameSettings

interface GameSettingsProvider {

    fun provideGameSettings(): GameSettings
}