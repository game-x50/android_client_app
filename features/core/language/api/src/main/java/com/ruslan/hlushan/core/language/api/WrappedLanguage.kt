package com.ruslan.hlushan.core.language.api

import com.ruslan.hlushan.core.api.dto.LangFullCode

data class WrappedLanguage(
        val language: Language,
        val isAppLanguage: Boolean
)

fun List<Language>.toWrappedLanguages(currentLang: LangFullCode): List<WrappedLanguage> =
        this.map { language ->
            WrappedLanguage(
                    language = language,
                    isAppLanguage = (currentLang == language.code)
            )
        }