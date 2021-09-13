package com.ruslan.hlushan.core.logger.impl.di

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.utils.InitAppConfig
import com.ruslan.hlushan.core.logger.api.ErrorLogger
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            LoggerModule::class
        ],
        dependencies = [
            AppContextProvider::class
        ]
)
interface LoggerImplExportComponent : LoggersProvider {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance initAppConfig: InitAppConfig,
                @BindsInstance errorLogger: ErrorLogger,
                appContextProvider: AppContextProvider
        ): LoggerImplExportComponent
    }

    object Initializer {

        fun init(
                initAppConfig: InitAppConfig,
                errorLogger: ErrorLogger,
                appContextProvider: AppContextProvider
        ): LoggersProvider =
                DaggerLoggerImplExportComponent.factory()
                        .create(
                                initAppConfig = initAppConfig,
                                errorLogger = errorLogger,
                                appContextProvider = appContextProvider
                        )
    }
}