package com.ruslan.hlushan.game.storage.impl.di

import com.ruslan.hlushan.game.api.network.AuthorizedNetworkApiCreator
import com.ruslan.hlushan.game.api.network.GameNetworkParams
import com.ruslan.hlushan.game.storage.impl.remote.SyncRemoteHttpApi
import dagger.Module
import dagger.Provides

@Module
internal object SyncRemoteHttpApiModule {

    @JvmStatic
    @Provides
    fun provideSyncRemoteHttpApi(
            authorizedNetworkApiCreator: AuthorizedNetworkApiCreator,
            gameNetworkParams: GameNetworkParams
    ): SyncRemoteHttpApi =
            authorizedNetworkApiCreator.createAuthorizedApi(
                    service = SyncRemoteHttpApi::class,
                    cacheFolderName = "auth",
                    baseUrl = gameNetworkParams.baseApiUrl
            )
}