package com.ruslan.hlushan.core.ui.impl.tools.di

import com.ruslan.hlushan.core.api.di.DatabaseViewInfoListProvider
import com.ruslan.hlushan.core.api.di.DebugToolsProvider
import com.ruslan.hlushan.core.api.di.asType
import com.ruslan.hlushan.core.api.dto.InitAppConfig
import com.ruslan.hlushan.core.error.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.manager.api.di.ManagersProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.fragment.injectorHolder
import com.ruslan.hlushan.core.ui.impl.tools.DebugSettingsFragment
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import dagger.BindsInstance
import dagger.Component

@UiCoreImplDebugScope
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

internal fun BaseFragment.getUiCoreImplDebugComponent(): UiCoreImplDebugComponent {
    val fragmentInjectorHolder = this.injectorHolder
    return fragmentInjectorHolder.components.getOrPut(UiCoreImplDebugComponent::class) {
        DaggerUiCoreImplDebugComponent.factory()
                .create(
                        initAppConfig = fragmentInjectorHolder.initAppConfig,
                        uiCoreProvider = fragmentInjectorHolder.iBaseInjector.asType(),
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