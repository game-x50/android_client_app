package com.ruslan.hlushan.game

import androidx.work.Configuration
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ruslan.hlushan.core.di.ClassInstanceMap
import com.ruslan.hlushan.core.extensions.lazyUnsafe
import com.ruslan.hlushan.core.impl.BaseApplication
import com.ruslan.hlushan.game.di.GameAppComponent
import com.ruslan.hlushan.game.error.ErrorLoggerImpl
import com.ruslan.hlushan.third_party.androidx.work.manager.utils.CompositeWorkerFactory
import javax.inject.Inject

internal class GameApp : BaseApplication(), Configuration.Provider {

    @Inject
    lateinit var compositeWorkerFactory: CompositeWorkerFactory

    override val APP_TAG: String get() = BuildConfig.APP_TAG

    override val components: ClassInstanceMap = ClassInstanceMap()

    override val iBaseInjector: GameAppComponent by lazyUnsafe { GameAppComponent.init(this) }

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