package com.ruslan.hlushan.core.logger.impl.di

import com.ruslan.hlushan.core.logger.api.FileLogger
import com.ruslan.hlushan.core.logger.impl.FileLoggerImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface StagingFileLoggerModule {

    @Binds
    @Singleton
    fun provideFileLogger(impl: FileLoggerImpl): FileLogger
}