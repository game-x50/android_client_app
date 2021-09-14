package com.ruslan.hlushan.core.ui.impl.tools.di

import com.ruslan.hlushan.core.api.di.DatabaseViewInfoListProvider
import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.api.di.StagingToolsProvider
import com.ruslan.hlushan.core.api.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.api.di.asType
import com.ruslan.hlushan.core.api.dto.InitAppConfig
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.fragment.injectorHolder
import com.ruslan.hlushan.core.ui.impl.tools.StagingSettingsFragment
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import dagger.BindsInstance
import dagger.Component

@UiCoreImplStagingUiScope
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

internal fun BaseFragment.getUiCoreImplStagingComponent(): UiCoreImplStagingComponent {
    val fragmentInjectorHolder = this.injectorHolder
    return fragmentInjectorHolder.components.getOrPut(UiCoreImplStagingComponent::class) {
        DaggerUiCoreImplStagingComponent.factory()
                .create(
                        initAppConfig = fragmentInjectorHolder.initAppConfig,
                        uiCoreProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        uiRoutingProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        userErrorMapperProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        managersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        loggersProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        stagingToolsProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        databasesListProvider = fragmentInjectorHolder.iBaseInjector.asType()
                )
    }
}

internal fun BaseFragment.clearUiCoreImplStagingComponent() =
        this.injectorHolder.components.clear(UiCoreImplStagingComponent::class)