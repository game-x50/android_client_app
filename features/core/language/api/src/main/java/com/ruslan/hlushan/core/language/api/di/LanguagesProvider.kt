package com.ruslan.hlushan.core.language.api.di

import com.ruslan.hlushan.core.language.api.LanguageInteractor

interface LanguagesProvider {

    fun provideLanguageInteractor(): LanguageInteractor
}