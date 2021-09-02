package com.ruslan.hlushan.core.ui.impl.di

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.di.CoreProvider
import com.ruslan.hlushan.core.api.di.UserErrorMapperProvider
import com.ruslan.hlushan.core.api.managers.SimpleUserErrorMapper
import com.ruslan.hlushan.core.ui.api.di.UiCoreProvider
import com.ruslan.hlushan.core.ui.impl.tools.di.UiToolsModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * @author Ruslan Hlushan on 9/15/18.
 */
@Component(
        modules = [
            NavigationModule::class,
            UiManagersModule::class,
            ThreadCheckerModule::class,
            CompositeUserErrorMapperModule::class,
            UiToolsModule::class
        ],
        dependencies = [
            CoreProvider::class,
            AppContextProvider::class
        ]
)
@Singleton
interface UiCoreImplExportComponent : UiCoreProvider,
                                      UserErrorMapperProvider {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance external: List<SimpleUserErrorMapper>,
                coreProvider: CoreProvider,
                appContextProvider: AppContextProvider
        ): UiCoreImplExportComponent
    }

    object Initializer {

        fun init(
                external: List<SimpleUserErrorMapper>,
                coreProvider: CoreProvider,
                appContextProvider: AppContextProvider
        ): UiCoreImplExportComponent =
                DaggerUiCoreImplExportComponent.factory()
                        .create(
                                external = external,
                                coreProvider = coreProvider,
                                appContextProvider = appContextProvider
                        )
    }
}