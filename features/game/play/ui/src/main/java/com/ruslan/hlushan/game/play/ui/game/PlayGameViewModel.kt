package com.ruslan.hlushan.game.play.ui.game

import androidx.annotation.CallSuper
import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.command.CommandQueue
import com.ruslan.hlushan.core.ui.api.presentation.command.MutableCommandQueue
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.AddToEndSingleStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.HandleStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.OneExecutionStateStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.SkipStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.StrategyCommand
import com.ruslan.hlushan.core.ui.api.presentation.presenter.BaseViewModel
import com.ruslan.hlushan.game.api.play.PlayRecordsInteractor
import com.ruslan.hlushan.game.api.play.dto.GameState
import com.ruslan.hlushan.game.play.ui.GameScopeMarkerRepository
import com.ruslan.hlushan.rxjava2.extensions.safetyDispose
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.threeten.bp.Duration
import java.util.concurrent.TimeUnit

internal abstract class PlayGameViewModel(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        private val schedulersManager: SchedulersManager,
        private val gameScopeMarkerRepository: GameScopeMarkerRepository,
        protected val router: Router,
        protected val playRecordsInteractor: PlayRecordsInteractor
) : BaseViewModel(appLogger, threadChecker) {

    private val timerGranularity: Long get() = 1
    private val timerGranularityUnit: TimeUnit get() = TimeUnit.SECONDS
    private val timerGranularityDuration: Duration = Duration.ofSeconds(timerGranularity)

    @UiMainThread
    private var timerDisposable: Disposable? = null

    @UiMainThread
    var playedDuration: Duration = Duration.ZERO
        private set(newValue) {
            field = newValue
            showPlayedTime()
        }

    @UiMainThread
    private val mutableCommandsQueue = MutableCommandQueue<Command>()

    val commandsQueue: CommandQueue<Command> get() = mutableCommandsQueue

    @CallSuper
    @UiMainThread
    override fun onAfterAttachView() {
        super.onAfterAttachView()
        showPlayedTime()
    }

    @UiMainThread
    fun restore(restoredPlayedDuration: Duration) {
        this.playedDuration = restoredPlayedDuration
    }

    @UiMainThread
    fun onPressedExitWithState(result: GameState) {
        if (shouldBeSaveResultQuestionDialogShown(result)) {
            mutableCommandsQueue.add(
                    Command.ShowSaveResultQuestionDialog(result.current.immutableNumbersMatrix.totalSum)
            )
        } else {
            markAsNonPlayingAndExit()
        }
    }

    @UiMainThread
    fun exitAndSaveResultIfNeeded(result: GameState, save: Boolean) =
            if (save) {
                saveRecord(result)
            } else {
                markAsNonPlayingAndExit()
            }

    @UiMainThread
    protected abstract fun shouldBeSaveResultQuestionDialogShown(result: GameState): Boolean

    @UiMainThread
    protected abstract fun saveRecord(result: GameState)

    @UiMainThread
    protected abstract fun markAsNonPlayingAndExit()

    @UiMainThread
    protected abstract fun getTotalPlayedDuration(): Duration

    @UiMainThread
    fun startTimer() {
        stopTimer()
        timerDisposable = Observable.interval(timerGranularity, timerGranularityUnit, schedulersManager.computation)
                .observeOn(schedulersManager.ui)
                .doOnNext { playedDuration += timerGranularityDuration }
                .subscribe(
                        { appLogger.log(this, "playedDuration =: $playedDuration") },
                        { error -> mutableCommandsQueue.add(Command.ShowError(error)) }
                )
        timerDisposable?.joinWhileViewAttached()
    }

    @UiMainThread
    fun stopTimer() {
        timerDisposable?.safetyDispose()
        timerDisposable = null
    }

    @UiMainThread
    private fun showPlayedTime() =
            mutableCommandsQueue.add(Command.ShowPlayedTime(getTotalPlayedDuration()))

    @UiMainThread
    protected fun Completable.handleSavingRecord(result: GameState?) =
            observeOn(schedulersManager.ui)
                    .doOnSubscribe { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = true)) }
                    .doOnComplete {
                        gameScopeMarkerRepository.wasListChanged = (result != null)
                        router.exit()
                    }
                    .doFinally { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = false)) }
                    .subscribe(
                            { appLogger.log(this, "saved success: $result") },
                            { error -> mutableCommandsQueue.add(Command.ShowError(error)) }
                    )
                    .joinUntilDestroy()

    sealed class Command : StrategyCommand {

        class ShowSimpleProgress(val show: Boolean) : Command() {
            override fun produceStrategy(): HandleStrategy = AddToEndSingleStrategy()
        }

        class ShowPlayedTime(val playDuration: Duration) : Command() {
            override fun produceStrategy(): HandleStrategy = AddToEndSingleStrategy()
        }

        class ShowSaveResultQuestionDialog(val totalSum: Int) : Command() {
            override fun produceStrategy(): HandleStrategy = SkipStrategy()
        }

        class ShowError(val error: Throwable) : Command() {
            override fun produceStrategy(): HandleStrategy = OneExecutionStateStrategy()
        }
    }
}