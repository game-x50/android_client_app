package com.ruslan.hlushan.core.impl.model.interactors

import com.ruslan.hlushan.core.api.dto.Language
import com.ruslan.hlushan.core.api.managers.Settings
import com.ruslan.hlushan.core.api.model.interactors.LanguagesInteractor
import com.ruslan.hlushan.core.impl.model.repositories.impl.LanguagesRepository
import com.ruslan.hlushan.core.impl.model.repositories.impl.getCurrentLanguage
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by User on 02.02.2018.
 */

internal class LanguagesInteractorImpl
@Inject
constructor(
        private val languagesRepository: LanguagesRepository,
        private val settings: Settings
) : LanguagesInteractor {

    override fun getLanguages(): Single<List<Language>> =
            languagesRepository.getLanguages()

    override fun getCurrentLanguage(): Single<Language> =
            languagesRepository.getCurrentLanguage(settings)
}