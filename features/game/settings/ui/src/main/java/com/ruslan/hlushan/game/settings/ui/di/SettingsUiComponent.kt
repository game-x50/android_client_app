package com.ruslan.hlushan.game.settings.ui.di

import com.ruslan.hlushan.core.di.asType
import com.ruslan.hlushan.core.error.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.language.api.di.LanguagesInteractorProvider
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.manager.api.di.ManagersProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.fragment.injectorHolder
import com.ruslan.hlushan.core.ui.fragment.manager.FragmentManagerConfiguratorProvider
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import com.ruslan.hlushan.game.settings.ui.about.AboutAppFragment
import com.ruslan.hlushan.game.settings.ui.flow.SettingsFlowFragment
import com.ruslan.hlushan.game.settings.ui.instruction.GameInstructionFragment
import com.ruslan.hlushan.game.settings.ui.languages.LanguagesFragment
import com.ruslan.hlushan.game.settings.ui.languages.LanguagesViewModel
import com.ruslan.hlushan.game.settings.ui.menu.SettingsMenuFragment
import com.ruslan.hlushan.game.settings.ui.menu.SettingsMenuViewModel
import com.ruslan.hlushan.game.settings.ui.theme.ThemesFragment
import com.ruslan.hlushan.third_party.rxjava2.extensions.di.SchedulersManagerProvider
import dagger.Component

@SuppressWarnings("ComplexInterface", "MethodOverloading")
@SettingsUiScope
@Component(
        dependencies = [
            UiCoreProvider::class,
            FragmentManagerConfiguratorProvider::class,
            UiRoutingProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            SchedulersManagerProvider::class,
            LanguagesInteractorProvider::class,
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
                fragmentManagerConfiguratorProvider: FragmentManagerConfiguratorProvider,
                uiRoutingProvider: UiRoutingProvider,
                userErrorMapperProvider: UserErrorMapperProvider,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersManagerProvider,
                languagesInteractorProvider: LanguagesInteractorProvider,
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
                        fragmentManagerConfiguratorProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        uiRoutingProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        userErrorMapperProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        managersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        loggersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        schedulersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        languagesInteractorProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        settingsOutScreenCreatorProvider = fragmentInjectorHolder.iBaseInjector.asType()
                )
    }
}

internal fun BaseFragment.clearSettingsUiComponent() =
        this.injectorHolder.components.clear(SettingsUiComponent::class)