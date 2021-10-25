package com.ruslan.hlushan.core.foreground.observer.impl.di

import com.ruslan.hlushan.core.foreground.observer.api.AppForegroundObserver
import com.ruslan.hlushan.core.foreground.observer.impl.AppForegroundObserverAndroidImpl
import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal object AppForegroundObserverModule {

    @JvmStatic
    @Provides
    @Singleton
    fun appForegroundObserver(
            schedulersManager: SchedulersManager
    ): AppForegroundObserver = AppForegroundObserverAndroidImpl.activateAndProvide(schedulersManager)
}