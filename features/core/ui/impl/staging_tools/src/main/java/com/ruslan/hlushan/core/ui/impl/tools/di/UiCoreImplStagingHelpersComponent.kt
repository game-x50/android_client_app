package com.ruslan.hlushan.core.ui.impl.tools.di

import android.app.Activity
import com.ruslan.hlushan.core.di.asType
import com.ruslan.hlushan.core.error.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.manager.api.di.ManagersProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.api.extensions.injectorHolder
import com.ruslan.hlushan.core.ui.fragment.manager.FragmentManagerConfiguratorProvider
import com.ruslan.hlushan.core.ui.impl.tools.file.FileLogsActivity
import com.ruslan.hlushan.core.ui.impl.tools.file.FileLogsViewModel
import com.ruslan.hlushan.third_party.rxjava2.extensions.di.SchedulersManagerProvider
import dagger.Component

@UiCoreImplStagingHelpersScope
@Component(
        dependencies = [
            UiCoreProvider::class,
            FragmentManagerConfiguratorProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            SchedulersManagerProvider::class
        ]
)
internal interface UiCoreImplStagingHelpersComponent {

    fun inject(fileLogsActivity: FileLogsActivity)

    fun fileLogsViewModelFactory(): FileLogsViewModel.Factory

    @Component.Factory
    interface Factory {
        @SuppressWarnings("LongParameterList")
        fun create(
                uiCoreProvider: UiCoreProvider,
                fragmentManagerConfiguratorProvider: FragmentManagerConfiguratorProvider,
                userErrorMapperProvider: UserErrorMapperProvider,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersManagerProvider
        ): UiCoreImplStagingHelpersComponent
    }
}

internal fun Activity.getUiCoreImplStagingHelpersComponent(): UiCoreImplStagingHelpersComponent {
    val activityInjectorHolder = this.injectorHolder
    return activityInjectorHolder.components.getOrPut(UiCoreImplStagingHelpersComponent::class) {
        DaggerUiCoreImplStagingHelpersComponent.factory()
                .create(
                        uiCoreProvider = activityInjectorHolder.iBaseInjector.asType(),
                        fragmentManagerConfiguratorProvider = activityInjectorHolder.iBaseInjector.asType(),
                        userErrorMapperProvider = activityInjectorHolder.iBaseInjector.asType(),
                        managersProvider = activityInjectorHolder.iBaseInjector.asType(),
                        loggersProvider = activityInjectorHolder.iBaseInjector.asType(),
                        schedulersProvider = activityInjectorHolder.iBaseInjector.asType()
                )
    }
}

internal fun Activity.clearUiCoreImplStagingHelpersComponent() =
        this.injectorHolder.components.clear(UiCoreImplStagingHelpersComponent::class)