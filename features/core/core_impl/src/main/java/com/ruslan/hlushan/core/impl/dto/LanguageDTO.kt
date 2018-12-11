package com.ruslan.hlushan.core.impl.dto

import com.ruslan.hlushan.core.api.dto.Language
import com.ruslan.hlushan.core.api.managers.ResourceManager
import kotlinx.serialization.Serializable

/**
 * Created by User on 02.02.2018.
 */

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