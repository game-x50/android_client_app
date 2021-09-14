package com.ruslan.hlushan.core.ui.api.di

import com.ruslan.hlushan.core.thread.ThreadChecker
import com.ruslan.hlushan.core.ui.api.manager.AppActivitiesSettings
import com.ruslan.hlushan.core.ui.api.utils.ViewModifier

interface UiCoreProvider {

    fun provideAppActivitiesSettings(): AppActivitiesSettings

    fun provideThreadChecker(): ThreadChecker

    fun provideViewModifier(): ViewModifier
}