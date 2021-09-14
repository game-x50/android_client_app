package com.ruslan.hlushan.core.language.impl

import com.ruslan.hlushan.core.api.managers.Settings
import com.ruslan.hlushan.core.language.api.Language
import com.ruslan.hlushan.core.language.api.LanguageInteractor
import io.reactivex.Single
import javax.inject.Inject

internal class LanguageInteractorImpl
@Inject
constructor(
        private val languagesRepository: LanguagesRepository,
        private val settings: Settings
) : LanguageInteractor {

    override fun getLanguages(): Single<List<Language>> =
            languagesRepository.getLanguages()

    override fun getCurrentLanguage(): Single<Language> =
            languagesRepository.getCurrentLanguage(settings)
}