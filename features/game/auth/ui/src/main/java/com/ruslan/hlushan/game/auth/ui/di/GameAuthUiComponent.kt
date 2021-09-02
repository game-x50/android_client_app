package com.ruslan.hlushan.game.auth.ui.di

import com.ruslan.hlushan.core.api.di.InjectorHolder
import com.ruslan.hlushan.core.api.di.LoggersProvider
import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.api.di.SchedulersProvider
import com.ruslan.hlushan.core.api.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.api.presentation.view.fragment.BaseFragment
import com.ruslan.hlushan.game.api.di.providers.AuthInteractorProvider
import com.ruslan.hlushan.game.api.di.providers.PlayRecordsInteractorProvider
import com.ruslan.hlushan.game.auth.ui.forgot.password.ForgotPasswordFragment
import com.ruslan.hlushan.game.auth.ui.forgot.password.ForgotPasswordViewModel
import com.ruslan.hlushan.game.auth.ui.login.LoginFragment
import com.ruslan.hlushan.game.auth.ui.login.LoginViewModel
import com.ruslan.hlushan.game.auth.ui.profile.UserProfileFragment
import com.ruslan.hlushan.game.auth.ui.profile.UserProfileViewModel
import com.ruslan.hlushan.game.auth.ui.register.RegisterFragment
import com.ruslan.hlushan.game.auth.ui.register.RegisterViewModel
import dagger.Component

@Component(
        dependencies = [
            UiCoreProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            SchedulersProvider::class,
            PlayRecordsInteractorProvider::class,
            AuthInteractorProvider::class
        ]
)
internal interface GameAuthUiComponent {

    fun inject(fragment: UserProfileFragment)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: RegisterFragment)
    fun inject(fragment: ForgotPasswordFragment)

    fun userProfileViewModelFactory(): UserProfileViewModel.Factory
    fun loginViewModelFactory(): LoginViewModel.Factory
    fun registerViewModelFactory(): RegisterViewModel.Factory
    fun forgotPasswordViewModelFactory(): ForgotPasswordViewModel.Factory

    @Component.Factory
    interface Factory {
        @SuppressWarnings("LongParameterList")
        fun create(
                uiCoreProvider: UiCoreProvider,
                userErrorMapperProvider: UserErrorMapperProvider,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersProvider,
                playRecordsInteractorProvider: PlayRecordsInteractorProvider,
                authInteractorProvider: AuthInteractorProvider
        ): GameAuthUiComponent
    }
}

@SuppressWarnings("UnsafeCast")
internal fun BaseFragment.getGameAuthUiComponent(): GameAuthUiComponent {
    val injectorHolder = (activity?.application as InjectorHolder)
    val components = injectorHolder.components
    return components.getOrPut(GameAuthUiComponent::class) {
        DaggerGameAuthUiComponent.factory()
                .create(
                        uiCoreProvider = (injectorHolder.iBaseInjector as UiCoreProvider),
                        userErrorMapperProvider = (injectorHolder.iBaseInjector as UserErrorMapperProvider),
                        managersProvider = (injectorHolder.iBaseInjector as ManagersProvider),
                        loggersProvider = (injectorHolder.iBaseInjector as LoggersProvider),
                        schedulersProvider = (injectorHolder.iBaseInjector as SchedulersProvider),
                        playRecordsInteractorProvider = (injectorHolder.iBaseInjector as PlayRecordsInteractorProvider),
                        authInteractorProvider = (injectorHolder.iBaseInjector as AuthInteractorProvider)
                )
    }
}