package com.ruslan.hlushan.core.logger.impl.di

import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.logger.api.FileLogger
import com.ruslan.hlushan.core.logger.impl.AppLoggerImpl
import com.ruslan.hlushan.core.logger.impl.FileLoggerImpl
import com.ruslan.hlushan.core.logger.impl.LogcatLogger
import com.ruslan.hlushan.core.logger.impl.LogcatLoggerImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal interface LoggerModule {

    @Binds
    @Singleton
    fun provideAppLogger(impl: AppLoggerImpl): AppLogger

    @Binds
    @Singleton
    fun provideLogcatLogger(impl: LogcatLoggerImpl): LogcatLogger

    @Binds
    @Singleton
    fun provideFileLogger(impl: FileLoggerImpl): FileLogger
}