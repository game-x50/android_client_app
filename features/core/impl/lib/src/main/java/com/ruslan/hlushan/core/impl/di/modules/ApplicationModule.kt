package com.ruslan.hlushan.core.impl.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.ruslan.hlushan.android.extensions.wrapContextWithNewLanguage
import com.ruslan.hlushan.android.storage.SharedPrefsProvider
import com.ruslan.hlushan.core.api.dto.InitAppConfig
import com.ruslan.hlushan.core.extensions.fullLangCodeToNotFull
import com.ruslan.hlushan.core.impl.di.annotations.SettingsPrefs
import com.ruslan.hlushan.core.impl.utils.getAppLangFullCode
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal object ApplicationModule {

    @JvmStatic
    @Provides
    @Singleton
    @SettingsPrefs
    fun provideSharedPreferences(app: Application): SharedPreferences =
            SharedPrefsProvider.providePrefs(app, "default_app_settings")

    @JvmStatic
    @Provides
    @Singleton
    fun provideContext(
            app: Application,
            @SettingsPrefs prefs: SharedPreferences,
            initAppConfig: InitAppConfig
    ): Context {
        val appLangNotFullCode = fullLangCodeToNotFull(
                prefs.getAppLangFullCode(
                        initAppConfig.availableLanguagesFullCodes,
                        initAppConfig.defaultLanguageFullCode
                ),
                initAppConfig.defaultLanguageFullCode
        )
        val appContext = app.applicationContext

        return appContext.wrapContextWithNewLanguage(appLangNotFullCode)
    }
}