package com.ruslan.hlushan.core.ui.impl.di

import com.ruslan.hlushan.core.thread.ThreadChecker
import com.ruslan.hlushan.core.ui.api.utils.UiMainThreadChecker
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal object ThreadCheckerModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideThreadChecker(): ThreadChecker = UiMainThreadChecker
}