package com.ruslan.hlushan.game.api.di.providers

import com.ruslan.hlushan.game.api.top.TopInteractor

interface TopInteractorProvider {

    fun provideTopInteractor(): TopInteractor
}