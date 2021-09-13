package com.ruslan.hlushan.game

import androidx.work.Configuration
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ruslan.hlushan.core.api.di.ClassInstanceMap
import com.ruslan.hlushan.core.api.utils.InitAppConfig
import com.ruslan.hlushan.core.extensions.lazyUnsafe
import com.ruslan.hlushan.core.impl.BaseApplication
import com.ruslan.hlushan.game.di.GameAppComponent
import com.ruslan.hlushan.game.error.ErrorLoggerImpl
import com.ruslan.hlushan.third_party.androidx.work.manager.utils.CompositeWorkerFactory
import java.io.File
import javax.inject.Inject

internal class GameApp : BaseApplication(), Configuration.Provider {

    @Inject
    lateinit var compositeWorkerFactory: CompositeWorkerFactory

    override val APP_TAG: String get() = BuildConfig.APP_TAG

    override val initAppConfig: InitAppConfig by lazyUnsafe {
        InitAppConfig(
                appTag = APP_TAG,
                versionCode = BuildConfig.VERSION_CODE,
                versionName = BuildConfig.VERSION_NAME,
                isLogcatEnabled = BuildConfig.IS_LOGCAT_ENABLED,
                fileLogsFolder = File(this.applicationContext.cacheDir, "fileLogs"),
                languagesJsonRawResId = com.ruslan.hlushan.game.settings.ui.R.raw.languages,
                defaultLanguageFullCode = BuildConfig.DEFAULT_LANGUAGE_NON_FULL_CODE,
                availableLanguagesFullCodes = BuildConfig.AVAILABLE_LANGUAGES_FULL_CODES.toList()
        )
    }

    override val components: ClassInstanceMap = ClassInstanceMap()

    override val iBaseInjector: GameAppComponent by lazyUnsafe { GameAppComponent.init(this, initAppConfig) }

    override fun initDagger2AndInject() = iBaseInjector.inject(this)

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

    override fun initCrashlytics() = ErrorLoggerImpl.init()

    override fun getWorkManagerConfiguration(): Configuration =
            Configuration.Builder()
                    .setWorkerFactory(compositeWorkerFactory)
                    .build()
}