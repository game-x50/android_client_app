package com.ruslan.hlushan.core.api.dto

import java.util.Locale

@JvmInline
value class LangNonFullCode private constructor(val code: String) {

    companion object {

        fun fromLocale(locale: Locale): LangNonFullCode? =
                LangNonFullCode.createFrom(code = locale.language)

        fun createFrom(code: String?): LangNonFullCode? =
                if (code.isNullOrBlank()) {
                    null
                } else {
                    LangNonFullCode(code = code)
                }
    }
}

fun LangNonFullCode.toLocale(): Locale = Locale(this.code)

data class LangFullCode(
        val nonFullCode: LangNonFullCode,
        val countryCode: String
) {

    companion object {

        fun getAppLangFullCodeByLocale(initAppConfig: InitAppConfig): LangFullCode {
            val currentLocaleLangNonFullCode: LangNonFullCode? = LangNonFullCode.fromLocale(Locale.getDefault())

            return if (currentLocaleLangNonFullCode == null) {
                initAppConfig.defaultLanguageFullCode
            } else {
                val fullCodeFromLocale: LangFullCode? = initAppConfig.availableLanguagesFullCodes
                        .firstOrNull { singleCode ->
                            singleCode.nonFullCode.code.contains(currentLocaleLangNonFullCode.code)
                        }

                (fullCodeFromLocale ?: initAppConfig.defaultLanguageFullCode)
            }
        }

        fun createFrom(code: String?, countryCode: String?): LangFullCode? =
                if (countryCode != null) {
                    LangNonFullCode.createFrom(code)
                            ?.let { nonFullCode ->
                                LangFullCode(
                                        nonFullCode = nonFullCode,
                                        countryCode = countryCode
                                )
                            }
                } else {
                    null
                }
    }
}

val LangFullCode.stringValue: String
    get() = (this.nonFullCode.code + "_" + this.countryCode)