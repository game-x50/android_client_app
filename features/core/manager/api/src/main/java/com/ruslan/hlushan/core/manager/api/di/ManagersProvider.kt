package com.ruslan.hlushan.core.manager.api.di

import com.ruslan.hlushan.core.manager.api.ResourceManager
import com.ruslan.hlushan.core.manager.api.Settings

interface ManagersProvider {

    fun provideSettings(): Settings

    fun provideResourceManager(): ResourceManager
}