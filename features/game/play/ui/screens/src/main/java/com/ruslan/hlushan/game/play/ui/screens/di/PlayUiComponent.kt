package com.ruslan.hlushan.game.play.ui.screens.di

import com.ruslan.hlushan.core.di.asType
import com.ruslan.hlushan.core.error.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.manager.api.di.ManagersProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.fragment.injectorHolder
import com.ruslan.hlushan.core.ui.fragment.manager.FragmentManagerConfiguratorProvider
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import com.ruslan.hlushan.game.api.di.providers.AuthInteractorProvider
import com.ruslan.hlushan.game.api.di.providers.GameSettingsProvider
import com.ruslan.hlushan.game.api.di.providers.PlayRecordsInteractorProvider
import com.ruslan.hlushan.game.api.di.providers.RecordsUseCasesProvider
import com.ruslan.hlushan.game.play.ui.screens.flow.PlayFlowFragment
import com.ruslan.hlushan.game.play.ui.screens.game.continue_game.ContinueGameFragment
import com.ruslan.hlushan.game.play.ui.screens.game.continue_game.ContinueGameViewModel
import com.ruslan.hlushan.game.play.ui.screens.game.new_game.NewGameFragment
import com.ruslan.hlushan.game.play.ui.screens.game.new_game.NewGameViewModel
import com.ruslan.hlushan.game.play.ui.screens.records.GameRecordsListFragment
import com.ruslan.hlushan.game.play.ui.screens.records.GameRecordsListViewModel
import com.ruslan.hlushan.third_party.rxjava2.extensions.di.SchedulersManagerProvider
import dagger.Component

@PlayUiScope
@Component(
        modules = [
            AssistedGamePlayUiViewModelsModule::class
        ],
        dependencies = [
            UiCoreProvider::class,
            FragmentManagerConfiguratorProvider::class,
            UiRoutingProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            SchedulersManagerProvider::class,
            GameSettingsProvider::class,
            PlayRecordsInteractorProvider::class,
            RecordsUseCasesProvider::class,
            AuthInteractorProvider::class
        ]
)
internal interface PlayUiComponent {

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
                fragmentManagerConfiguratorProvider: FragmentManagerConfiguratorProvider,
                uiRoutingProvider: UiRoutingProvider,
                userErrorMapperProvider: UserErrorMapperProvider,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersManagerProvider,
                gameSettingsProvider: GameSettingsProvider,
                playRecordsInteractorProvider: PlayRecordsInteractorProvider,
                recordsUseCasesProvider: RecordsUseCasesProvider,
                authInteractorProvider: AuthInteractorProvider
        ): PlayUiComponent
    }
}

internal fun BaseFragment.gamePlayUiComponent(): PlayUiComponent {
    val fragmentInjectorHolder = this.injectorHolder
    return fragmentInjectorHolder.components.getOrPut(PlayUiComponent::class) {
        DaggerPlayUiComponent.factory()
                .create(
                        uiCoreProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        fragmentManagerConfiguratorProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        uiRoutingProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        userErrorMapperProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        managersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        loggersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        schedulersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        gameSettingsProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        playRecordsInteractorProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        recordsUseCasesProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        authInteractorProvider = fragmentInjectorHolder.iBaseInjector.asType()
                )
    }
}

internal fun BaseFragment.clearPlayUiComponent() =
        this.injectorHolder.components.clear(PlayUiComponent::class)