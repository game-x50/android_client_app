package com.ruslan.hlushan.core.impl.utils

import android.content.SharedPreferences
import com.ruslan.hlushan.core.api.dto.InitAppConfig
import com.ruslan.hlushan.core.api.dto.LangFullCode
import com.ruslan.hlushan.core.api.dto.stringValue

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

    return (fullCodeFromPrefs ?: LangFullCode.getAppLangFullCodeByLocale(initAppConfig))
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