package com.ruslan.hlushan.game.auth.impl.di.repo

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.config.app.InitAppConfig
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.game.api.di.providers.NonAuthorizedNetworkApiCreatorProvider
import com.ruslan.hlushan.game.api.network.GameNetworkParams
import com.ruslan.hlushan.game.auth.impl.repo.AuthRepository
import com.ruslan.hlushan.network.api.NetworkConfig
import com.ruslan.hlushan.network.api.di.NetworkBuildHelperProvider
import com.ruslan.hlushan.third_party.rxjava2.extensions.di.SchedulersManagerProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [AuthRepoModule::class],
        dependencies = [
            AppContextProvider::class,
            LoggersProvider::class,
            SchedulersManagerProvider::class,
            NetworkBuildHelperProvider::class
        ]
)
interface AuthRepoExportComponentProvider : NonAuthorizedNetworkApiCreatorProvider {

    fun provideAuthRepoHolder(): AuthRepoHolder

    @Component.Factory
    interface Factory {
        @SuppressWarnings("LongParameterList")
        fun create(
                @BindsInstance initAppConfig: InitAppConfig,
                @BindsInstance networkConfig: NetworkConfig,
                @BindsInstance gameNetworkParams: GameNetworkParams,
                appContextProvider: AppContextProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersManagerProvider,
                networkBuildHelperProvider: NetworkBuildHelperProvider
        ): AuthRepoExportComponentProvider
    }

    object Initializer {
        @SuppressWarnings("LongParameterList")
        fun init(
                initAppConfig: InitAppConfig,
                networkConfig: NetworkConfig,
                gameNetworkParams: GameNetworkParams,
                appContextProvider: AppContextProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersManagerProvider,
                networkBuildHelperProvider: NetworkBuildHelperProvider
        ): AuthRepoExportComponentProvider =
                DaggerAuthRepoExportComponentProvider.factory()
                        .create(
                                initAppConfig = initAppConfig,
                                networkConfig = networkConfig,
                                gameNetworkParams = gameNetworkParams,
                                appContextProvider = appContextProvider,
                                loggersProvider = loggersProvider,
                                schedulersProvider = schedulersProvider,
                                networkBuildHelperProvider = networkBuildHelperProvider
                        )
    }
}

class AuthRepoHolder internal constructor(internal val repo: AuthRepository)