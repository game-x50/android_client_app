package com.ruslan.hlushan.game.play.ui.screens.records

import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.command.CommandQueue
import com.ruslan.hlushan.core.command.MutableCommandQueue
import com.ruslan.hlushan.core.command.strategy.AddToEndSingleStrategy
import com.ruslan.hlushan.core.command.strategy.HandleStrategy
import com.ruslan.hlushan.core.command.strategy.OneExecutionStateStrategy
import com.ruslan.hlushan.core.command.strategy.StrategyCommand
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.pagination.api.PaginationPagesRequest
import com.ruslan.hlushan.core.pagination.api.PaginationResponse
import com.ruslan.hlushan.core.pagination.api.PaginationState
import com.ruslan.hlushan.core.pagination.api.itemsOrEmpty
import com.ruslan.hlushan.core.pagination.api.map
import com.ruslan.hlushan.core.thread.ThreadChecker
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.pagination.viewmodel.PaginationViewModel
import com.ruslan.hlushan.game.api.GameSettings
import com.ruslan.hlushan.game.api.auth.AuthInteractor
import com.ruslan.hlushan.game.api.auth.observeUserIsAuthenticated
import com.ruslan.hlushan.game.api.play.PlayRecordsInteractor
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.RequestParams
import com.ruslan.hlushan.game.api.sync.StartSyncUseCase
import com.ruslan.hlushan.game.api.sync.observeSyncFinished
import com.ruslan.hlushan.game.play.ui.screens.GameScopeMarkerRepository
import com.ruslan.hlushan.game.play.ui.screens.game.continue_game.ContinueGameScreen
import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.Single

private const val REQUEST_ITEMS_LIMIT = 20

