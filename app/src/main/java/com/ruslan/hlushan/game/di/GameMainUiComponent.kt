package com.ruslan.hlushan.game.di

import android.app.Activity
import com.ruslan.hlushan.core.api.di.InjectorHolder
import com.ruslan.hlushan.core.api.di.LoggersProvider
import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.api.di.SchedulersProvider
import com.ruslan.hlushan.core.api.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import com.ruslan.hlushan.game.GameAppActivity
import com.ruslan.hlushan.game.GameAppViewModel
import com.ruslan.hlushan.game.screens.main.MainScreenFragment
import dagger.Component

@Component(
        dependencies = [
            UiCoreProvider::class,
            UiRoutingProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            SchedulersProvider::class
        ]
)
internal interface GameMainUiComponent {

    fun inject(activity: GameAppActivity)
    fun inject(fragment: MainScreenFragment)

    fun gameAppViewModelFactory(): GameAppViewModel.Factory

    @Component.Factory
    interface Factory {
        @SuppressWarnings("LongParameterList")
        fun create(
                uiCoreProvider: UiCoreProvider,
                uiRoutingProvider: UiRoutingProvider,
                userErrorMapperProvider: UserErrorMapperProvider,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersProvider
        ): GameMainUiComponent
    }
}

@SuppressWarnings("UnsafeCast")
internal fun Activity.getGameMainUiComponent(): GameMainUiComponent {
    val injectorHolder = (this.application as InjectorHolder)
    val components = injectorHolder.components
    return components.getOrPut(GameMainUiComponent::class) {
        DaggerGameMainUiComponent.factory()
                .create(
                        uiCoreProvider = (injectorHolder.iBaseInjector as UiCoreProvider),
                        uiRoutingProvider = (injectorHolder.iBaseInjector as UiRoutingProvider),
                        userErrorMapperProvider = (injectorHolder.iBaseInjector as UserErrorMapperProvider),
                        managersProvider = (injectorHolder.iBaseInjector as ManagersProvider),
                        loggersProvider = (injectorHolder.iBaseInjector as LoggersProvider),
                        schedulersProvider = (injectorHolder.iBaseInjector as SchedulersProvider)
                )
    }
}