package com.ruslan.hlushan.game.auth.impl.di.helpers

import com.ruslan.hlushan.game.api.network.AuthorizedNetworkApiCreator
import com.ruslan.hlushan.game.auth.impl.di.repo.AuthRepoHolder
import com.ruslan.hlushan.game.auth.impl.network.AuthorizedNetworkApiCreatorImpl
import com.ruslan.hlushan.game.auth.impl.repo.AuthRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal object AuthHelpersModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideAuthorizedNetworkApiCreator(impl: AuthorizedNetworkApiCreatorImpl): AuthorizedNetworkApiCreator = impl

    @JvmStatic
    @Provides
    fun provideAuthRepo(authRepoHolder: AuthRepoHolder): AuthRepository = authRepoHolder.repo
}