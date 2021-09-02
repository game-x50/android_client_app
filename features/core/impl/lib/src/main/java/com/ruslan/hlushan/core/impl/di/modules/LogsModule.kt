package com.ruslan.hlushan.core.impl.di.modules

import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.log.FileLogger
import com.ruslan.hlushan.core.api.log.LogcatLogger
import com.ruslan.hlushan.core.impl.log.AppLoggerImpl
import com.ruslan.hlushan.core.impl.log.FileLoggerImpl
import com.ruslan.hlushan.core.impl.log.LogcatLoggerImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal interface LogsModule {

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