package com.ruslan.hlushan.core.ui.impl.di

import com.ruslan.hlushan.core.ui.api.manager.AppActivitiesSettings
import com.ruslan.hlushan.core.ui.impl.manager.AppActivitiesSettingsImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal interface UiManagersModule {

    @Binds
    @Singleton
    fun appActivitiesSettings(impl: AppActivitiesSettingsImpl): AppActivitiesSettings
}