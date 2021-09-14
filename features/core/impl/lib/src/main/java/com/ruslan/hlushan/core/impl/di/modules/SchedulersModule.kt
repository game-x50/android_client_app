package com.ruslan.hlushan.core.impl.di.modules

import com.ruslan.hlushan.core.impl.managers.SchedulersManagerImpl
import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal interface SchedulersModule {

    @Binds
    @Singleton
    fun provideSchedulersManager(schedulersManagerImpl: SchedulersManagerImpl): SchedulersManager
}