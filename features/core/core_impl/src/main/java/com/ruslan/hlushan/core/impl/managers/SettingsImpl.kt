package com.ruslan.hlushan.core.impl.managers

import android.content.SharedPreferences
import com.ruslan.hlushan.core.api.managers.Settings
import com.ruslan.hlushan.core.api.utils.InitAppConfig
import com.ruslan.hlushan.core.api.utils.ThemeMode
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.impl.di.annotations.SettingsPrefs
import com.ruslan.hlushan.core.impl.utils.applyThemeModeToApp
import com.ruslan.hlushan.core.impl.utils.geAvailableThemeModes
import com.ruslan.hlushan.core.impl.utils.getAppLangFullCode
import com.ruslan.hlushan.core.impl.utils.getAppThemeMode
import com.ruslan.hlushan.core.impl.utils.setAppLangFullCode
import com.ruslan.hlushan.core.impl.utils.setAppThemeMode
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

/**
 * @author Ruslan Hlushan on 10/18/18.
 */
internal class SettingsImpl
@Inject
constructor(
        @SettingsPrefs
        private val sharedPreferences: SharedPreferences,
        private val initAppConfig: InitAppConfig
) : Settings {

    private val langFullCodeReference = AtomicReference(sharedPreferences.getAppLangFullCode(availableLanguagesFullCodes, defaultLanguageFullCode))
    private val themeModeReference = AtomicReference(sharedPreferences.getAppThemeMode())

    override val defaultLanguageFullCode: String get() = initAppConfig.defaultLanguageFullCode

    override val availableLanguagesFullCodes: List<String> get() = initAppConfig.availableLanguagesFullCodes

    override var appLanguageFullCode: String
        get() = langFullCodeReference.get()
        set(newLanguageFullCode) {
            langFullCodeReference.set(newLanguageFullCode)
            sharedPreferences.setAppLangFullCode(newLanguageFullCode, availableLanguagesFullCodes)
        }

    override var themeMode: ThemeMode
        get() = themeModeReference.get()
        @UiMainThread
        set(newThemeMode) {
            themeModeReference.set(newThemeMode)
            sharedPreferences.setAppThemeMode(newThemeMode)

            applyThemeModeToApp(newThemeMode)
        }

    override val availableThemeModes: List<ThemeMode> get() = geAvailableThemeModes()
}