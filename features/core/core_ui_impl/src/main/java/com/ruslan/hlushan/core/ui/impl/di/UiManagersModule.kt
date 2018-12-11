package com.ruslan.hlushan.core.ui.impl.di

import com.ruslan.hlushan.core.ui.api.manager.AppActivitiesSettings
import com.ruslan.hlushan.core.ui.impl.manager.AppActivitiesSettingsImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Created by User on 01.02.2018.
 */

@Module
internal interface UiManagersModule {

    @Binds
    @Singleton
    fun appActivitiesSettings(impl: AppActivitiesSettingsImpl): AppActivitiesSettings
}