package com.ruslan.hlushan.core.logger.impl.di

import android.content.Context
import com.ruslan.hlushan.core.config.app.InitAppConfig
import com.ruslan.hlushan.core.logger.api.ErrorLogger
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            LoggerModule::class
        ]
)
interface LoggerImplExportComponent : LoggersProvider {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance initAppConfig: InitAppConfig,
                @BindsInstance errorLogger: ErrorLogger,
                /**
                * [com.ruslan.hlushan.android.core.api.di.AppContextProvider] not used
                 * because [LoggersProvider] should be created almost before all other deps.
                * */
                @BindsInstance appContext: Context
        ): LoggerImplExportComponent
    }

    object Initializer {

        fun init(
                initAppConfig: InitAppConfig,
                errorLogger: ErrorLogger,
                appContext: Context
        ): LoggersProvider =
                DaggerLoggerImplExportComponent.factory()
                        .create(
                                initAppConfig = initAppConfig,
                                errorLogger = errorLogger,
                                appContext = appContext
                        )
    }
}