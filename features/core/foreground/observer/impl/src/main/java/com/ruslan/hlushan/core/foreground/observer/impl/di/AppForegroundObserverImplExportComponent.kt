package com.ruslan.hlushan.core.foreground.observer.impl.di

import com.ruslan.hlushan.core.foreground.observer.api.di.AppForegroundObserverProvider
import com.ruslan.hlushan.third_party.rxjava2.extensions.di.SchedulersManagerProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AppForegroundObserverModule::class
        ],
        dependencies = [
            SchedulersManagerProvider::class,
        ]
)
interface AppForegroundObserverImplExportComponent : AppForegroundObserverProvider {

    @Component.Factory
    interface Factory {
        fun create(
                schedulersProvider: SchedulersManagerProvider
        ): AppForegroundObserverImplExportComponent
    }

    object Initializer {

        fun init(
                schedulersProvider: SchedulersManagerProvider
        ): AppForegroundObserverImplExportComponent =
                DaggerAppForegroundObserverImplExportComponent.factory()
                        .create(
                                schedulersProvider = schedulersProvider
                        )
    }
}