package com.ruslan.hlushan.core.ui.impl.di

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.error.SimpleUserErrorMapper
import com.ruslan.hlushan.core.error.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.impl.tools.di.UiToolsModule
import com.ruslan.hlushan.core.ui.routing.di.UiRoutingProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            NavigationModule::class,
            UiManagersModule::class,
            ThreadCheckerModule::class,
            CompositeUserErrorMapperModule::class,
            UiToolsModule::class
        ],
        dependencies = [
            ManagersProvider::class,
            LoggersProvider::class,
            AppContextProvider::class
        ]
)
interface UiCoreImplExportComponent : UiCoreProvider,
                                      UiRoutingProvider,
                                      UserErrorMapperProvider {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance external: List<SimpleUserErrorMapper>,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                appContextProvider: AppContextProvider
        ): UiCoreImplExportComponent
    }

    object Initializer {

        fun init(
                external: List<SimpleUserErrorMapper>,
                managersProvider: ManagersProvider,
                loggersProvider: LoggersProvider,
                appContextProvider: AppContextProvider
        ): UiCoreImplExportComponent =
                DaggerUiCoreImplExportComponent.factory()
                        .create(
                                external = external,
                                managersProvider = managersProvider,
                                loggersProvider = loggersProvider,
                                appContextProvider = appContextProvider
                        )
    }
}