package com.ruslan.hlushan.core.language.impl.di

import com.ruslan.hlushan.core.config.app.InitAppConfig
import com.ruslan.hlushan.core.language.api.di.LanguagesInteractorProvider
import com.ruslan.hlushan.core.manager.api.di.ManagersProvider
import com.ruslan.hlushan.third_party.rxjava2.extensions.di.SchedulersManagerProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            LanguageModule::class
        ],
        dependencies = [
            SchedulersManagerProvider::class,
            ManagersProvider::class
        ]
)
interface LanguageImplExportComponent : LanguagesInteractorProvider {

    @Component.Factory
    interface Factory {
        fun create(
                @BindsInstance initAppConfig: InitAppConfig,
                schedulersProvider: SchedulersManagerProvider,
                managersProvider: ManagersProvider
        ): LanguageImplExportComponent
    }

    object Initializer {

        fun init(
                initAppConfig: InitAppConfig,
                schedulersProvider: SchedulersManagerProvider,
                managersProvider: ManagersProvider
        ): LanguagesInteractorProvider =
                DaggerLanguageImplExportComponent.factory()
                        .create(
                                initAppConfig = initAppConfig,
                                schedulersProvider = schedulersProvider,
                                managersProvider = managersProvider
                        )
    }
}