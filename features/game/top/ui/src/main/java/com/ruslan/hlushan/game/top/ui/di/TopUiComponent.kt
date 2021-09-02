package com.ruslan.hlushan.game.top.ui.di

import com.ruslan.hlushan.core.api.di.InjectorHolder
import com.ruslan.hlushan.core.api.di.LoggersProvider
import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.api.di.SchedulersProvider
import com.ruslan.hlushan.core.api.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.api.presentation.view.fragment.BaseFragment
import com.ruslan.hlushan.game.api.di.providers.AuthInteractorProvider
import com.ruslan.hlushan.game.api.di.providers.TopInteractorProvider
import com.ruslan.hlushan.game.top.ui.games.TopGamesFragment
import com.ruslan.hlushan.game.top.ui.games.TopGamesViewModel
import dagger.Component

@SuppressWarnings("ComplexInterface")
@Component(
        dependencies = [
            UiCoreProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            SchedulersProvider::class,
            TopInteractorProvider::class,
            AuthInteractorProvider::class
        ]
)
internal interface TopUiComponent {

    fun inject(fragment: TopGamesFragment)

    fun topGamesViewModelFactory(): TopGamesViewModel.Factory

    @Component.Factory
    interface Factory {
        @SuppressWarnings("LongParameterList")
        fun create(
                uiCoreProvider: UiCoreProvider,
                userErrorMapperProvider: UserErrorMapperProvider,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersProvider,
                topInteractorProvider: TopInteractorProvider,
                authInteractorProvider: AuthInteractorProvider
        ): TopUiComponent
    }
}

@SuppressWarnings("UnsafeCast")
internal fun BaseFragment.getTopUiComponent(): TopUiComponent {
    val injectorHolder = (activity?.application as InjectorHolder)
    val components = injectorHolder.components
    return components.getOrPut(TopUiComponent::class) {
        DaggerTopUiComponent.factory()
                .create(
                        uiCoreProvider = (injectorHolder.iBaseInjector as UiCoreProvider),
                        userErrorMapperProvider = (injectorHolder.iBaseInjector as UserErrorMapperProvider),
                        managersProvider = (injectorHolder.iBaseInjector as ManagersProvider),
                        loggersProvider = (injectorHolder.iBaseInjector as LoggersProvider),
                        schedulersProvider = (injectorHolder.iBaseInjector as SchedulersProvider),
                        topInteractorProvider = (injectorHolder.iBaseInjector as TopInteractorProvider),
                        authInteractorProvider = (injectorHolder.iBaseInjector as AuthInteractorProvider)
                )
    }
}

internal fun BaseFragment.clearTopUiComponent() {
    val injectorHolder = (activity?.application as InjectorHolder)
    injectorHolder.components.clear(TopUiComponent::class)
}