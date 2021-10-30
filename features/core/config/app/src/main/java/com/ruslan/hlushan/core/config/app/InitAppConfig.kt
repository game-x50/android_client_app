package com.ruslan.hlushan.core.config.app

import androidx.annotation.RawRes
import com.ruslan.hlushan.core.language.code.LangFullCode
import com.ruslan.hlushan.core.language.code.LangNonFullCode
import java.io.File
import java.util.Locale

@SuppressWarnings("LongParameterList")
data class InitAppConfig(
        val versionCode: Int,
        val versionName: String,
        val appTag: String,
        val isLogcatEnabled: Boolean,
        val fileLogsFolder: File,
        @RawRes val languagesJsonRawResId: Int,
        val defaultLanguageFullCode: LangFullCode,
        val availableLanguagesFullCodes: List<LangFullCode>
)

fun InitAppConfig.getAppLangFullCodeByLocale(): LangFullCode {
    val currentLocaleLangNonFullCode: LangNonFullCode? = LangNonFullCode.fromLocale(Locale.getDefault())

    return if (currentLocaleLangNonFullCode == null) {
        this.defaultLanguageFullCode
    } else {
        val fullCodeFromLocale: LangFullCode? = this.availableLanguagesFullCodes
                .firstOrNull { singleCode ->
                    singleCode.nonFullCode.code.contains(currentLocaleLangNonFullCode.code)
                }

        (fullCodeFromLocale ?: this.defaultLanguageFullCode)
    }
}