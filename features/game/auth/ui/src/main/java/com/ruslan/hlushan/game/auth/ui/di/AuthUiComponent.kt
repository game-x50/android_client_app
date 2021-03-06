package com.ruslan.hlushan.game.auth.ui.di

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
import com.ruslan.hlushan.game.api.di.providers.PlayRecordsInteractorProvider
import com.ruslan.hlushan.game.auth.ui.forgot.password.ForgotPasswordFragment
import com.ruslan.hlushan.game.auth.ui.forgot.password.ForgotPasswordViewModel
import com.ruslan.hlushan.game.auth.ui.login.LoginFragment
import com.ruslan.hlushan.game.auth.ui.login.LoginViewModel
import com.ruslan.hlushan.game.auth.ui.profile.UserProfileFragment
import com.ruslan.hlushan.game.auth.ui.profile.UserProfileViewModel
import com.ruslan.hlushan.game.auth.ui.register.RegisterFragment
import com.ruslan.hlushan.game.auth.ui.register.RegisterViewModel
import com.ruslan.hlushan.third_party.rxjava2.extensions.di.SchedulersManagerProvider
import dagger.Component

@AuthUiScope
@Component(
        dependencies = [
            UiCoreProvider::class,
            FragmentManagerConfiguratorProvider::class,
            UiRoutingProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            SchedulersManagerProvider::class,
            PlayRecordsInteractorProvider::class,
            AuthInteractorProvider::class
        ]
)
internal interface AuthUiComponent {

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
                fragmentManagerConfiguratorProvider: FragmentManagerConfiguratorProvider,
                uiRoutingProvider: UiRoutingProvider,
                userErrorMapperProvider: UserErrorMapperProvider,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersManagerProvider,
                playRecordsInteractorProvider: PlayRecordsInteractorProvider,
                authInteractorProvider: AuthInteractorProvider
        ): AuthUiComponent
    }
}

internal fun BaseFragment.getAuthUiComponent(): AuthUiComponent {
    val fragmentInjectorHolder = this.injectorHolder
    return fragmentInjectorHolder.components.getOrPut(AuthUiComponent::class) {
        DaggerAuthUiComponent.factory()
                .create(
                        uiCoreProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        fragmentManagerConfiguratorProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        uiRoutingProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        userErrorMapperProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        managersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        loggersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        schedulersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        playRecordsInteractorProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        authInteractorProvider = fragmentInjectorHolder.iBaseInjector.asType()
                )
    }
}

internal fun BaseFragment.clearAuthUiComponent() =
        this.injectorHolder.components.clear(AuthUiComponent::class)