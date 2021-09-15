package com.ruslan.hlushan.core.impl.utils

import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.ruslan.hlushan.android.storage.ReferencePreferencesDelegate
import com.ruslan.hlushan.core.manager.api.ThemeMode
import com.ruslan.hlushan.core.thread.UiMainThread

private const val KEY_APP_THEME_MODE = "KEY_APP_THEME_MODE"

@SuppressWarnings("FunctionNaming")
@UiMainThread
internal fun ThemeModePreferencesDelegate(
        preferences: SharedPreferences
): ReferencePreferencesDelegate<ThemeMode> = ReferencePreferencesDelegate(
        preferences = preferences,
        writer = { editor, newValue ->
            editor.putString(KEY_APP_THEME_MODE, newValue.localName)
        },
        reader = { prefs ->
            val stringValue: String? = prefs.getString(KEY_APP_THEME_MODE, null)

            val valueFromPrefs: ThemeMode? = geAvailableThemeModes()
                    .firstOrNull { mode -> mode.localName == stringValue }

            valueFromPrefs ?: getDefaultThemeMode()
        },
        onValueSaved = ::applyThemeModeToApp
)

internal fun geAvailableThemeModes(): List<ThemeMode> =
        listOf(
                ThemeMode.LIGHT,
                ThemeMode.NIGHT,
                getDefaultThemeMode()
        )

@UiMainThread
private fun applyThemeModeToApp(themeMode: ThemeMode) {
    AppCompatDelegate.setDefaultNightMode(themeMode.androidValue)
}

@AppCompatDelegate.NightMode
private val ThemeMode.androidValue: Int
    get() = when (this) {
        ThemeMode.NIGHT          -> AppCompatDelegate.MODE_NIGHT_YES
        ThemeMode.LIGHT          -> AppCompatDelegate.MODE_NIGHT_NO
        ThemeMode.SAVE_BATTERY   -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        ThemeMode.SYSTEM_DEFAULT -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

private fun getDefaultThemeMode(): ThemeMode =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ThemeMode.SYSTEM_DEFAULT
        } else {
            ThemeMode.SAVE_BATTERY
        }

private val ThemeMode.localName: String
    get() = when (this) {
        ThemeMode.NIGHT          -> "NIGHT"
        ThemeMode.LIGHT          -> "LIGHT"
        ThemeMode.SAVE_BATTERY   -> "SAVE_BATTERY"
        ThemeMode.SYSTEM_DEFAULT -> "SYSTEM_DEFAULT"
    }