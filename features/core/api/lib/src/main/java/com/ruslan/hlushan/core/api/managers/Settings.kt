package com.ruslan.hlushan.core.api.managers

import com.ruslan.hlushan.core.api.dto.ThemeMode
import com.ruslan.hlushan.core.extensions.fullLangCodeToNotFull
import com.ruslan.hlushan.core.thread.UiMainThread

interface Settings {

    val defaultLanguageFullCode: String

    val availableLanguagesFullCodes: List<String>

    var appLanguageFullCode: String

    var themeMode: ThemeMode
        @UiMainThread
        set

    val availableThemeModes: List<ThemeMode>
}

val Settings.appLanguageNotFullCode: String
    get() = fullLangCodeToNotFull(appLanguageFullCode, defaultLanguageFullCode)