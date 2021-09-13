package com.ruslan.hlushan.game.di

import android.app.Activity
import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.api.di.SchedulersProvider
import com.ruslan.hlushan.core.api.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.api.di.asType
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.api.extensions.injectorHolder
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import com.ruslan.hlushan.game.GameAppActivity
import com.ruslan.hlushan.game.GameAppViewModel
import com.ruslan.hlushan.game.screens.main.MainScreenFragment
import dagger.Component

@MainUiScope
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

internal fun Activity.getGameMainUiComponent(): GameMainUiComponent {
    val activityInjectorHolder = this.injectorHolder
    return activityInjectorHolder.components.getOrPut(GameMainUiComponent::class) {
        DaggerGameMainUiComponent.factory()
                .create(
                        uiCoreProvider = activityInjectorHolder.iBaseInjector.asType(),
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