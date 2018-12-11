package com.ruslan.hlushan.core.ui.impl.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.ui.api.router.FlowCiceronesHolder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal object NavigationModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideAppCicerone(): Cicerone<Router> = Cicerone.create()

    @JvmStatic
    @Provides
    @Singleton
    fun provideFlowCiceronesHolder(): FlowCiceronesHolder = FlowCiceronesHolder()
}