package com.ruslan.hlushan.game.auth.impl.di.helpers

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.utils.InitAppConfig
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.game.api.di.providers.AuthorizedNetworkApiCreatorProvider
import com.ruslan.hlushan.game.auth.impl.di.repo.AuthRepoHolder
import com.ruslan.hlushan.network.api.NetworkConfig
import com.ruslan.hlushan.network.api.di.NetworkBuildHelperProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [AuthHelpersModule::class],
        dependencies = [
            AppContextProvider::class,
            LoggersProvider::class,
            NetworkBuildHelperProvider::class
        ]
)
interface AuthHelpersExportComponentProvider : AuthorizedNetworkApiCreatorProvider {

    @Component.Factory
    interface Factory {
        @SuppressWarnings("LongParameterList")
        fun create(
                @BindsInstance initAppConfig: InitAppConfig,
                @BindsInstance networkConfig: NetworkConfig,
                @BindsInstance authRepoHolder: AuthRepoHolder,
                appContextProvider: AppContextProvider,
                loggersProvider: LoggersProvider,
                networkBuildHelperProvider: NetworkBuildHelperProvider
        ): AuthHelpersExportComponentProvider
    }

    object Initializer {
        @SuppressWarnings("LongParameterList")
        fun init(
                initAppConfig: InitAppConfig,
                networkConfig: NetworkConfig,
                authRepoHolder: AuthRepoHolder,
                appContextProvider: AppContextProvider,
                loggersProvider: LoggersProvider,
                networkBuildHelperProvider: NetworkBuildHelperProvider
        ): AuthorizedNetworkApiCreatorProvider =
                DaggerAuthHelpersExportComponentProvider.factory()
                        .create(
                                initAppConfig = initAppConfig,
                                networkConfig = networkConfig,
                                authRepoHolder = authRepoHolder,
                                appContextProvider = appContextProvider,
                                loggersProvider = loggersProvider,
                                networkBuildHelperProvider = networkBuildHelperProvider
                        )
    }
}