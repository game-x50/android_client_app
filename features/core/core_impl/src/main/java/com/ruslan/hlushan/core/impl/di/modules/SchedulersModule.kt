package com.ruslan.hlushan.core.impl.di.modules

import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.impl.managers.SchedulersManagerImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Created by User on 13.09.2017.
 */

@Module
internal interface SchedulersModule {

    @Binds
    @Singleton
    fun provideSchedulersManager(schedulersManagerImpl: SchedulersManagerImpl): SchedulersManager
}