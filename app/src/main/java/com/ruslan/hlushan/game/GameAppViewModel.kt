package com.ruslan.hlushan.game

import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.thread.ThreadChecker
import com.ruslan.hlushan.core.ui.viewmodel.BaseViewModel
import com.ruslan.hlushan.game.screens.main.MainScreen
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

internal class GameAppViewModel
@AssistedInject
constructor(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        @Assisted private val router: Router
) : BaseViewModel(appLogger, threadChecker) {

    fun startFirstScreen() = router.newRootScreen(MainScreen())

    @AssistedFactory
    interface Factory {
        fun create(router: Router): GameAppViewModel
    }
}