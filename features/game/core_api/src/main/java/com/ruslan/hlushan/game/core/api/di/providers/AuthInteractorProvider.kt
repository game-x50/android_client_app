package com.ruslan.hlushan.game.core.api.di.providers

import com.ruslan.hlushan.game.core.api.auth.AuthInteractor

interface AuthInteractorProvider {

    fun provideAuthInteractor(): AuthInteractor
}