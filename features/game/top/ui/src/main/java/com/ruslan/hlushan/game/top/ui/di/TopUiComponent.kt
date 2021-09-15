package com.ruslan.hlushan.game.top.ui.di

import com.ruslan.hlushan.core.api.di.asType
import com.ruslan.hlushan.core.error.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.manager.api.di.ManagersProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.fragment.injectorHolder
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import com.ruslan.hlushan.game.api.di.providers.AuthInteractorProvider
import com.ruslan.hlushan.game.api.di.providers.TopInteractorProvider
import com.ruslan.hlushan.game.top.ui.games.TopGamesFragment
import com.ruslan.hlushan.game.top.ui.games.TopGamesViewModel
import com.ruslan.hlushan.third_party.rxjava2.extensions.di.SchedulersManagerProvider
import dagger.Component

@SuppressWarnings("ComplexInterface")
@TopUiScope
@Component(
        dependencies = [
            UiCoreProvider::class,
            UiRoutingProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            SchedulersManagerProvider::class,
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
                uiRoutingProvider: UiRoutingProvider,
                userErrorMapperProvider: UserErrorMapperProvider,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersManagerProvider,
                topInteractorProvider: TopInteractorProvider,
                authInteractorProvider: AuthInteractorProvider
        ): TopUiComponent
    }
}

internal fun BaseFragment.getTopUiComponent(): TopUiComponent {
    val fragmentInjectorHolder = this.injectorHolder
    return fragmentInjectorHolder.components.getOrPut(TopUiComponent::class) {
        DaggerTopUiComponent.factory()
                .create(
                        uiCoreProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        uiRoutingProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        userErrorMapperProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        managersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        loggersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        schedulersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        topInteractorProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        authInteractorProvider = fragmentInjectorHolder.iBaseInjector.asType()
                )
    }
}

internal fun BaseFragment.clearTopUiComponent() =
        this.injectorHolder.components.clear(TopUiComponent::class)