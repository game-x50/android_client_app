package com.ruslan.hlushan.core.ui.impl.tools.di

import com.ruslan.hlushan.core.api.di.DatabaseViewInfoListProvider
import com.ruslan.hlushan.core.api.di.InjectorHolder
import com.ruslan.hlushan.core.api.di.LoggersProvider
import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.api.di.StagingToolsProvider
import com.ruslan.hlushan.core.api.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.api.utils.InitAppConfig
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.impl.tools.StagingSettingsFragment
import dagger.BindsInstance
import dagger.Component

@Component(
        dependencies = [
            UiCoreProvider::class,
            UiRoutingProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            StagingToolsProvider::class,
            DatabaseViewInfoListProvider::class
        ]
)
internal interface UiCoreImplStagingComponent {

    fun inject(fragment: StagingSettingsFragment)

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
                stagingToolsProvider: StagingToolsProvider,
                databasesListProvider: DatabaseViewInfoListProvider
        ): UiCoreImplStagingComponent
    }
}

@SuppressWarnings("UnsafeCast")
internal fun com.ruslan.hlushan.core.ui.fragment.BaseFragment.getUiCoreImplStagingComponent(): UiCoreImplStagingComponent {
    val injectorHolder = (this.activity?.application as InjectorHolder)
    val components = injectorHolder.components
    return components.getOrPut(UiCoreImplStagingComponent::class) {
        DaggerUiCoreImplStagingComponent.factory()
                .create(
                        initAppConfig = (injectorHolder.initAppConfig),
                        uiCoreProvider = (injectorHolder.iBaseInjector as UiCoreProvider),
                        userErrorMapperProvider = (injectorHolder.iBaseInjector as UserErrorMapperProvider),
                        managersProvider = (injectorHolder.iBaseInjector as ManagersProvider),
                        loggersProvider = (injectorHolder.iBaseInjector as LoggersProvider),
                        stagingToolsProvider = (injectorHolder.iBaseInjector as StagingToolsProvider),
                        databasesListProvider = (injectorHolder.iBaseInjector as DatabaseViewInfoListProvider)
                )
    }
}