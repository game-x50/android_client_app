package com.ruslan.hlushan.game.storage.impl.di

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.di.DatabaseViewInfoListProvider
import com.ruslan.hlushan.core.foreground.observer.api.di.AppForegroundObserverProvider
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.game.api.di.providers.AuthorizedNetworkApiCreatorProvider
import com.ruslan.hlushan.game.api.di.providers.GameSettingsProvider
import com.ruslan.hlushan.game.api.di.providers.PlayRecordsInteractorProvider
import com.ruslan.hlushan.game.api.di.providers.RecordsUseCasesProvider
import com.ruslan.hlushan.game.api.network.GameNetworkParams
import com.ruslan.hlushan.third_party.androidx.work.manager.utils.di.WorkerFactoryProvider
import com.ruslan.hlushan.third_party.rxjava2.extensions.di.SchedulersManagerProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            RecordsModule::class,
            WorkersFactoryModule::class
        ],
        dependencies = [
            AppContextProvider::class,
            LoggersProvider::class,
            SchedulersManagerProvider::class,
            AppForegroundObserverProvider::class,
            AuthorizedNetworkApiCreatorProvider::class
        ]
)
interface RecordsExportComponentProvider : GameSettingsProvider,
                                           PlayRecordsInteractorProvider,
                                           RecordsUseCasesProvider,
                                           DatabaseViewInfoListProvider,
                                           WorkerFactoryProvider {

    @Component.Factory
    interface Factory {
        @SuppressWarnings("LongParameterList")
        fun create(
                @BindsInstance gameNetworkParams: GameNetworkParams,
                appContextProvider: AppContextProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersManagerProvider,
                appForegroundObserverProvider: AppForegroundObserverProvider,
                authorizedNetworkApiCreatorProvider: AuthorizedNetworkApiCreatorProvider
        ): RecordsExportComponentProvider
    }

    object Initializer {
        @SuppressWarnings("LongParameterList")
        fun init(
                gameNetworkParams: GameNetworkParams,
                appContextProvider: AppContextProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersManagerProvider,
                appForegroundObserverProvider: AppForegroundObserverProvider,
                authorizedNetworkApiCreatorProvider: AuthorizedNetworkApiCreatorProvider
        ): RecordsExportComponentProvider =
                DaggerRecordsExportComponentProvider.factory()
                        .create(
                                gameNetworkParams = gameNetworkParams,
                                appContextProvider = appContextProvider,
                                loggersProvider = loggersProvider,
                                schedulersProvider = schedulersProvider,
                                appForegroundObserverProvider = appForegroundObserverProvider,
                                authorizedNetworkApiCreatorProvider = authorizedNetworkApiCreatorProvider
                        )
    }
}