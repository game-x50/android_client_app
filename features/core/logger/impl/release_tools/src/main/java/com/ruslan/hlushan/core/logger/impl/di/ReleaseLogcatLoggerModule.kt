package com.ruslan.hlushan.core.logger.impl.di

import com.ruslan.hlushan.core.logger.api.EmptyLogcatLoggerImpl
import com.ruslan.hlushan.core.logger.api.LogcatLogger
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ReleaseLogcatLoggerModule {

    @Provides
    @Singleton
    fun provideLogcatLogger(): LogcatLogger = EmptyLogcatLoggerImpl
}