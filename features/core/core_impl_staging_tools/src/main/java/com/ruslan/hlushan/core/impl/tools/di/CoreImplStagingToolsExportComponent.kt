package com.ruslan.hlushan.core.impl.tools.di

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.di.LoggersProvider
import com.ruslan.hlushan.core.api.di.StagingToolsProvider
import dagger.Component
import javax.inject.Singleton

@Component(
        modules = [CoreImplStagingToolsModule::class],
        dependencies = [
            AppContextProvider::class,
            LoggersProvider::class
        ]
)
@Singleton
interface CoreImplStagingToolsExportComponent : StagingToolsProvider {

    @Component.Factory
    interface Factory {
        fun create(
                appContextProvider: AppContextProvider,
                loggersProvider: LoggersProvider
        ): CoreImplStagingToolsExportComponent
    }

    object Initializer {

        fun init(
                appContextProvider: AppContextProvider,
                loggersProvider: LoggersProvider
        ): StagingToolsProvider =
                DaggerCoreImplStagingToolsExportComponent.factory()
                        .create(
                                appContextProvider = appContextProvider,
                                loggersProvider = loggersProvider
                        )
    }
}