package com.ruslan.hlushan.core.language.api

import io.reactivex.Single

interface LanguageInteractor {

    fun getLanguages(): Single<List<Language>>

    fun getCurrentLanguage(): Single<Language>
}

fun LanguageInteractor.getWrappedLanguages(): Single<List<WrappedLanguage>> =
        Single.zip<List<Language>, Language, List<WrappedLanguage>>(
                this.getLanguages(),
                this.getCurrentLanguage(),
                { allLanguages, currentLanguage ->
                    allLanguages.toWrappedLanguages(currentLang = currentLanguage.code)
                }
        )