package com.ruslan.hlushan.game.settings.ui.di

import com.ruslan.hlushan.core.api.di.InjectorHolder
import com.ruslan.hlushan.core.api.di.LanguagesProvider
import com.ruslan.hlushan.core.api.di.LoggersProvider
import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.api.di.SchedulersProvider
import com.ruslan.hlushan.core.api.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
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
@Component(
        dependencies = [
            UiCoreProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            SchedulersProvider::class,
            LanguagesProvider::class,
            SettingsOutScreenCreatorProvider::class
        ]
)
internal interface GameSettingsUiComponent {

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
                userErrorMapperProvider: UserErrorMapperProvider,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersProvider,
                languagesProvider: LanguagesProvider,
                settingsOutScreenCreatorProvider: SettingsOutScreenCreatorProvider
        ): GameSettingsUiComponent
    }
}

@SuppressWarnings("UnsafeCast", "MaxLineLength")
internal fun com.ruslan.hlushan.core.ui.fragment.BaseFragment.getGameSettingsUiComponent(): GameSettingsUiComponent {
    val injectorHolder = (activity?.application as InjectorHolder)
    val components = injectorHolder.components
    return components.getOrPut(GameSettingsUiComponent::class) {
        DaggerGameSettingsUiComponent.factory()
                .create(
                        uiCoreProvider = (injectorHolder.iBaseInjector as UiCoreProvider),
                        userErrorMapperProvider = (injectorHolder.iBaseInjector as UserErrorMapperProvider),
                        managersProvider = (injectorHolder.iBaseInjector as ManagersProvider),
                        loggersProvider = (injectorHolder.iBaseInjector as LoggersProvider),
                        schedulersProvider = (injectorHolder.iBaseInjector as SchedulersProvider),
                        languagesProvider = (injectorHolder.iBaseInjector as LanguagesProvider),
                        settingsOutScreenCreatorProvider = (injectorHolder.iBaseInjector as SettingsOutScreenCreatorProvider)
                )
    }
}