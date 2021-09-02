package com.ruslan.hlushan.game.api.di.providers

import com.ruslan.hlushan.game.api.play.PlayRecordsInteractor

interface PlayRecordsInteractorProvider {

    fun providePlayRecordsInteractor(): PlayRecordsInteractor
}