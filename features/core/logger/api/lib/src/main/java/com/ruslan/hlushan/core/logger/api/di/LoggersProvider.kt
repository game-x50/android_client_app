package com.ruslan.hlushan.core.logger.api.di

import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.logger.api.ErrorLogger
import com.ruslan.hlushan.core.logger.api.FileLogger

interface LoggersProvider {

    fun provideAppLogger(): AppLogger

    fun provideFileLogger(): FileLogger

    fun provideErrorLogger(): ErrorLogger
}