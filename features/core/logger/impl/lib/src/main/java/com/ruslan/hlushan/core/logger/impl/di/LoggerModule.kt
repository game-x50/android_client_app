package com.ruslan.hlushan.core.logger.impl.di

import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.logger.impl.AppLoggerImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(
        includes = [
            LogcatLoggerModule::class,
            FileLoggerModule::class
        ]
)
internal interface LoggerModule {

    @Binds
    @Singleton
    fun provideAppLogger(impl: AppLoggerImpl): AppLogger
}