package com.ruslan.hlushan.game.api.di.providers

import com.ruslan.hlushan.game.api.auth.AuthInteractor

interface AuthInteractorProvider {

    fun provideAuthInteractor(): AuthInteractor
}