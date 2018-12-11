package com.ruslan.hlushan.game.storage.impl.di

import com.ruslan.hlushan.game.core.api.network.AuthorizedNetworkApiCreator
import com.ruslan.hlushan.game.core.api.network.GameNetworkParams
import com.ruslan.hlushan.game.storage.impl.remote.SyncRemoteHttpApi
import dagger.Module
import dagger.Provides

/**
 * @author Ruslan Hlushan on 2019-07-26
 */
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