package com.ruslan.hlushan.core.impl.utils

import android.content.SharedPreferences
import com.ruslan.hlushan.core.config.app.InitAppConfig
import com.ruslan.hlushan.core.config.app.getAppLangFullCodeByLocale
import com.ruslan.hlushan.core.language.code.LangFullCode
import com.ruslan.hlushan.core.language.code.stringValue

private const val KEY_APP_LANGUAGE_FULL_CODE = "KEY_APP_LANGUAGE_FULL_CODE"

@SuppressWarnings("TooGenericExceptionCaught")
fun SharedPreferences.getAppLangFullCode(initAppConfig: InitAppConfig): LangFullCode {

    val fullCodeStringValueFromPrefs = try {
        this.getString(KEY_APP_LANGUAGE_FULL_CODE, null)
    } catch (e: Exception) {
        null
    }

    val fullCodeFromPrefs: LangFullCode? = initAppConfig.availableLanguagesFullCodes
            .firstOrNull { singleCode -> singleCode.stringValue == fullCodeStringValueFromPrefs }

    return (fullCodeFromPrefs ?: initAppConfig.getAppLangFullCodeByLocale())
}

@SuppressWarnings("TooGenericExceptionCaught")
fun SharedPreferences.setAppLangFullCode(code: LangFullCode, availableCoder: List<LangFullCode>) {
    if (availableCoder.contains(code)) {
        try {
            this.edit()
                    .putString(KEY_APP_LANGUAGE_FULL_CODE, code.stringValue)
                    .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}