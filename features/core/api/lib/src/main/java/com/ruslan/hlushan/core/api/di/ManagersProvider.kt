package com.ruslan.hlushan.core.api.di

import com.ruslan.hlushan.core.api.managers.ResourceManager
import com.ruslan.hlushan.core.api.managers.Settings

interface ManagersProvider {

    fun provideSettings(): Settings

    fun provideResourceManager(): ResourceManager
}