package com.ruslan.hlushan.game.api.di.providers

import com.ruslan.hlushan.game.api.play.ClearAllLocalGamesInfoUseCase
import com.ruslan.hlushan.game.api.sync.StartSyncUseCase

interface RecordsUseCasesProvider {

    fun provideStartSyncUseCase(): StartSyncUseCase

    fun provideClearAllLocalGamesInfoUseCase(): ClearAllLocalGamesInfoUseCase
}