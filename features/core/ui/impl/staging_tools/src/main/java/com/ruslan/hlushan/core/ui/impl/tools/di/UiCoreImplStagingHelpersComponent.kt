package com.ruslan.hlushan.core.ui.impl.tools.di

import android.app.Activity
import com.ruslan.hlushan.core.api.di.InjectorHolder
import com.ruslan.hlushan.core.api.di.LoggersProvider
import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.api.di.SchedulersProvider
import com.ruslan.hlushan.core.api.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.impl.tools.file.FileLogsActivity
import com.ruslan.hlushan.core.ui.impl.tools.file.FileLogsViewModel
import dagger.Component

/**
 * @author Ruslan Hlushan on 10/22/18.
 */

@Component(
        dependencies = [
            UiCoreProvider::class,
            UserErrorMapperProvider::class,
            ManagersProvider::class,
            LoggersProvider::class,
            SchedulersProvider::class
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
                userErrorMapperProvider: UserErrorMapperProvider,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                schedulersProvider: SchedulersProvider
        ): UiCoreImplStagingHelpersComponent
    }
}

@SuppressWarnings("UnsafeCast")
internal fun Activity.getUiCoreImplStagingHelpersComponent(): UiCoreImplStagingHelpersComponent {
    val injectorHolder = (this.application as InjectorHolder)
    val components = injectorHolder.components
    return components.getOrPut(UiCoreImplStagingHelpersComponent::class) {
        DaggerUiCoreImplStagingHelpersComponent.factory()
                .create(
                        uiCoreProvider = (injectorHolder.iBaseInjector as UiCoreProvider),
                        userErrorMapperProvider = (injectorHolder.iBaseInjector as UserErrorMapperProvider),
                        managersProvider = (injectorHolder.iBaseInjector as ManagersProvider),
                        loggersProvider = (injectorHolder.iBaseInjector as LoggersProvider),
                        schedulersProvider = (injectorHolder.iBaseInjector as SchedulersProvider)
                )
    }
}

internal fun Activity.clearUiCoreImplStagingHelpersComponent() {
    val injectorHolder = (application as InjectorHolder)
    injectorHolder.components.clear(UiCoreImplStagingHelpersComponent::class)
}