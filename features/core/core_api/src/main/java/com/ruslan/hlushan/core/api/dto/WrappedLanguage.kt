package com.ruslan.hlushan.core.api.dto

/**
 * Created by Ruslan on 04.02.2018.
 */

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