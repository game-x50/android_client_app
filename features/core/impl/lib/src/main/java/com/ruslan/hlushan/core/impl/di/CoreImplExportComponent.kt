package com.ruslan.hlushan.core.impl.di

import android.app.Application
import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.api.di.SchedulersProvider
import com.ruslan.hlushan.core.api.dto.InitAppConfig
import com.ruslan.hlushan.core.impl.di.modules.ApplicationModule
import com.ruslan.hlushan.core.impl.di.modules.ManagerModule
import com.ruslan.hlushan.core.impl.di.modules.SchedulersModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            ApplicationModule::class,
            ManagerModule::class,
            SchedulersModule::class
        ]
)
interface CoreImplExportComponent : ManagersProvider,
                                    SchedulersProvider,
                                    AppContextProvider {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance application: Application,
                @BindsInstance initAppConfig: InitAppConfig
        ): CoreImplExportComponent
    }

    object Initializer {

        fun init(
                application: Application,
                initAppConfig: InitAppConfig
        ): CoreImplExportComponent =
                DaggerCoreImplExportComponent.factory()
                        .create(
                                application = application,
                                initAppConfig = initAppConfig
                        )
    }
}