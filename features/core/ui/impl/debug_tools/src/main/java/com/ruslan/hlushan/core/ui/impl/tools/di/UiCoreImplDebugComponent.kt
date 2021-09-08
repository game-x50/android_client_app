package com.ruslan.hlushan.core.ui.impl.tools.di

import com.ruslan.hlushan.core.api.di.DatabaseViewInfoListProvider
import com.ruslan.hlushan.core.api.di.DebugToolsProvider
import com.ruslan.hlushan.core.api.di.InjectorHolder
import com.ruslan.hlushan.core.api.di.LoggersProvider
import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.api.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.api.utils.InitAppConfig
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.impl.tools.DebugSettingsFragment
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import dagger.BindsInstance
import dagger.Component

@Component(
        dependencies = [
            UiCoreProvider::class,
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
                @BindsInstance initAppConfig: InitAppConfig,
                uiCoreProvider: UiCoreProvider,
                uiRoutingProvider: UiRoutingProvider,
                userErrorMapperProvider: UserErrorMapperProvider,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                debugToolsProvider: DebugToolsProvider,
                databasesListProvider: DatabaseViewInfoListProvider
        ): UiCoreImplDebugComponent
    }
}

@SuppressWarnings("UnsafeCast")
internal fun BaseFragment.getUiCoreImplDebugComponent(): UiCoreImplDebugComponent {
    val injectorHolder = (this.activity?.application as InjectorHolder)
    val components = injectorHolder.components
    return components.getOrPut(UiCoreImplDebugComponent::class) {
        DaggerUiCoreImplDebugComponent.factory()
                .create(
                        initAppConfig = (injectorHolder.initAppConfig),
                        uiCoreProvider = (injectorHolder.iBaseInjector as UiCoreProvider),
                        uiRoutingProvider = (injectorHolder.iBaseInjector as UiRoutingProvider),
                        userErrorMapperProvider = (injectorHolder.iBaseInjector as UserErrorMapperProvider),
                        managersProvider = (injectorHolder.iBaseInjector as ManagersProvider),
                        loggersProvider = (injectorHolder.iBaseInjector as LoggersProvider),
                        debugToolsProvider = (injectorHolder.iBaseInjector as DebugToolsProvider),
                        databasesListProvider = (injectorHolder.iBaseInjector as DatabaseViewInfoListProvider)
                )
    }
}