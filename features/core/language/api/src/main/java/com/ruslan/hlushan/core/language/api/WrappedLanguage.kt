package com.ruslan.hlushan.core.language.api

data class WrappedLanguage(
        val language: Language,
        val isAppLanguage: Boolean
)

fun List<Language>.toWrappedLanguages(currentLangFullCode: String): List<WrappedLanguage> =
        this.map { language ->
            WrappedLanguage(
                    language = language,
                    isAppLanguage = (currentLangFullCode == language.fullCode)
            )
        }