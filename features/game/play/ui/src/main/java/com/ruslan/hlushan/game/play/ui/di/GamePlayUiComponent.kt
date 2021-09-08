package com.ruslan.hlushan.game.play.ui.di

import com.ruslan.hlushan.core.api.di.InjectorHolder
import com.ruslan.hlushan.core.api.di.LoggersProvider
import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.api.di.SchedulersProvider
import com.ruslan.hlushan.core.api.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import com.ruslan.hlushan.game.api.di.providers.AuthInteractorProvider
import com.ruslan.hlushan.game.api.di.providers.GameSettingsProvider
import com.ruslan.hlushan.game.api.di.providers.PlayRecordsInteractorProvider
import com.ruslan.hlushan.game.api.di.providers.RecordsUseCasesProvider
import com.ruslan.hlushan.game.play.ui.GameScopeMarkerRepository
import com.ruslan.hlushan.game.play.ui.flow.PlayFlowFragment
import com.ruslan.hlushan.game.play.ui.game.continue_game.ContinueGameFragment
import com.ruslan.hlushan.game.play.ui.game.continue_game.ContinueGameViewModel
import com.ruslan.hlushan.game.play.ui.game.new_game.NewGameFragment
import com.ruslan.hlushan.game.play.ui.game.new_game.NewGameViewModel
import com.ruslan.hlushan.game.play.ui.records.GameRecordsListFragment
import com.ruslan.hlushan.game.play.ui.records.GameRecordsListViewModel
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(
        modules = [AssistedGamePlayUiViewModelsModule::class],
        dependencies = [
            UiCoreProvider::class,
            UiRoutingProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            SchedulersProvider::class,
            GameSettingsProvider::class,
            PlayRecordsInteractorProvider::class,
            RecordsUseCasesProvider::class,
            AuthInteractorProvider::class
        ]
)
internal interface GamePlayUiComponent {

    fun inject(fragment: PlayFlowFragment)
    fun inject(fragment: GameRecordsListFragment)
    fun inject(fragment: NewGameFragment)
    fun inject(fragment: ContinueGameFragment)

    fun gameRecordsListViewModelFactory(): GameRecordsListViewModel.Factory
    fun newGameViewModelFactory(): NewGameViewModel.Factory
    fun continueGameViewModelFactory(): ContinueGameViewModel.Factory

    @Component.Factory
    interface Factory {
        @SuppressWarnings("LongParameterList")
        fun create(
                uiCoreProvider: UiCoreProvider,
                uiRoutingProvider: UiRoutingProvider,
                userErrorMapperProvider: UserErrorMapperProvider,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersProvider,
                gameSettingsProvider: GameSettingsProvider,
                playRecordsInteractorProvider: PlayRecordsInteractorProvider,
                recordsUseCasesProvider: RecordsUseCasesProvider,
                authInteractorProvider: AuthInteractorProvider
        ): GamePlayUiComponent
    }
}

@Module
internal object AssistedGamePlayUiViewModelsModule {

    @JvmStatic
    @Singleton//todo: create custom named?: https://github.com/game-x50/android_client_app/issues/40
    @Provides
    fun provideGameScopeMarkerRepository(): GameScopeMarkerRepository = GameScopeMarkerRepository()
}

@SuppressWarnings("UnsafeCast")
internal fun BaseFragment.getGamePlayUiComponent(): GamePlayUiComponent {
    val injectorHolder = (activity?.application as InjectorHolder)
    val components = injectorHolder.components
    return components.getOrPut(GamePlayUiComponent::class) {
        DaggerGamePlayUiComponent.factory()
                .create(
                        uiCoreProvider = (injectorHolder.iBaseInjector as UiCoreProvider),
                        uiRoutingProvider = (injectorHolder.iBaseInjector as UiRoutingProvider),
                        userErrorMapperProvider = (injectorHolder.iBaseInjector as UserErrorMapperProvider),
                        managersProvider = (injectorHolder.iBaseInjector as ManagersProvider),
                        loggersProvider = (injectorHolder.iBaseInjector as LoggersProvider),
                        schedulersProvider = (injectorHolder.iBaseInjector as SchedulersProvider),
                        gameSettingsProvider = (injectorHolder.iBaseInjector as GameSettingsProvider),
                        playRecordsInteractorProvider = (injectorHolder.iBaseInjector as PlayRecordsInteractorProvider),
                        recordsUseCasesProvider = (injectorHolder.iBaseInjector as RecordsUseCasesProvider),
                        authInteractorProvider = (injectorHolder.iBaseInjector as AuthInteractorProvider)
                )
    }
}