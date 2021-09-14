package com.ruslan.hlushan.game.settings.ui.languages

import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.managers.Settings
import com.ruslan.hlushan.core.command.CommandQueue
import com.ruslan.hlushan.core.command.MutableCommandQueue
import com.ruslan.hlushan.core.command.strategy.AddToEndSingleStrategy
import com.ruslan.hlushan.core.command.strategy.HandleStrategy
import com.ruslan.hlushan.core.command.strategy.OneExecutionStateStrategy
import com.ruslan.hlushan.core.command.strategy.StrategyCommand
import com.ruslan.hlushan.core.language.api.LanguageInteractor
import com.ruslan.hlushan.core.language.api.WrappedLanguage
import com.ruslan.hlushan.core.language.api.getWrappedLanguages
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.thread.ThreadChecker
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.viewmodel.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

internal class LanguagesViewModel
@AssistedInject
constructor(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        schedulersManager: SchedulersManager,
        languagesInteractor: LanguageInteractor,
        @Assisted private val router: Router,
        private val settings: Settings
) : BaseViewModel(appLogger, threadChecker) {

    @UiMainThread
    private val mutableCommandsQueue = MutableCommandQueue<Command>()

    val commandsQueue: CommandQueue<Command> get() = mutableCommandsQueue

    init {
        languagesInteractor.getWrappedLanguages()
                .map { list -> list.map { wrappedLang -> LanguageRecyclerItem(wrappedLang) } }
                .observeOn(schedulersManager.ui)
                .doOnSuccess { languages -> mutableCommandsQueue.add(Command.ShowAvailableLanguages(languages)) }
                .subscribe(
                        { list -> appLogger.log(this@LanguagesViewModel, "getLanguages: SUCCESS") },
                        { error -> mutableCommandsQueue.add(Command.ShowError(error)) }
                )
                .joinUntilDestroy()
    }

    @UiMainThread
    fun setApplicationLanguage(wrappedLanguage: WrappedLanguage) {
        if (!wrappedLanguage.isAppLanguage) {
            settings.appLanguageFullCode = wrappedLanguage.language.fullCode
            router.exit()
        }
    }

    sealed class Command : StrategyCommand {

        class ShowAvailableLanguages(val languages: List<LanguageRecyclerItem>) : Command() {
            override fun produceStrategy(): HandleStrategy = AddToEndSingleStrategy()
        }

        class ShowError(val error: Throwable) : Command() {
            override fun produceStrategy(): HandleStrategy = OneExecutionStateStrategy()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(router: Router): LanguagesViewModel
    }
}