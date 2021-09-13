package com.ruslan.hlushan.core.impl.tools.di

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.di.StagingToolsProvider
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            CoreImplStagingToolsModule::class
        ],
        dependencies = [
            AppContextProvider::class,
            LoggersProvider::class
        ]
)
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