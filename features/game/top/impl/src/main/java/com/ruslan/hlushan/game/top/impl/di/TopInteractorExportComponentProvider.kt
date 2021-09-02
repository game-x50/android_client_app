package com.ruslan.hlushan.game.top.impl.di

import com.ruslan.hlushan.core.api.di.LoggersProvider
import com.ruslan.hlushan.core.api.di.SchedulersProvider
import com.ruslan.hlushan.game.api.di.providers.NonAuthorizedNetworkApiCreatorProvider
import com.ruslan.hlushan.game.api.di.providers.TopInteractorProvider
import com.ruslan.hlushan.game.api.network.GameNetworkParams
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [TopModule::class],
        dependencies = [
            LoggersProvider::class,
            SchedulersProvider::class,
            NonAuthorizedNetworkApiCreatorProvider::class
        ]
)
interface TopInteractorExportComponentProvider : TopInteractorProvider {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance gameNetworkParams: GameNetworkParams,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersProvider,
                nonAuthorizedNetworkApiCreatorProvider: NonAuthorizedNetworkApiCreatorProvider
        ): TopInteractorExportComponentProvider
    }

    object Initializer {
        fun init(
                gameNetworkParams: GameNetworkParams,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersProvider,
                nonAuthorizedNetworkApiCreatorProvider: NonAuthorizedNetworkApiCreatorProvider
        ): TopInteractorExportComponentProvider =
                DaggerTopInteractorExportComponentProvider.factory()
                        .create(
                                gameNetworkParams = gameNetworkParams,
                                loggersProvider = loggersProvider,
                                schedulersProvider = schedulersProvider,
                                nonAuthorizedNetworkApiCreatorProvider = nonAuthorizedNetworkApiCreatorProvider
                        )
    }
}