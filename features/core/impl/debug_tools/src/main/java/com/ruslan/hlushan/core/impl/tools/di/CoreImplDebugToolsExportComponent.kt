package com.ruslan.hlushan.core.impl.tools.di

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.di.DebugToolsProvider
import com.ruslan.hlushan.core.api.di.LoggersProvider
import com.ruslan.hlushan.core.api.di.StagingToolsProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        dependencies = [
            StagingToolsProvider::class
        ]
)
interface CoreImplDebugToolsExportComponent : DebugToolsProvider {

    @Component.Factory
    interface Factory {
        fun create(
                stagingToolsProvider: StagingToolsProvider
        ): CoreImplDebugToolsExportComponent
    }

    object Initializer {

        fun init(
                appContextProvider: AppContextProvider,
                loggersProvider: LoggersProvider
        ): DebugToolsProvider =
                DaggerCoreImplDebugToolsExportComponent.factory()
                        .create(
                                stagingToolsProvider = CoreImplStagingToolsExportComponent.Initializer.init(
                                        appContextProvider = appContextProvider,
                                        loggersProvider = loggersProvider
                                )
                        )
    }
}