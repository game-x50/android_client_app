package com.ruslan.hlushan.network.impl.di

import com.ruslan.hlushan.network.api.di.NetworkBuildHelperProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            NetworkModule::class
        ]
)
interface NetworkImplExportComponent : NetworkBuildHelperProvider {

    @Component.Factory
    interface Factory {
        fun create(): NetworkImplExportComponent
    }

    object Initializer {

        fun init(): NetworkBuildHelperProvider =
                DaggerNetworkImplExportComponent.factory()
                        .create()
    }
}