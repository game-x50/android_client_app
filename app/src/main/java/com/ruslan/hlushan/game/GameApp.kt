package com.ruslan.hlushan.game

import androidx.work.Configuration
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ruslan.hlushan.core.config.app.InitAppConfig
import com.ruslan.hlushan.core.di.ClassInstanceMap
import com.ruslan.hlushan.core.extensions.lazyUnsafe
import com.ruslan.hlushan.core.impl.BaseApplication
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider
import com.ruslan.hlushan.game.di.GameAppComponent
import com.ruslan.hlushan.game.error.ErrorLoggerImpl
import com.ruslan.hlushan.third_party.androidx.work.manager.utils.CompositeWorkerFactory
import javax.inject.Inject

internal class GameApp : BaseApplication(), Configuration.Provider {

    override val APP_TAG: String get() = BuildConfig.APP_TAG

    private val initAppConfig: InitAppConfig by lazyUnsafe {
        GameAppComponent.buildInitAppConfig(
                app = this,
                appTag = APP_TAG
        )
    }

    private val loggersProviderDelegate = lazyUnsafe {
        GameAppComponent.buildLoggersProvider(
                app = this,
                initAppConfig = initAppConfig
        )
    }

    private val loggersProvider: LoggersProvider by loggersProviderDelegate

    override val appLogger: AppLogger
        get() = loggersProvider.provideAppLogger()

    override val iBaseInjector: GameAppComponent by lazyUnsafe {
        GameAppComponent.init(
                app = this,
                initAppConfig = initAppConfig,
                loggersProvider = loggersProvider
        )
    }

    @Inject
    lateinit var compositeWorkerFactory: CompositeWorkerFactory

    override val components: ClassInstanceMap = ClassInstanceMap()

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

    override fun initCrashlytics() =
            ErrorLoggerImpl.init()

    override fun initDagger2AndInject() =
            iBaseInjector.inject(this)

    override fun areAppLoggersInitialized(): Boolean =
            loggersProviderDelegate.isInitialized()

    override fun getWorkManagerConfiguration(): Configuration =
            Configuration.Builder()
                    .setWorkerFactory(compositeWorkerFactory)
                    .build()
}