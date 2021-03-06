package com.ruslan.hlushan.core.manager.api

import com.ruslan.hlushan.core.language.code.LangFullCode
import com.ruslan.hlushan.core.thread.UiMainThread

interface Settings {

    var appLanguageFullCode: LangFullCode

    var themeMode: ThemeMode
        @UiMainThread
        set

    val availableThemeModes: List<ThemeMode>

    fun reInitThemeMode()
}