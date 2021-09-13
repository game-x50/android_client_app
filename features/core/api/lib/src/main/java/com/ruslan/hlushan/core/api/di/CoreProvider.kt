package com.ruslan.hlushan.core.api.di

import com.ruslan.hlushan.core.api.managers.ResourceManager
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.managers.Settings
import com.ruslan.hlushan.core.api.model.interactors.LanguagesInteractor

interface ManagersProvider {

    fun provideSettings(): Settings

    fun provideResourceManager(): ResourceManager
}

interface LanguagesProvider {

    fun provideLanguagesInterator(): LanguagesInteractor
}

interface SchedulersProvider {

    fun provideSchedulersManager(): SchedulersManager
}