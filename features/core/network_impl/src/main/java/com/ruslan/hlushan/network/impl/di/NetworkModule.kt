package com.ruslan.hlushan.network.impl.di

import com.ruslan.hlushan.network.api.NetworkBuildHelper
import com.ruslan.hlushan.network.impl.NetworkBuildHelperImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal interface NetworkModule {

    @Binds
    @Singleton
    fun provideNetworkBuildHelper(impl: NetworkBuildHelperImpl): NetworkBuildHelper
}