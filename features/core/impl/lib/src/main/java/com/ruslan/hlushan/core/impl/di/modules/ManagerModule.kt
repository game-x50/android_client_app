package com.ruslan.hlushan.core.impl.di.modules

import com.ruslan.hlushan.core.impl.managers.ResourceManagerImpl
import com.ruslan.hlushan.core.impl.managers.SettingsImpl
import com.ruslan.hlushan.core.manager.api.ResourceManager
import com.ruslan.hlushan.core.manager.api.Settings
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal interface ManagerModule {

    @Binds
    @Singleton
    fun settings(settingsImpl: SettingsImpl): Settings

    @Binds
    @Singleton
    fun resourceManager(resourceManagerImpl: ResourceManagerImpl): ResourceManager
}