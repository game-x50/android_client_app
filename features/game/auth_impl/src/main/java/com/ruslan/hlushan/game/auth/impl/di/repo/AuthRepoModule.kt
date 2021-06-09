package com.ruslan.hlushan.game.auth.impl.di.repo

import com.ruslan.hlushan.game.auth.impl.network.NonAuthorizedNetworkApiCreatorImpl
import com.ruslan.hlushan.game.auth.impl.repo.AuthHttpsApi
import com.ruslan.hlushan.game.auth.impl.repo.AuthRepository
import com.ruslan.hlushan.game.auth.impl.repo.AuthRepositoryImpl
import com.ruslan.hlushan.game.core.api.network.GameNetworkParams
import com.ruslan.hlushan.game.core.api.network.NonAuthorizedNetworkApiCreator
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal object AuthRepoModule {

    @JvmStatic
    @Provides
    fun provideNonAuthorizedNetworkApiCreator(
            impl: NonAuthorizedNetworkApiCreatorImpl
    ): NonAuthorizedNetworkApiCreator = impl

    @JvmStatic
    @Provides
    fun provideAuthHttpsApi(
            nonAuthorizedNetworkApiCreator: NonAuthorizedNetworkApiCreator,
            gameNetworkParams: GameNetworkParams
    ): AuthHttpsApi =
            nonAuthorizedNetworkApiCreator.createApi(
                    service = AuthHttpsApi::class,
                    cacheFolderName = "auth",
                    baseUrl = gameNetworkParams.baseApiUrl
            )

    @JvmStatic
    @Provides
    fun provideAuthRepoHolder(authRepository: AuthRepository): AuthRepoHolder = AuthRepoHolder(authRepository)

    @JvmStatic
    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl
}