package com.ruslan.hlushan.core.config.app.di

import com.ruslan.hlushan.core.config.app.InitAppConfig

interface InitAppConfigProvider {

    fun provideInitAppConfig(): InitAppConfig
}