@SuppressWarnings("LongParameterList")
internal class GameRecordsListViewModel
@AssistedInject
constructor(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        schedulersManager: SchedulersManager,
        @Assisted private val router: Router,
        private val gameSettings: GameSettings,
        private val gameScopeMarkerRepository: GameScopeMarkerRepository,
        private val startSyncUseCase: StartSyncUseCase,
        private val authInteractor: AuthInteractor,
        private val playRecordsInteractor: PlayRecordsInteractor
) : PaginationViewModel<GameRecordWithSyncState.Order.Params, Long, GameRecordRecyclerItem, RequestParams>(
        appLogger = appLogger,
        threadChecker = threadChecker,
        initFilter = gameSettings.orderParams,
        schedulersManager = schedulersManager
) {

    @UiMainThread
    var orderParams: GameRecordWithSyncState.Order.Params
        get() = gameSettings.orderParams
        set(newValue) {
            if (gameSettings.orderParams != newValue) {
                gameSettings.orderParams = newValue
                updateFilter(gameSettings.orderParams)
            }
        }

    @UiMainThread
    private val mutableCommandsQueue = MutableCommandQueue<Command>()

    val commandsQueue: CommandQueue<Command> get() = mutableCommandsQueue

    init {
        refresh()
        observeEnableSync()
        observeSyncFinished()
    }

    @UiMainThread
    override fun loadData(
            pagesRequest: PaginationPagesRequest<RequestParams>,
            filter: GameRecordWithSyncState.Order.Params
    ): Single<PaginationResponse<GameRecordRecyclerItem, RequestParams>> =
            playRecordsInteractor.getAvailableRecords(pagesRequest, filter, limit = REQUEST_ITEMS_LIMIT)
                    .map { response -> response.map(::GameRecordRecyclerItem) }

    @UiMainThread
    override fun onStateUpdated() =
            mutableCommandsQueue.add(Command.SetState(
                    gameRecords = state.itemsOrEmpty(),
                    additional = (state as? PaginationState.Active)?.additional
            ))

    @UiMainThread
    override fun onAfterAttachView() {
        super.onAfterAttachView()

        if (gameScopeMarkerRepository.wasListChanged) {
            refresh()
            gameScopeMarkerRepository.wasListChanged = false
        }
    }

    @UiMainThread
    fun observeUpdates(recyclerItem: GameRecordRecyclerItem): Observable<GameRecordRecyclerItem> =
            playRecordsInteractor.observeGameRecord(recyclerItem.gameRecord.record.id)
                    .observeOn(schedulersManager.ui)
                    .flatMap { gameHolder ->
                        val game = gameHolder.value
                        if (game != null) {
                            Observable.just(GameRecordRecyclerItem(game))
                                    .doOnNext { updatedItem ->
                                        onSingleItemUpdated(updatedItem = updatedItem, notifyStateUpdated = false)
                                    }
                        } else {
                            onSingleItemDeleted(deletedItemId = recyclerItem.gameRecord.record.id)
                            Observable.empty()
                        }
                    }

    @UiMainThread
    fun removeRecord(gameRecord: GameRecord) {
        playRecordsInteractor.removeRecordById(gameRecord.id)
                .observeOn(schedulersManager.ui)
                .doOnSubscribe { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = true)) }
                .doOnComplete { onSingleItemDeleted(deletedItemId = gameRecord.id) }
                .doFinally { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = false)) }
                .subscribe(
                        { appLogger.log(this, "removeRecord success") },
                        { error -> mutableCommandsQueue.add(Command.ShowError(error)) }
                )
                .joinUntilDestroy()
    }

    @UiMainThread
    fun selectRecord(gameRecordWithSyncState: GameRecordWithSyncState) {
        playRecordsInteractor.updateAndGetRecordForPlaying(gameRecordWithSyncState.record.id)
                .observeOn(schedulersManager.ui)
                .doOnSubscribe { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = true)) }
                .doOnSuccess { recordForPlaying -> router.navigateTo(ContinueGameScreen(recordForPlaying)) }
                .doFinally { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = false)) }
                .subscribe(
                        { appLogger.log(this, "selectRecord success") },
                        { error -> mutableCommandsQueue.add(Command.ShowError(error)) }
                )
                .joinUntilDestroy()
    }

    @UiMainThread
    fun startSync() = startSyncUseCase.start()

    @UiMainThread
    private fun observeEnableSync() {
        Observable.combineLatest<Boolean, Boolean, Boolean>(
                authInteractor.observeUserIsAuthenticated(),
                startSyncUseCase.observeIsSynchronizing(),
                { userIsAuthenticated, isSynchronizing -> (userIsAuthenticated && !isSynchronizing) }
        )
                .observeOn(schedulersManager.ui)
                .doOnNext { enable -> mutableCommandsQueue.add(Command.EnableSyncButton(enable)) }
                .subscribe(
                        { appLogger.log(this, "observeCurrentUser success: $it") },
                        { error -> mutableCommandsQueue.add(Command.ShowError(error)) }
                )
                .joinUntilDestroy()
    }

    @UiMainThread
    private fun observeSyncFinished() {
        startSyncUseCase.observeSyncFinished()
                .observeOn(schedulersManager.ui)
                .subscribe(
                        { unit -> refresh() },
                        { error -> mutableCommandsQueue.add(Command.ShowError(error)) }
                )
                .joinUntilDestroy()
    }

    sealed class Command : StrategyCommand {

        class ShowSimpleProgress(val show: Boolean) : Command() {
            override fun produceStrategy(): HandleStrategy = AddToEndSingleStrategy()
        }

        class SetState(
                val gameRecords: List<GameRecordRecyclerItem>,
                val additional: PaginationState.Additional?
        ) : Command() {
            override fun produceStrategy(): HandleStrategy = AddToEndSingleStrategy()
        }

        class EnableSyncButton(val enable: Boolean) : Command() {
            override fun produceStrategy(): HandleStrategy = AddToEndSingleStrategy()
        }

        class ShowError(val error: Throwable) : Command() {
            override fun produceStrategy(): HandleStrategy = OneExecutionStateStrategy()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(router: Router): GameRecordsListViewModel
    }
}