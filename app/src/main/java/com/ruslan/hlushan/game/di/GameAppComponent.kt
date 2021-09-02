package com.ruslan.hlushan.game.di

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.di.CoreProvider
import com.ruslan.hlushan.core.api.di.DatabaseViewInfoListProvider
import com.ruslan.hlushan.core.api.di.IBaseInjector
import com.ruslan.hlushan.core.api.di.ToolsProvider
import com.ruslan.hlushan.core.api.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.api.dto.DatabaseViewInfo
import com.ruslan.hlushan.core.api.utils.InitAppConfig
import com.ruslan.hlushan.core.impl.di.CoreImplExportComponent
import com.ruslan.hlushan.core.impl.tools.createToolsProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.impl.di.UiCoreImplExportComponent
import com.ruslan.hlushan.game.BuildConfig
import com.ruslan.hlushan.game.GameApp
import com.ruslan.hlushan.game.api.di.providers.AuthInteractorProvider
import com.ruslan.hlushan.game.api.di.providers.GameSettingsProvider
import com.ruslan.hlushan.game.api.di.providers.PlayRecordsInteractorProvider
import com.ruslan.hlushan.game.api.di.providers.RecordsUseCasesProvider
import com.ruslan.hlushan.game.api.di.providers.TopInteractorProvider
import com.ruslan.hlushan.game.api.network.GameNetworkParams
import com.ruslan.hlushan.game.auth.impl.di.helpers.AuthHelpersExportComponentProvider
import com.ruslan.hlushan.game.auth.impl.di.interactor.AuthInteractorExportComponentProvider
import com.ruslan.hlushan.game.auth.impl.di.repo.AuthRepoExportComponentProvider
import com.ruslan.hlushan.game.error.ErrorLoggerImpl
import com.ruslan.hlushan.game.settings.ui.di.SettingsOutScreenCreatorProvider
import com.ruslan.hlushan.game.storage.impl.di.RecordsExportComponentProvider
import com.ruslan.hlushan.game.top.impl.di.TopInteractorExportComponentProvider
import com.ruslan.hlushan.network.api.NetworkConfig
import com.ruslan.hlushan.network.impl.di.NetworkImplExportComponent
import com.ruslan.hlushan.work.manager.utils.CompositeWorkerFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [GameAppModule::class],
        dependencies = [
            CoreProvider::class,
            AppContextProvider::class,
            UiCoreProvider::class,
            ToolsProvider::class,
            UserErrorMapperProvider::class,
            AuthInteractorProvider::class,
            GameSettingsProvider::class,
            PlayRecordsInteractorProvider::class,
            TopInteractorProvider::class,
            RecordsUseCasesProvider::class
        ]
)
internal interface GameAppComponent : IBaseInjector,
                                      CoreProvider,
                                      UiCoreProvider,
                                      ToolsProvider,
                                      DatabaseViewInfoListProvider,
                                      UserErrorMapperProvider,
                                      AuthInteractorProvider,
                                      GameSettingsProvider,
                                      PlayRecordsInteractorProvider,
                                      TopInteractorProvider,
                                      RecordsUseCasesProvider,
                                      SettingsOutScreenCreatorProvider {

    fun inject(app: GameApp)

    @Component.Factory
    interface Factory {
        @SuppressWarnings("LongParameterList")
        fun create(
                @BindsInstance allAppDatabases: List<DatabaseViewInfo>,
                @BindsInstance compositeWorkerFactory: CompositeWorkerFactory,
                coreProvider: CoreProvider,
                appContextProvider: AppContextProvider,
                uiCoreProvider: UiCoreProvider,
                toolsProvider: ToolsProvider,
                userErrorMapperProvider: UserErrorMapperProvider,
                authInteractorProvider: AuthInteractorProvider,
                playRecordsInteractorProvider: PlayRecordsInteractorProvider,
                topInteractorProvider: TopInteractorProvider,
                recordsUseCasesProvider: RecordsUseCasesProvider,
                gameSettingsProvider: GameSettingsProvider
        ): GameAppComponent
    }

    companion object Initializer {

        @SuppressWarnings("LongMethod")
        fun init(app: GameApp, initAppConfig: InitAppConfig): GameAppComponent {

            val networkConfig = NetworkConfig()
            val gameNetworkParams = GameNetworkParams(baseApiUrl = BuildConfig.BASE_API_URL)

            val coreProvider = CoreImplExportComponent.Initializer.init(
                    application = app,
                    initAppConfig = initAppConfig,
                    errorLogger = ErrorLoggerImpl()
            )

            val toolsProvider = createToolsProvider(
                    appContextProvider = coreProvider,
                    loggersProvider = coreProvider
            )

            val uiCoreProvider = UiCoreImplExportComponent.Initializer.init(
                    external = emptyList(),
                    coreProvider = coreProvider,
                    appContextProvider = coreProvider
            )

            val networkBuildHelperProvider = NetworkImplExportComponent.Initializer.init()

            val authRepoExportComponentProvider = AuthRepoExportComponentProvider.Initializer.init(
                    initAppConfig = initAppConfig,
                    networkConfig = networkConfig,
                    gameNetworkParams = gameNetworkParams,
                    appContextProvider = coreProvider,
                    loggersProvider = coreProvider,
                    schedulersProvider = coreProvider,
                    networkBuildHelperProvider = networkBuildHelperProvider
            )

            val authRepoHolder = authRepoExportComponentProvider.provideAuthRepoHolder()

            val authorizedNetworkApiCreatorProvider = AuthHelpersExportComponentProvider.Initializer.init(
                    initAppConfig = initAppConfig,
                    networkConfig = networkConfig,
                    authRepoHolder = authRepoHolder,
                    appContextProvider = coreProvider,
                    loggersProvider = coreProvider,
                    networkBuildHelperProvider = networkBuildHelperProvider
            )

            val gameRecordsFeatureProvider = RecordsExportComponentProvider.Initializer.init(
                    gameNetworkParams = gameNetworkParams,
                    appContextProvider = coreProvider,
                    loggersProvider = coreProvider,
                    schedulersProvider = coreProvider,
                    authorizedNetworkApiCreatorProvider = authorizedNetworkApiCreatorProvider
            )

            val topInteractorProvider = TopInteractorExportComponentProvider.Initializer.init(
                    gameNetworkParams = gameNetworkParams,
                    loggersProvider = coreProvider,
                    schedulersProvider = coreProvider,
                    nonAuthorizedNetworkApiCreatorProvider = authRepoExportComponentProvider
            )

            val authInteractorProvider = AuthInteractorExportComponentProvider.Initializer.init(
                    recordsUseCasesProvider = gameRecordsFeatureProvider,
                    authRepoHolder = authRepoHolder,
                    schedulersProvider = coreProvider
            )

            val allAppDatabases: List<DatabaseViewInfo> = gameRecordsFeatureProvider.provideDatabases()

            val compositeWorkerFactory = CompositeWorkerFactory(
                    factoryProviders = listOf(gameRecordsFeatureProvider)
            )

            return DaggerGameAppComponent.factory()
                    .create(
                            allAppDatabases = allAppDatabases,
                            compositeWorkerFactory = compositeWorkerFactory,
                            coreProvider = coreProvider,
                            appContextProvider = coreProvider,
                            uiCoreProvider = uiCoreProvider,
                            toolsProvider = toolsProvider,
                            userErrorMapperProvider = uiCoreProvider,
                            authInteractorProvider = authInteractorProvider,
                            playRecordsInteractorProvider = gameRecordsFeatureProvider,
                            topInteractorProvider = topInteractorProvider,
                            recordsUseCasesProvider = gameRecordsFeatureProvider,
                            gameSettingsProvider = gameRecordsFeatureProvider
                    )
        }
    }
}