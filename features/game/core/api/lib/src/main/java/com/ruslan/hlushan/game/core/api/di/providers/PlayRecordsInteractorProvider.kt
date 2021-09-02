package com.ruslan.hlushan.game.core.api.di.providers

import com.ruslan.hlushan.game.core.api.play.PlayRecordsInteractor

interface PlayRecordsInteractorProvider {

    fun providePlayRecordsInteractor(): PlayRecordsInteractor
}