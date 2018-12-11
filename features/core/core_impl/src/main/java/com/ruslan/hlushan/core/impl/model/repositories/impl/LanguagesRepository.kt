package com.ruslan.hlushan.core.impl.model.repositories.impl

import com.ruslan.hlushan.core.api.dto.Language
import com.ruslan.hlushan.core.api.managers.ResourceManager
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.managers.Settings
import com.ruslan.hlushan.core.api.utils.InitAppConfig
import com.ruslan.hlushan.core.impl.dto.LanguageDTO
import com.ruslan.hlushan.core.impl.dto.mapLanguage
import com.ruslan.hlushan.parsing.impl.utils.parsing.AppJson
import io.reactivex.Single
import kotlinx.serialization.decodeFromString
import javax.inject.Inject

internal class LanguagesRepository
@Inject
constructor(
        private val schedulersManager: SchedulersManager,
        private val resourceManager: ResourceManager,
        private val initAppConfig: InitAppConfig
) {
    fun getLanguages(): Single<List<Language>> =
            resourceManager.readRawTextFile(initAppConfig.languagesJsonRawResId)
                    .observeOn(schedulersManager.computation)
                    .map { jsonStringWrapper -> AppJson.decodeFromString<List<LanguageDTO>>(jsonStringWrapper.value!!) }
                    .map { list ->
                        list.map { dto -> mapLanguage(dto, resourceManager) }
                                .sortedBy { language -> language.name }
                    }
}

internal fun LanguagesRepository.getCurrentLanguage(settings: Settings): Single<Language> =
        getLanguages()
                .map { list ->
                    list.firstOrNull { language -> settings.appLanguageFullCode == language.fullCode }
                }