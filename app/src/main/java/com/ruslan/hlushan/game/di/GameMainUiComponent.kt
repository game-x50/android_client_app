package com.ruslan.hlushan.game.di

import android.app.Activity
import com.ruslan.hlushan.core.di.asType
import com.ruslan.hlushan.core.error.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.manager.api.di.ManagersProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.api.extensions.injectorHolder
import com.ruslan.hlushan.core.ui.fragment.manager.FragmentManagerConfiguratorProvider
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import com.ruslan.hlushan.game.GameAppActivity
import com.ruslan.hlushan.game.GameAppViewModel
import com.ruslan.hlushan.game.screens.main.MainScreenFragment
import com.ruslan.hlushan.third_party.rxjava2.extensions.di.SchedulersManagerProvider
import dagger.Component

@MainUiScope
@Component(
        dependencies = [
            UiCoreProvider::class,
            FragmentManagerConfiguratorProvider::class,
            UiRoutingProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            SchedulersManagerProvider::class
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
                fragmentManagerConfiguratorProvider: FragmentManagerConfiguratorProvider,
                uiRoutingProvider: UiRoutingProvider,
                userErrorMapperProvider: UserErrorMapperProvider,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersManagerProvider
        ): GameMainUiComponent
    }
}

internal fun Activity.getGameMainUiComponent(): GameMainUiComponent {
    val activityInjectorHolder = this.injectorHolder
    return activityInjectorHolder.components.getOrPut(GameMainUiComponent::class) {
        DaggerGameMainUiComponent.factory()
                .create(
                        uiCoreProvider = activityInjectorHolder.iBaseInjector.asType(),
                        fragmentManagerConfiguratorProvider = activityInjectorHolder.iBaseInjector.asType(),
                        uiRoutingProvider = activityInjectorHolder.iBaseInjector.asType(),
                        userErrorMapperProvider = activityInjectorHolder.iBaseInjector.asType(),
                        managersProvider = activityInjectorHolder.iBaseInjector.asType(),
                        loggersProvider = activityInjectorHolder.iBaseInjector.asType(),
                        schedulersProvider = activityInjectorHolder.iBaseInjector.asType()
                )
    }
}

internal fun Activity.clearGameMainUiComponent() =
        this.injectorHolder.components.clear(GameMainUiComponent::class)