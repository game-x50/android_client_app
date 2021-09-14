package com.ruslan.hlushan.core.language.impl.dto

import com.ruslan.hlushan.core.api.managers.ResourceManager
import com.ruslan.hlushan.core.language.api.Language
import kotlinx.serialization.Serializable

@Serializable
internal data class LanguageDTO(
        val fullCode: String,
        val defaultName: String,
        val image: String?,
        val alphabet: String
)

internal fun mapLanguage(baseLangDTO: LanguageDTO, resourceManager: ResourceManager): Language {
    val localizedName = resourceManager.getStringResourceByName(baseLangDTO.fullCode)
    return Language(
            baseLangDTO.fullCode,
            if (localizedName.isNotEmpty()) localizedName else baseLangDTO.defaultName,
            baseLangDTO.image,
            baseLangDTO.alphabet
    )
}