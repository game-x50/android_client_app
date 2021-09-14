package com.ruslan.hlushan.core.language.impl.di

import com.ruslan.hlushan.core.language.api.LanguageInteractor
import com.ruslan.hlushan.core.language.impl.LanguageInteractorImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
internal interface LanguageModule {
    @Binds
    @Singleton
    fun languageInteractor(languagesInteractorImpl: LanguageInteractorImpl): LanguageInteractor
}