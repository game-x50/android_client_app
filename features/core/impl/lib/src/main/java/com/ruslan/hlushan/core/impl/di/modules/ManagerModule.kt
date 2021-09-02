package com.ruslan.hlushan.core.impl.di.modules

import com.ruslan.hlushan.core.api.managers.ResourceManager
import com.ruslan.hlushan.core.api.managers.Settings
import com.ruslan.hlushan.core.impl.managers.ResourceManagerImpl
import com.ruslan.hlushan.core.impl.managers.SettingsImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Created by User on 01.02.2018.
 */

@Module
internal interface ManagerModule {

    @Binds
    @Singleton
    fun settings(settingsImpl: SettingsImpl): Settings

    @Binds
    @Singleton
    fun resourceManager(resourceManagerImpl: ResourceManagerImpl): ResourceManager
}