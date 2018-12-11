package com.ruslan.hlushan.core.api.di

import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.log.ErrorLogger
import com.ruslan.hlushan.core.api.log.FileLogger
import com.ruslan.hlushan.core.api.managers.ResourceManager
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.managers.Settings
import com.ruslan.hlushan.core.api.model.interactors.LanguagesInteractor
import com.ruslan.hlushan.core.api.utils.InitAppConfig

/**
 * @author Ruslan Hlushan on 10/29/18.
 */
interface CoreProvider : ManagersProvider,
                         LoggersProvider,
                         LanguagesProvider,
                         SchedulersProvider,
                         InitAppConfigProvider

interface ManagersProvider {

    fun provideSettings(): Settings

    fun provideResourceManager(): ResourceManager
}

interface LoggersProvider {

    fun provideAppLogger(): AppLogger

    fun provideFileLogger(): FileLogger

    fun provideErrorLogger(): ErrorLogger
}

interface LanguagesProvider {

    fun provideLanguagesInterator(): LanguagesInteractor
}

interface SchedulersProvider {

    fun provideSchedulersManager(): SchedulersManager
}

interface InitAppConfigProvider {

    fun provideInitAppConfig(): InitAppConfig
}