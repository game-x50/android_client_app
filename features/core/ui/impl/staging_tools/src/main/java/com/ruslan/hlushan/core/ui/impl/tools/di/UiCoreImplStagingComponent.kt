package com.ruslan.hlushan.core.ui.impl.tools.di

import com.ruslan.hlushan.core.api.di.StagingToolsProvider
import com.ruslan.hlushan.core.config.app.di.InitAppConfigProvider
import com.ruslan.hlushan.core.di.asType
import com.ruslan.hlushan.core.error.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.manager.api.di.ManagersProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.fragment.injectorHolder
import com.ruslan.hlushan.core.ui.fragment.manager.FragmentManagerConfiguratorProvider
import com.ruslan.hlushan.core.ui.impl.tools.StagingSettingsFragment
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import com.ruslan.hlushan.third_party.androidx.room.utils.di.DatabaseViewInfoListProvider
import dagger.Component

@UiCoreImplStagingUiScope
@Component(
        dependencies = [
            InitAppConfigProvider::class,
            UiCoreProvider::class,
            FragmentManagerConfiguratorProvider::class,
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
                initAppConfigProvider: InitAppConfigProvider,
                uiCoreProvider: UiCoreProvider,
                fragmentManagerConfiguratorProvider: FragmentManagerConfiguratorProvider,
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
                        initAppConfigProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        uiCoreProvider = fragmentInjectorHolder.iBaseInjector.asType(),
                        fragmentManagerConfiguratorProvider = fragmentInjectorHolder.iBaseInjector.asType(),
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