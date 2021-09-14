package com.ruslan.hlushan.game.di

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.di.DatabaseViewInfoListProvider
import com.ruslan.hlushan.core.api.di.IBaseInjector
import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.api.di.SchedulersProvider
import com.ruslan.hlushan.core.api.di.ToolsProvider
import com.ruslan.hlushan.core.api.dto.DatabaseViewInfo
import com.ruslan.hlushan.core.api.dto.InitAppConfig
import com.ruslan.hlushan.core.error.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.impl.di.CoreImplExportComponent
import com.ruslan.hlushan.core.impl.tools.createToolsProvider
import com.ruslan.hlushan.core.language.api.di.LanguagesProvider
import com.ruslan.hlushan.core.language.impl.di.LanguageImplExportComponent
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.logger.impl.di.LoggerImplExportComponent
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.impl.di.UiCoreImplExportComponent
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
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
import com.ruslan.hlushan.third_party.androidx.work.manager.utils.CompositeWorkerFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [GameAppModule::class],
        dependencies = [
            ManagersProvider::class,
            LoggersProvider::class,
            LanguagesProvider::class,
            SchedulersProvider::class,
            AppContextProvider::class,
            UiCoreProvider::class,
            UiRoutingProvider::class,
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
                                      ManagersProvider,
                                      LoggersProvider,
                                      LanguagesProvider,
                                      SchedulersProvider,
                                      UiCoreProvider,
                                      UiRoutingProvider,
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
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                languagesProvider: LanguagesProvider,
                schedulersProvider: SchedulersProvider,
                appContextProvider: AppContextProvider,
                uiCoreProvider: UiCoreProvider,
                uiRoutingProvider: UiRoutingProvider,
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

            val coreImplProvider = CoreImplExportComponent.Initializer.init(
                    application = app,
                    initAppConfig = initAppConfig
            )

            val loggersProvider = LoggerImplExportComponent.Initializer.init(
                    initAppConfig = initAppConfig,
                    errorLogger = ErrorLoggerImpl(),
                    appContextProvider = coreImplProvider
            )

            val languagesProvider = LanguageImplExportComponent.Initializer.init(
                    initAppConfig = initAppConfig,
                    schedulersProvider = coreImplProvider,
                    managersProvider = coreImplProvider
            )

            val toolsProvider = createToolsProvider(
                    appContextProvider = coreImplProvider,
                    loggersProvider = loggersProvider
            )

            val uiCoreProvider = UiCoreImplExportComponent.Initializer.init(
                    external = emptyList(),
                    managersProvider = coreImplProvider,
                    loggersProvider = loggersProvider,
                    appContextProvider = coreImplProvider
            )

            val networkBuildHelperProvider = NetworkImplExportComponent.Initializer.init()

            val authRepoExportComponentProvider = AuthRepoExportComponentProvider.Initializer.init(
                    initAppConfig = initAppConfig,
                    networkConfig = networkConfig,
                    gameNetworkParams = gameNetworkParams,
                    appContextProvider = coreImplProvider,
                    loggersProvider = loggersProvider,
                    schedulersProvider = coreImplProvider,
                    networkBuildHelperProvider = networkBuildHelperProvider
            )

            val authRepoHolder = authRepoExportComponentProvider.provideAuthRepoHolder()

            val authorizedNetworkApiCreatorProvider = AuthHelpersExportComponentProvider.Initializer.init(
                    initAppConfig = initAppConfig,
                    networkConfig = networkConfig,
                    authRepoHolder = authRepoHolder,
                    appContextProvider = coreImplProvider,
                    loggersProvider = loggersProvider,
                    networkBuildHelperProvider = networkBuildHelperProvider
            )

            val gameRecordsFeatureProvider = RecordsExportComponentProvider.Initializer.init(
                    gameNetworkParams = gameNetworkParams,
                    appContextProvider = coreImplProvider,
                    loggersProvider = loggersProvider,
                    schedulersProvider = coreImplProvider,
                    authorizedNetworkApiCreatorProvider = authorizedNetworkApiCreatorProvider
            )

            val topInteractorProvider = TopInteractorExportComponentProvider.Initializer.init(
                    gameNetworkParams = gameNetworkParams,
                    loggersProvider = loggersProvider,
                    schedulersProvider = coreImplProvider,
                    nonAuthorizedNetworkApiCreatorProvider = authRepoExportComponentProvider
            )

            val authInteractorProvider = AuthInteractorExportComponentProvider.Initializer.init(
                    recordsUseCasesProvider = gameRecordsFeatureProvider,
                    authRepoHolder = authRepoHolder,
                    schedulersProvider = coreImplProvider
            )

            val allAppDatabases: List<DatabaseViewInfo> = gameRecordsFeatureProvider.provideDatabases()

            val compositeWorkerFactory = CompositeWorkerFactory(
                    factoryProviders = listOf(gameRecordsFeatureProvider)
            )

            return DaggerGameAppComponent.factory()
                    .create(
                            allAppDatabases = allAppDatabases,
                            compositeWorkerFactory = compositeWorkerFactory,
                            managersProvider = coreImplProvider,
                            loggersProvider = loggersProvider,
                            languagesProvider = languagesProvider,
                            schedulersProvider = coreImplProvider,
                            appContextProvider = coreImplProvider,
                            uiCoreProvider = uiCoreProvider,
                            uiRoutingProvider = uiCoreProvider,
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