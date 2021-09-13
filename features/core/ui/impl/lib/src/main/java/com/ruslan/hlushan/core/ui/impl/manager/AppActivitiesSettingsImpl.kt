package com.ruslan.hlushan.core.ui.impl.manager

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.ruslan.hlushan.android.extensions.currentLocale
import com.ruslan.hlushan.android.extensions.updateResourcesWithNewLanguage
import com.ruslan.hlushan.android.storage.SharedPrefsProvider
import com.ruslan.hlushan.core.api.managers.Settings
import com.ruslan.hlushan.core.api.managers.appLanguageNotFullCode
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.api.utils.thread.checkThread
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.ui.api.manager.AppActivitiesSettings
import com.ruslan.hlushan.core.ui.api.utils.UiMainThreadChecker
import javax.inject.Inject

private const val KEY_ACTIVITY_LANG = "KEY_ACTIVITY_LANG_"

internal class AppActivitiesSettingsImpl
@Inject
constructor(
        appContext: Context,
        private val settings: Settings,
        private val appLogger: AppLogger
) : AppActivitiesSettings {

    private val threadChecker: ThreadChecker = UiMainThreadChecker

    private val activitiesSettingsPrefs: SharedPreferences = SharedPrefsProvider.providePrefs(
            appContext,
            "app_activities_settings_prefs"
    )

    @UiMainThread
    override fun checkLocaleAndRecreateIfNeeded(activity: Activity) {
        appLogger.log(this)

        threadChecker.checkThread()

        val appLangNotFullCode = settings.appLanguageNotFullCode
        val currentActivityLangNotFullCode = getActivityLanguageNonFullCode(activity)
        @Suppress("MaxLineLength")
        val message = ("appLangNotFullCode = " + appLangNotFullCode
                       + ", currentActivityLangNotFullCode = "
                       + if (!currentActivityLangNotFullCode.isNullOrEmpty()) currentActivityLangNotFullCode else "null")
        appLogger.log(activity, message)
        if (!currentActivityLangNotFullCode.isNullOrEmpty() && (appLangNotFullCode != currentActivityLangNotFullCode)) {
            appLogger.log(activity, "recreate")
            activity.recreate()
        }
    }

    @UiMainThread
    override fun changeLangIfNeeded(activity: Activity) {
        appLogger.log(this)

        threadChecker.checkThread()

        val resources = activity.resources
        val currentAppLangNotFullCode = settings.appLanguageNotFullCode
        val currentActivityLocaleLangCode = resources.configuration.currentLocale.language
        val message = ("currentAppLangNotFullCode = " + currentAppLangNotFullCode
                       + ", currentActivityLocaleLangCode = " +
                       if (!currentActivityLocaleLangCode.isNullOrEmpty()) currentActivityLocaleLangCode else "null")
        appLogger.log(activity, message)
        setActivityLanguageNonFullCode(activity, currentAppLangNotFullCode)

        if (currentAppLangNotFullCode != currentActivityLocaleLangCode) {
            appLogger.log(activity, "changeLang")
            resources.updateResourcesWithNewLanguage(currentAppLangNotFullCode)
        }
    }

    @UiMainThread
    override fun changeThemeIfNeeded() {
        appLogger.log(this)

        threadChecker.checkThread()

        settings.themeMode = settings.themeMode
    }

    @UiMainThread
    private fun getActivityLanguageNonFullCode(activity: Activity): String? {
        val key = KEY_ACTIVITY_LANG + activity.javaClass.simpleName
        return activitiesSettingsPrefs.getString(key, null)
    }

    @UiMainThread
    private fun setActivityLanguageNonFullCode(activity: Activity, languageNonFullCode: String) {
        val key = KEY_ACTIVITY_LANG + activity.javaClass.simpleName
        activitiesSettingsPrefs.edit()
                .putString(key, languageNonFullCode)
                .apply()
    }
}