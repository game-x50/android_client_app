package com.ruslan.hlushan.core.impl.di

import android.app.Application
import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.di.CoreProvider
import com.ruslan.hlushan.core.api.log.ErrorLogger
import com.ruslan.hlushan.core.api.utils.InitAppConfig
import com.ruslan.hlushan.core.impl.di.modules.ApplicationModule
import com.ruslan.hlushan.core.impl.di.modules.LanguagesModule
import com.ruslan.hlushan.core.impl.di.modules.LogsModule
import com.ruslan.hlushan.core.impl.di.modules.ManagerModule
import com.ruslan.hlushan.core.impl.di.modules.SchedulersModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            ApplicationModule::class,
            LanguagesModule::class,
            LogsModule::class,
            ManagerModule::class,
            SchedulersModule::class
        ]
)
interface CoreImplExportComponent : CoreProvider,
                                    AppContextProvider {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance application: Application,
                @BindsInstance initAppConfig: InitAppConfig,
                @BindsInstance errorLogger: ErrorLogger
        ): CoreImplExportComponent
    }

    object Initializer {

        fun init(
                application: Application,
                initAppConfig: InitAppConfig,
                errorLogger: ErrorLogger
        ): CoreImplExportComponent =
                DaggerCoreImplExportComponent.factory()
                        .create(
                                application = application,
                                initAppConfig = initAppConfig,
                                errorLogger = errorLogger
                        )
    }
}