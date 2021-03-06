package com.ruslan.hlushan.game.di

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.di.ToolsProvider
import com.ruslan.hlushan.core.config.app.InitAppConfig
import com.ruslan.hlushan.core.config.app.di.InitAppConfigProvider
import com.ruslan.hlushan.core.di.IBaseInjector
import com.ruslan.hlushan.core.error.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.foreground.observer.impl.di.AppForegroundObserverImplExportComponent
import com.ruslan.hlushan.core.impl.di.CoreImplExportComponent
import com.ruslan.hlushan.core.impl.tools.createToolsProvider
import com.ruslan.hlushan.core.language.api.di.LanguagesInteractorProvider
import com.ruslan.hlushan.core.language.code.LangFullCode
import com.ruslan.hlushan.core.language.impl.di.LanguageImplExportComponent
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.logger.impl.di.LoggerImplExportComponent
import com.ruslan.hlushan.core.manager.api.di.ManagersProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.fragment.manager.FragmentManagerConfiguratorProvider
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
import com.ruslan.hlushan.third_party.androidx.room.utils.DatabaseViewInfo
import com.ruslan.hlushan.third_party.androidx.room.utils.di.DatabaseViewInfoListProvider
import com.ruslan.hlushan.third_party.androidx.work.manager.utils.CompositeWorkerFactory
import com.ruslan.hlushan.third_party.rxjava2.extensions.di.SchedulersManagerProvider
import dagger.BindsInstance
import dagger.Component
import java.io.File
import javax.inject.Singleton

@Singleton
@Component(
        modules = [GameAppModule::class],
        dependencies = [
            ManagersProvider::class,
            LoggersProvider::class,
            LanguagesInteractorProvider::class,
            SchedulersManagerProvider::class,
            AppContextProvider::class,
            UiCoreProvider::class,
            FragmentManagerConfiguratorProvider::class,
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
                                      InitAppConfigProvider,
                                      ManagersProvider,
                                      LoggersProvider,
                                      LanguagesInteractorProvider,
                                      SchedulersManagerProvider,
                                      UiCoreProvider,
                                      FragmentManagerConfiguratorProvider,
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
                @BindsInstance initAppConfig: InitAppConfig,
                @BindsInstance allAppDatabases: List<DatabaseViewInfo>,
                @BindsInstance compositeWorkerFactory: CompositeWorkerFactory,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                languagesInteractorProvider: LanguagesInteractorProvider,
                schedulersProvider: SchedulersManagerProvider,
                appContextProvider: AppContextProvider,
                uiCoreProvider: UiCoreProvider,
                fragmentManagerConfiguratorProvider: FragmentManagerConfiguratorProvider,
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

        fun buildInitAppConfig(
                app: GameApp,
                appTag: String
        ): InitAppConfig =
                InitAppConfig(
                        appTag = appTag,
                        versionCode = BuildConfig.VERSION_CODE,
                        versionName = BuildConfig.VERSION_NAME,
                        fileLogsFolder = File(app.cacheDir, "fileLogs"),
                        languagesJsonRawResId = com.ruslan.hlushan.game.settings.ui.R.raw.languages,
                        defaultLanguageFullCode = BuildConfig.DEFAULT_LANGUAGE_FULL_CODE
                                .let { pair -> pair.toLangFullCode()!! },
                        availableLanguagesFullCodes = BuildConfig.AVAILABLE_LANGUAGES_FULL_CODES
                                .mapNotNull { pair -> pair.toLangFullCode() }
                )

        fun buildLoggersProvider(
                app: GameApp,
                initAppConfig: InitAppConfig
        ): LoggersProvider =
                LoggerImplExportComponent.Initializer.init(
                        initAppConfig = initAppConfig,
                        errorLogger = ErrorLoggerImpl(),
                        appContext = app
                )

        @SuppressWarnings("LongMethod")
        fun init(
                app: GameApp,
                initAppConfig: InitAppConfig,
                loggersProvider: LoggersProvider
        ): GameAppComponent {

            val networkConfig = NetworkConfig()
            val gameNetworkParams = GameNetworkParams(baseApiUrl = BuildConfig.BASE_API_URL)

            val coreImplProvider = CoreImplExportComponent.Initializer.init(
                    application = app,
                    initAppConfig = initAppConfig
            )

            val appForegroundObserverProvider = AppForegroundObserverImplExportComponent.Initializer.init(
                    schedulersProvider = coreImplProvider
            )

            val languagesInteractorProvider = LanguageImplExportComponent.Initializer.init(
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
                    appForegroundObserverProvider = appForegroundObserverProvider,
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
                            initAppConfig = initAppConfig,
                            allAppDatabases = allAppDatabases,
                            compositeWorkerFactory = compositeWorkerFactory,
                            managersProvider = coreImplProvider,
                            loggersProvider = loggersProvider,
                            languagesInteractorProvider = languagesInteractorProvider,
                            schedulersProvider = coreImplProvider,
                            appContextProvider = coreImplProvider,
                            uiCoreProvider = uiCoreProvider,
                            fragmentManagerConfiguratorProvider = uiCoreProvider,
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

private fun Pair<String, String>.toLangFullCode(): LangFullCode? =
        LangFullCode.createFrom(this.first, this.second)