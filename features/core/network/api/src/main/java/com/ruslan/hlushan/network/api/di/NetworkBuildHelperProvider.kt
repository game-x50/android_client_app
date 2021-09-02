package com.ruslan.hlushan.network.api.di

import com.ruslan.hlushan.network.api.NetworkBuildHelper

interface NetworkBuildHelperProvider {

    fun provideNetworkBuildHelper(): NetworkBuildHelper
}