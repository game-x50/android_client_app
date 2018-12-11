package com.ruslan.hlushan.game.core.api.di.providers

import com.ruslan.hlushan.game.core.api.GameSettings

interface GameSettingsProvider {

    fun provideGameSettings(): GameSettings
}