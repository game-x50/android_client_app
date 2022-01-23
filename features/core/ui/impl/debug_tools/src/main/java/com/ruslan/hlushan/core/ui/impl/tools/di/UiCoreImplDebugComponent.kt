package com.ruslan.hlushan.core.ui.impl.tools.di

import com.ruslan.hlushan.core.api.di.DebugToolsProvider
import com.ruslan.hlushan.core.config.app.di.InitAppConfigProvider
import com.ruslan.hlushan.core.di.asType
import com.ruslan.hlushan.core.error.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.manager.api.di.ManagersProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.fragment.injectorHolder
import com.ruslan.hlushan.core.ui.fragment.manager.FragmentManagerConfiguratorProvider
import com.ruslan.hlushan.core.ui.impl.tools.DebugSettingsFragment
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import com.ruslan.hlushan.third_party.androidx.room.utils.di.DatabaseViewInfoListProvider
import dagger.Component

@UiCoreImplDebugScope
@Component(
        dependencies = [
            InitAppConfigProvider::class,
            UiCoreProvider::class,
            FragmentManagerConfiguratorProvider::class,
            UiRoutingProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            DebugToolsProvider::class,
            DatabaseViewInfoListProvider::class
        ]
)
internal interface UiCoreImplDebugComponent {

    fun inject(fragment: DebugSettingsFragment)

    @Component.Factory
    interface Factory {
        @SuppressWarnings("LongParameterList")
        fun create(
                initAppConfigProvider: InitAppConfigProvider,
                uiCoreProvider: UiCoreProvider,
                fragmentManagerConfiguratorProvider: FragmentManagerConfiguratorProvider,
                uiRoutingProvider: UiRoutingProvider,
                userErrorMapperProvider: UserErrorMapperProvider,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                debugToolsProvider: DebugToolsProvider,
                databasesListProvider: DatabaseViewInfoListProvider
        ): UiCoreImplDebugComponent
    }
}

internal fun BaseFragment.getUiCoreImplDebugComponent(): UiCoreImplDebugComponent {
    val fragmentInjectorHolder = this.injectorHolder
    return fragmentInjectorHolder.components.getOrPut(UiCoreImplDebugComponent::class) {
        DaggerUiCoreImplDebugComponent.factory()
                .create(
                        initAppConfigProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        uiCoreProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        fragmentManagerConfiguratorProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        uiRoutingProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        userErrorMapperProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        managersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        loggersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        debugToolsProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        databasesListProvider = fragmentInjectorHolder.iBaseInjector.asType()
                )
    }
}

internal fun BaseFragment.clearUiCoreImplDebugComponent() =
        this.injectorHolder.components.clear(UiCoreImplDebugComponent::class)