package com.ruslan.hlushan.core.api.model.interactors

import com.ruslan.hlushan.core.api.dto.Language
import com.ruslan.hlushan.core.api.dto.WrappedLanguage
import com.ruslan.hlushan.core.api.dto.toWrappedLanguages
import io.reactivex.Single

/**
 * Created by User on 02.02.2018.
 */

interface LanguagesInteractor {

    fun getLanguages(): Single<List<Language>>

    fun getCurrentLanguage(): Single<Language>
}

fun LanguagesInteractor.getWrappedLanguages(): Single<List<WrappedLanguage>> =
        Single.zip<List<Language>, Language, List<WrappedLanguage>>(
                this.getLanguages(),
                this.getCurrentLanguage(),
                { allLanguages, currentLanguage ->
                    allLanguages.toWrappedLanguages(currentLangFullCode = currentLanguage.fullCode)
                }
        )