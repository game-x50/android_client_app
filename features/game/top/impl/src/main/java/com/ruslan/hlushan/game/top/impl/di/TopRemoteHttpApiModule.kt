package com.ruslan.hlushan.game.top.impl.di

import com.ruslan.hlushan.game.api.network.GameNetworkParams
import com.ruslan.hlushan.game.api.network.NonAuthorizedNetworkApiCreator
import com.ruslan.hlushan.game.top.impl.remote.TopRemoteHttpApi
import dagger.Module
import dagger.Provides

@Module
internal object TopRemoteHttpApiModule {

    @JvmStatic
    @Provides
    fun provideTopRemoteHttpApi(
            nonAuthorizedNetworkApiCreator: NonAuthorizedNetworkApiCreator,
            gameNetworkParams: GameNetworkParams
    ): TopRemoteHttpApi =
            nonAuthorizedNetworkApiCreator.createApi(
                    service = TopRemoteHttpApi::class,
                    cacheFolderName = "top",
                    baseUrl = gameNetworkParams.baseApiUrl
            )
}