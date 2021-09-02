package com.ruslan.hlushan.game.core.api.di.providers

import com.ruslan.hlushan.game.core.api.top.TopInteractor

interface TopInteractorProvider {

    fun provideTopInteractor(): TopInteractor
}