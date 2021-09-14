package com.ruslan.hlushan.game.settings.ui.di

import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.api.di.SchedulersProvider
import com.ruslan.hlushan.core.api.di.asType
import com.ruslan.hlushan.core.error.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.language.api.di.LanguagesProvider
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.fragment.injectorHolder
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import com.ruslan.hlushan.game.settings.ui.about.AboutAppFragment
import com.ruslan.hlushan.game.settings.ui.flow.SettingsFlowFragment
import com.ruslan.hlushan.game.settings.ui.instruction.GameInstructionFragment
import com.ruslan.hlushan.game.settings.ui.languages.LanguagesFragment
import com.ruslan.hlushan.game.settings.ui.languages.LanguagesViewModel
import com.ruslan.hlushan.game.settings.ui.menu.SettingsMenuFragment
import com.ruslan.hlushan.game.settings.ui.menu.SettingsMenuViewModel
import com.ruslan.hlushan.game.settings.ui.theme.ThemesFragment
import dagger.Component

@SuppressWarnings("ComplexInterface", "MethodOverloading")
@SettingsUiScope
@Component(
        dependencies = [
            UiCoreProvider::class,
            UiRoutingProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            SchedulersProvider::class,
            LanguagesProvider::class,
            SettingsOutScreenCreatorProvider::class
        ]
)
internal interface SettingsUiComponent {

    fun inject(fragment: SettingsFlowFragment)
    fun inject(fragment: SettingsMenuFragment)
    fun inject(fragment: LanguagesFragment)
    fun inject(fragment: ThemesFragment)
    fun inject(fragment: GameInstructionFragment)
    fun inject(fragment: AboutAppFragment)

    fun settingMenuViewModelFactory(): SettingsMenuViewModel.Factory
    fun languagesViewModelFactory(): LanguagesViewModel.Factory

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
                languagesProvider: LanguagesProvider,
                settingsOutScreenCreatorProvider: SettingsOutScreenCreatorProvider
        ): SettingsUiComponent
    }
}

internal fun BaseFragment.getSettingsUiComponent(): SettingsUiComponent {
    val fragmentInjectorHolder = this.injectorHolder
    return fragmentInjectorHolder.components.getOrPut(SettingsUiComponent::class) {
        DaggerSettingsUiComponent.factory()
                .create(
                        uiCoreProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        uiRoutingProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        userErrorMapperProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        managersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        loggersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        schedulersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        languagesProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        settingsOutScreenCreatorProvider = fragmentInjectorHolder.iBaseInjector.asType()
                )
    }
}

internal fun BaseFragment.clearSettingsUiComponent() =
        this.injectorHolder.components.clear(SettingsUiComponent::class)