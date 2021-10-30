package com.ruslan.hlushan.core.language.impl

import com.ruslan.hlushan.core.config.app.InitAppConfig
import com.ruslan.hlushan.core.language.api.Language
import com.ruslan.hlushan.core.language.impl.dto.LanguageDTO
import com.ruslan.hlushan.core.language.impl.dto.mapLanguage
import com.ruslan.hlushan.core.manager.api.ResourceManager
import com.ruslan.hlushan.core.manager.api.Settings
import com.ruslan.hlushan.parsing.impl.utils.parsing.AppJson
import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager
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
                        list.mapNotNull { dto -> mapLanguage(dto, resourceManager) }
                                .sortedBy { language -> language.name }
                    }
}

internal fun LanguagesRepository.getCurrentLanguage(settings: Settings): Single<Language> =
        getLanguages()
                .map { list ->
                    list.firstOrNull { language -> settings.appLanguageFullCode == language.code }
                }