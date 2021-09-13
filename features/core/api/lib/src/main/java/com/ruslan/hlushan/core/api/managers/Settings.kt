package com.ruslan.hlushan.core.api.managers

import com.ruslan.hlushan.core.api.utils.ThemeMode
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.extensions.fullLangCodeToNotFull

/**
 * Created by User on 01.02.2018.
 */

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