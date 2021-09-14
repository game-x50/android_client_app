package com.ruslan.hlushan.core.language.impl.di

import com.ruslan.hlushan.core.api.di.ManagersProvider
import com.ruslan.hlushan.core.api.di.SchedulersProvider
import com.ruslan.hlushan.core.api.dto.InitAppConfig
import com.ruslan.hlushan.core.language.api.di.LanguagesProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            LanguageModule::class
        ],
        dependencies = [
            SchedulersProvider::class,
            ManagersProvider::class
        ]
)
interface LanguageImplExportComponent : LanguagesProvider {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance initAppConfig: InitAppConfig,
                schedulersProvider: SchedulersProvider,
                managersProvider: ManagersProvider
        ): LanguageImplExportComponent
    }

    object Initializer {

        fun init(
                initAppConfig: InitAppConfig,
                schedulersProvider: SchedulersProvider,
                managersProvider: ManagersProvider
        ): LanguagesProvider =
                DaggerLanguageImplExportComponent.factory()
                        .create(
                                initAppConfig = initAppConfig,
                                schedulersProvider = schedulersProvider,
                                managersProvider = managersProvider
                        )
    }
}