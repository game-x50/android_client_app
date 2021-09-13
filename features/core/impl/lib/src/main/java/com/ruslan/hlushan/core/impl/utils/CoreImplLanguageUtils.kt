package com.ruslan.hlushan.core.impl.utils

import android.content.SharedPreferences
import com.ruslan.hlushan.core.extensions.defIfEmpty
import java.util.Locale

private const val KEY_APP_LANGUAGE_FULL_CODE = "KEY_APP_LANGUAGE_FULL_CODE"

@SuppressWarnings("TooGenericExceptionCaught")
fun SharedPreferences.getAppLangFullCode(
        availableLanguagesFullCodes: List<String>,
        defaultLanguageFullCode: String
): String {
    var lang = ""
    try {
        lang = this.getString(KEY_APP_LANGUAGE_FULL_CODE, lang) ?: lang
    } catch (e: Exception) {
        e.printStackTrace()
    }
    if (!availableLanguagesFullCodes.contains(lang)) {

        val locale: Locale? = Locale.getDefault()
        val currentLocaleLang = defIfEmpty(locale?.language, defaultLanguageFullCode)
        val defLang: String? = availableLanguagesFullCodes
                .firstOrNull { aLangFullCode -> aLangFullCode.contains(currentLocaleLang) }

        lang = defIfEmpty(defLang, defaultLanguageFullCode)
    }
    return lang
}

@SuppressWarnings("TooGenericExceptionCaught")
fun SharedPreferences.setAppLangFullCode(languageFullCode: String, availableLanguagesFullCodes: List<String>) {
    if (availableLanguagesFullCodes.contains(languageFullCode)) {
        try {
            this.edit()
                    .putString(KEY_APP_LANGUAGE_FULL_CODE, languageFullCode)
                    .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}