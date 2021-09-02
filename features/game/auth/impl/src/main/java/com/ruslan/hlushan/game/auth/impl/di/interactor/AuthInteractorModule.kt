package com.ruslan.hlushan.game.auth.impl.di.interactor

import com.ruslan.hlushan.game.auth.impl.di.repo.AuthRepoHolder
import com.ruslan.hlushan.game.auth.impl.interactor.AuthInteractorImpl
import com.ruslan.hlushan.game.auth.impl.repo.AuthRepository
import com.ruslan.hlushan.game.core.api.auth.AuthInteractor
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
internal object AuthInteractorModule {

    @JvmStatic
    @Provides
    fun provideAuthRepo(authRepoHolder: AuthRepoHolder): AuthRepository = authRepoHolder.repo

    @JvmStatic
    @Provides
    @Reusable
    fun provideAuthInteractor(impl: AuthInteractorImpl): AuthInteractor = impl
}