package com.ruslan.hlushan.core.logger.impl.di

import com.ruslan.hlushan.core.logger.api.LogcatLogger
import com.ruslan.hlushan.core.logger.impl.LogcatLoggerImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface DebugLogcatLoggerModule {

    @Binds
    @Singleton
    fun provideLogcatLogger(impl: LogcatLoggerImpl): LogcatLogger
}