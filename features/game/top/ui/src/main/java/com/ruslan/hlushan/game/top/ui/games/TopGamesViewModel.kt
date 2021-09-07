package com.ruslan.hlushan.game.top.ui.games

import com.ruslan.hlushan.core.api.dto.pagination.PaginationPagesRequest
import com.ruslan.hlushan.core.api.dto.pagination.PaginationResponse
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.command.CommandQueue
import com.ruslan.hlushan.core.ui.api.presentation.command.MutableCommandQueue
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.AddToEndSingleStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.HandleStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.StrategyCommand
import com.ruslan.hlushan.core.ui.api.presentation.viewmodel.PaginationViewModel
import com.ruslan.hlushan.core.ui.api.presentation.viewmodel.pagination.PaginationState
import com.ruslan.hlushan.core.ui.api.presentation.viewmodel.pagination.itemsOrEmpty
import com.ruslan.hlushan.game.api.auth.AuthInteractor
import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.api.top.TopInteractor
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.Single

internal class TopGamesViewModel
@AssistedInject
constructor(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        schedulersManager: SchedulersManager,
        private val topInteractor: TopInteractor,
        private val authInteractor: AuthInteractor
) : PaginationViewModel<GameSize, String, TopGameRecyclerItem, String>(
        appLogger = appLogger,
        threadChecker = threadChecker,
        initFilter = GameSize.MEDIUM,
        schedulersManager = schedulersManager
) {

    @UiMainThread
    private val mutableCommandsQueue = MutableCommandQueue<Command>()

    val commandsQueue: CommandQueue<Command> get() = mutableCommandsQueue

    init {
        refresh()
    }

    @UiMainThread
    override fun loadData(
            pagesRequest: PaginationPagesRequest<String>,
            filter: GameSize
    ): Single<PaginationResponse<TopGameRecyclerItem, String>> =
            topInteractor.getTopGamesFor(size = filter)
                    .map<PaginationResponse<TopGameRecyclerItem, String>> { games ->
                        val currentUser = authInteractor.getUser()
                        val recyclerItems = games.map { previewWithUserDetails ->
                            TopGameRecyclerItem(
                                    isMyOwn = (currentUser?.nickname == previewWithUserDetails.userNickname),
                                    previewWithUserDetails = previewWithUserDetails
                            )
                        }
                        PaginationResponse.SinglePage(recyclerItems)
                    }

    @UiMainThread
    override fun onStateUpdated() =
            mutableCommandsQueue.add(Command.SetState(
                    items = state.itemsOrEmpty(),
                    additional = (state as? PaginationState.Active)?.additional)
            )

    sealed class Command : StrategyCommand {

        class SetState(
                val items: List<TopGameRecyclerItem>,
                val additional: PaginationState.Additional?
        ) : Command() {
            override fun produceStrategy(): HandleStrategy = AddToEndSingleStrategy()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(): TopGamesViewModel
    }
}