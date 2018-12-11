package com.ruslan.hlushan.game.settings.ui.menu

import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.model.interactors.LanguagesInteractor
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.command.CommandQueue
import com.ruslan.hlushan.core.ui.api.presentation.command.MutableCommandQueue
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.AddToEndSingleStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.HandleStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.OneExecutionStateStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.StrategyCommand
import com.ruslan.hlushan.core.ui.api.presentation.presenter.BaseViewModel
import com.ruslan.hlushan.game.settings.ui.di.SettingsOutScreenCreator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@SuppressWarnings("LongParameterList")
internal class SettingsMenuViewModel
@AssistedInject
constructor(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        @Assisted private val router: Router,
        private val schedulersManager: SchedulersManager,
        private val languagesInteractor: LanguagesInteractor,
        private val settingsOutScreenCreator: SettingsOutScreenCreator
) : BaseViewModel(appLogger, threadChecker) {

    @UiMainThread
    private val mutableCommandsQueue = MutableCommandQueue<Command>()

    val commandsQueue: CommandQueue<Command> get() = mutableCommandsQueue

    @UiMainThread
    override fun onAfterAttachView() {
        super.onAfterAttachView()
        showAppLang()
    }

    private fun showAppLang() {
        languagesInteractor.getCurrentLanguage()
                .observeOn(schedulersManager.ui)
                .doOnSubscribe { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = true)) }
                .doOnSuccess { currentLanguage -> mutableCommandsQueue.add(Command.ShowAppLanguage(currentLanguage.name, currentLanguage.imageUrl)) }
                .doFinally { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = false)) }
                .subscribe(
                        { currentLanguage -> appLogger.log(this@SettingsMenuViewModel, "currentLanguage = $currentLanguage") },
                        { error -> mutableCommandsQueue.add(Command.ShowError(error)) }
                )
                .joinWhileViewAttached()
    }

    @UiMainThread
    fun openUserProfile() = router.navigateTo(settingsOutScreenCreator.createProfileScreen())

    @UiMainThread
    fun openTop() = router.navigateTo(settingsOutScreenCreator.createTopScreen())

    sealed class Command : StrategyCommand {

        class ShowSimpleProgress(val show: Boolean) : Command() {
            override fun produceStrategy(): HandleStrategy = AddToEndSingleStrategy()
        }

        class ShowAppLanguage(val appLanguage: String, val imageResName: String?) : Command() {
            override fun produceStrategy(): HandleStrategy = AddToEndSingleStrategy()
        }

        class ShowError(val error: Throwable) : Command() {
            override fun produceStrategy(): HandleStrategy = OneExecutionStateStrategy()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(router: Router): SettingsMenuViewModel
    }
}