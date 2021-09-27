package com.ruslan.hlushan.core.impl.managers

import android.content.SharedPreferences
import com.ruslan.hlushan.core.api.dto.InitAppConfig
import com.ruslan.hlushan.core.api.dto.LangFullCode
import com.ruslan.hlushan.core.impl.di.annotations.SettingsPrefs
import com.ruslan.hlushan.core.impl.utils.ThemeModePreferencesDelegate
import com.ruslan.hlushan.core.impl.utils.applyThemeModeToApp
import com.ruslan.hlushan.core.impl.utils.geAvailableThemeModes
import com.ruslan.hlushan.core.impl.utils.getAppLangFullCode
import com.ruslan.hlushan.core.impl.utils.setAppLangFullCode
import com.ruslan.hlushan.core.manager.api.Settings
import com.ruslan.hlushan.core.manager.api.ThemeMode
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

internal class SettingsImpl
@Inject
constructor(
        @SettingsPrefs
        private val sharedPreferences: SharedPreferences,
        private val initAppConfig: InitAppConfig
) : Settings {

    private val langFullCodeReference = AtomicReference(
            sharedPreferences.getAppLangFullCode(initAppConfig)
    )

    override var appLanguageFullCode: LangFullCode
        get() = langFullCodeReference.get()
        set(newLanguageFullCode) {
            langFullCodeReference.set(newLanguageFullCode)
            sharedPreferences.setAppLangFullCode(newLanguageFullCode, initAppConfig.availableLanguagesFullCodes)
        }

    override var themeMode: ThemeMode by ThemeModePreferencesDelegate(preferences = sharedPreferences)

    override val availableThemeModes: List<ThemeMode> get() = geAvailableThemeModes()

    override fun reInitThemeMode() = applyThemeModeToApp(this.themeMode)
}