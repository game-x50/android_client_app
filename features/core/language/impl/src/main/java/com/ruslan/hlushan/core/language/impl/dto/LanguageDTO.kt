package com.ruslan.hlushan.core.language.impl.dto

import com.ruslan.hlushan.core.language.api.Language
import com.ruslan.hlushan.core.language.code.LangFullCode
import com.ruslan.hlushan.core.language.code.LangNonFullCode
import com.ruslan.hlushan.core.language.code.stringValue
import com.ruslan.hlushan.core.manager.api.ResourceManager
import kotlinx.serialization.Serializable

@Serializable
internal data class LanguageDTO(
        val code: String,
        val countryCode: String,
        val defaultName: String,
        val image: String?,
        val alphabet: String
)

internal fun mapLanguage(baseLangDTO: LanguageDTO, resourceManager: ResourceManager): Language? {
    val nonFullCode = LangNonFullCode.createFrom(baseLangDTO.code)

    return if (nonFullCode != null) {
        val code = LangFullCode(nonFullCode = nonFullCode, countryCode = baseLangDTO.countryCode)

        val localizedName = resourceManager.getStringResourceByName(code.stringValue)
        val name = if (localizedName.isBlank()) {
            localizedName
        } else {
            baseLangDTO.defaultName
        }

        Language(
                code = code,
                name = name,
                imageUrl = baseLangDTO.image,
                alphabet = baseLangDTO.alphabet
        )
    } else {
        null
    }
}