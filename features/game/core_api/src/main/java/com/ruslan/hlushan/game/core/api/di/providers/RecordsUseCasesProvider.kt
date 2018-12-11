package com.ruslan.hlushan.game.core.api.di.providers

import com.ruslan.hlushan.game.core.api.play.ClearAllLocalGamesInfoUseCase
import com.ruslan.hlushan.game.core.api.sync.StartSyncUseCase

interface RecordsUseCasesProvider {

    fun provideStartSyncUseCase(): StartSyncUseCase

    fun provideClearAllLocalGamesInfoUseCase(): ClearAllLocalGamesInfoUseCase
}