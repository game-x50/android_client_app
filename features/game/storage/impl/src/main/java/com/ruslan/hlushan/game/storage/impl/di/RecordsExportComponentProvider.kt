package com.ruslan.hlushan.game.storage.impl.di

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.di.DatabaseViewInfoListProvider
import com.ruslan.hlushan.core.api.di.LoggersProvider
import com.ruslan.hlushan.core.api.di.SchedulersProvider
import com.ruslan.hlushan.game.core.api.di.providers.AuthorizedNetworkApiCreatorProvider
import com.ruslan.hlushan.game.core.api.di.providers.GameSettingsProvider
import com.ruslan.hlushan.game.core.api.di.providers.PlayRecordsInteractorProvider
import com.ruslan.hlushan.game.core.api.di.providers.RecordsUseCasesProvider
import com.ruslan.hlushan.game.core.api.network.GameNetworkParams
import com.ruslan.hlushan.work.manager.utils.di.WorkerFactoryProvider
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
            SchedulersProvider::class,
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
        fun create(
                @BindsInstance gameNetworkParams: GameNetworkParams,
                appContextProvider: AppContextProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersProvider,
                authorizedNetworkApiCreatorProvider: AuthorizedNetworkApiCreatorProvider
        ): RecordsExportComponentProvider
    }

    object Initializer {
        fun init(
                gameNetworkParams: GameNetworkParams,
                appContextProvider: AppContextProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersProvider,
                authorizedNetworkApiCreatorProvider: AuthorizedNetworkApiCreatorProvider
        ): RecordsExportComponentProvider =
                DaggerRecordsExportComponentProvider.factory()
                        .create(
                                gameNetworkParams = gameNetworkParams,
                                appContextProvider = appContextProvider,
                                loggersProvider = loggersProvider,
                                schedulersProvider = schedulersProvider,
                                authorizedNetworkApiCreatorProvider = authorizedNetworkApiCreatorProvider
                        )
    }
}