package com.ruslan.hlushan.core.logger.impl.di

import com.ruslan.hlushan.core.logger.api.EmptyFileLoggerImpl
import com.ruslan.hlushan.core.logger.api.FileLogger
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ReleaseFileLoggerModule {

    @Provides
    @Singleton
    fun provideFileLogger(): FileLogger = EmptyFileLoggerImpl
}