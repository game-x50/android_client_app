package com.ruslan.hlushan.game.top.impl.di

import com.ruslan.hlushan.game.core.api.network.GameNetworkParams
import com.ruslan.hlushan.game.core.api.network.NonAuthorizedNetworkApiCreator
import com.ruslan.hlushan.game.top.impl.remote.TopRemoteHttpApi
import dagger.Module
import dagger.Provides

/**
 * @author Ruslan Hlushan on 2019-07-26
 */
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