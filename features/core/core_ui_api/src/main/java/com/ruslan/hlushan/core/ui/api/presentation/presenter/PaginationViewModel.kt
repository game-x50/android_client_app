package com.ruslan.hlushan.core.ui.api.presentation.presenter

import com.ruslan.hlushan.core.api.dto.PaginationResponse
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem
import com.ruslan.hlushan.extensions.exhaustive
import com.ruslan.hlushan.extensions.withReplacedFirst
import com.ruslan.hlushan.extensions.withoutFirst
import com.ruslan.hlushan.rxjava2.extensions.safetyDispose
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/*
 * https://www.youtube.com/watch?v=g7wwybnXE40
 */

private const val DEFAULT_ITEMS_LIMIT_PER_PAGE: Int = 20
private const val FILTER_UPDATE_DEBOUNCE_MILLIS: Long = 300

//TODO: #write_unit_tests
abstract class PaginationViewModel<F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any>(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        initFilter: F,
        protected val schedulersManager: SchedulersManager
) : BaseViewModel(appLogger, threadChecker) {

    private val filterSubject = PublishSubject.create<F>()

    @UiMainThread
    private var requestDisposable: Disposable? = null

    protected open val itemsLimitPerPage: Int get() = DEFAULT_ITEMS_LIMIT_PER_PAGE

    @UiMainThread
    protected open val totalItemCount: Int
        get() = state.items.size

    @UiMainThread
    protected var state: PaginationState<F, ItemId, RI, PageId> = PaginationState.Empty(filter = initFilter)
        /** use [setNewState] as setter*/
        private set

    init {
        filterSubject
                .debounce(FILTER_UPDATE_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(schedulersManager.ui)
                .doOnNext { filter -> handleFilterUpdated(filter) }
                .subscribe(
                        { action -> this.appLogger.log(this@PaginationViewModel, "action = $action") },
                        { error -> this.appLogger.log(this@PaginationViewModel, "filterSubject", error) }
                )
                .joinUntilDestroy()
    }

    @UiMainThread
    protected abstract fun loadData(nextId: PageId?, filter: F): Single<PaginationResponse<RI, PageId>>

    @UiMainThread
    protected abstract fun onStateUpdated()

    @UiMainThread
    fun retry() = loadMoreAction()

    @UiMainThread
    fun refresh() = handleAction(action = Action.UI.Refresh(state.filter))

    @UiMainThread
    fun onScrolled(lastVisibleItemPosition: Int) {
        @SuppressWarnings("MagicNumber")
        if ((lastVisibleItemPosition + (itemsLimitPerPage / 3) > totalItemCount)) {
            loadMoreAction()
        }
    }

    @UiMainThread
    protected fun updateFilter(newFilter: F) = filterSubject.onNext(newFilter)

    @UiMainThread
    protected fun onSingleItemUpdated(updatedItem: RI, notifyStateUpdated: Boolean) =
            handleAction(action = Action.Change.SingleItemUpdated(updatedItem, notifyStateUpdated))

    @UiMainThread
    protected fun onSingleItemDeleted(deletedItemId: ItemId) =
            handleAction(action = Action.Change.SingleItemDeleted(deletedItemId))

    @UiMainThread
    private fun handleFilterUpdated(newFilter: F) = handleAction(action = Action.UI.Refresh(newFilter))

    @UiMainThread
    private fun loadMoreAction() = handleAction(action = Action.UI.LoadMore())

    @UiMainThread
    private fun handleAction(action: Action<F, ItemId, RI, PageId>) {
        val result = reduceState(
                state = state,
                action = action
        )

        val notifyStateUpdated = !result.sideEffects.any { sideEffect -> (sideEffect is SideEffect.AvoidNotifyStateUpdated) }

        setNewState(newState = result.newState, notifyStateUpdated = notifyStateUpdated)

        result.sideEffects.forEach { sideEffect ->
            when (sideEffect) {
                is SideEffect.LoadNext -> {
                    loadMore(result.newState.nextId, result.newState.filter)
                }
                is SideEffect.AvoidNotifyStateUpdated -> {
                    Unit //NOP
                }
            }.exhaustive
        }
    }

    @UiMainThread
    private fun loadMore(nextId: PageId?, filter: F) {
        requestDisposable?.safetyDispose()

        requestDisposable = loadData(nextId, filter)
                .map<Action<F, ItemId, RI, PageId>> { response -> Action.Response.Success(response) }
                .onErrorReturn { error -> Action.Response.Error(error) }
                .observeOn(schedulersManager.ui)
                .doOnSuccess { action -> handleAction(action) }
                .subscribe(
                        { action -> appLogger.log(this@PaginationViewModel, "request action = $action") },
                        { error -> appLogger.log(this@PaginationViewModel, "request", error) }
                )

        requestDisposable?.joinUntilDestroy()
    }

    @UiMainThread
    private fun setNewState(newState: PaginationState<F, ItemId, RI, PageId>, notifyStateUpdated: Boolean) {
        if (state != newState) {
            state = newState

            if (notifyStateUpdated) {
                onStateUpdated()
            }
        }
    }
}

private sealed class Action<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> {

    sealed class UI<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> : Action<F, ItemId, RI, PageId>() {

        class LoadMore<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> : UI<F, ItemId, RI, PageId>()

        data class Refresh<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
                val filter: F
        ) : UI<F, ItemId, RI, PageId>()
    }

    sealed class Response<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> : Action<F, ItemId, RI, PageId>() {

        class Success<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
                val response: PaginationResponse<RI, PageId>
        ) : Response<F, ItemId, RI, PageId>()

        class Error<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
                val error: Throwable
        ) : Response<F, ItemId, RI, PageId>()
    }

    sealed class Change<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> : Action<F, ItemId, RI, PageId>() {

        class SingleItemUpdated<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
                val updatedItem: RI,
                val notifyStateUpdated: Boolean
        ) : Change<F, ItemId, RI, PageId>()

        class SingleItemDeleted<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
                val deletedItemId: ItemId
        ) : Change<F, ItemId, RI, PageId>()
    }
}

private sealed class SideEffect {
    class LoadNext : SideEffect()
    class AvoidNotifyStateUpdated : SideEffect()
}

private class ReduceResult<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
        val newState: PaginationState<F, ItemId, RI, PageId>,
        vararg val sideEffects: SideEffect
)

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceState(
        state: PaginationState<F, ItemId, RI, PageId>,
        action: Action<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        when (state) {
            is PaginationState.Empty -> reduceStateEmpty(state, action)
            is PaginationState.EmptyLoading -> reduceStateEmptyLoading(state, action)
            is PaginationState.EmptyWithError -> reduceStateEmptyWithError(state, action)
            is PaginationState.PartiallyLoaded -> reduceStatePartiallyLoaded(state, action)
            is PaginationState.PartiallyLoadedAndLoading -> reduceStatePartiallyLoadedAndLoading(state, action)
            is PaginationState.PartiallyLoadedWithError -> reduceStatePartiallyLoadedWithError(state, action)
            is PaginationState.AllLoaded -> reduceStateAllLoaded(state, action)
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceStateEmpty(
        state: PaginationState.Empty<F, ItemId, RI, PageId>,
        action: Action<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.UI.Refresh -> {
                ReduceResult(PaginationState.EmptyLoading(filter = action.filter), SideEffect.LoadNext())
            }
            is Action.UI.LoadMore -> {
                ReduceResult(PaginationState.EmptyLoading(filter = state.filter), SideEffect.LoadNext())
            }
            is Action.Response.Success,
            is Action.Response.Error,
            is Action.Change.SingleItemUpdated,
            is Action.Change.SingleItemDeleted -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceStateEmptyLoading(
        state: PaginationState.EmptyLoading<F, ItemId, RI, PageId>,
        action: Action<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.UI.Refresh -> {
                ReduceResult(PaginationState.EmptyLoading(action.filter), SideEffect.LoadNext())
            }
            is Action.Response.Success -> {
                @Suppress("MaxLineLength")
                when (action.response) {
                    is PaginationResponse.MiddlePage -> ReduceResult(PaginationState.PartiallyLoaded(action.response.result, filter = state.filter, nextId = action.response.nextId))
                    is PaginationResponse.LastPage   -> ReduceResult(PaginationState.AllLoaded(action.response.result, filter = state.filter))
                }
            }
            is Action.Response.Error -> {
                ReduceResult(PaginationState.EmptyWithError(filter = state.filter, error = action.error))
            }
            is Action.UI.LoadMore,
            is Action.Change.SingleItemUpdated,
            is Action.Change.SingleItemDeleted -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceStateEmptyWithError(
        state: PaginationState.EmptyWithError<F, ItemId, RI, PageId>,
        action: Action<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.UI.Refresh -> {
                ReduceResult(PaginationState.EmptyLoading(filter = action.filter), SideEffect.LoadNext())
            }
            is Action.UI.LoadMore -> {
                ReduceResult(PaginationState.EmptyLoading(filter = state.filter), SideEffect.LoadNext())
            }
            is Action.Response.Success,
            is Action.Response.Error,
            is Action.Change.SingleItemUpdated,
            is Action.Change.SingleItemDeleted -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceStatePartiallyLoaded(
        state: PaginationState.PartiallyLoaded<F, ItemId, RI, PageId>,
        action: Action<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.UI.Refresh -> {
                ReduceResult(PaginationState.EmptyLoading(filter = action.filter), SideEffect.LoadNext())
            }
            is Action.UI.LoadMore -> {
                @Suppress("MaxLineLength")
                ReduceResult(PaginationState.PartiallyLoadedAndLoading(state.items, filter = state.filter, nextId = state.nextId), SideEffect.LoadNext())
            }
            is Action.Change -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        copyStateWithNewItems = { updatedItems -> state.copy(items = updatedItems) },
                        action = action
                )
            }
            is Action.Response.Success,
            is Action.Response.Error -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceStatePartiallyLoadedAndLoading(
        state: PaginationState.PartiallyLoadedAndLoading<F, ItemId, RI, PageId>,
        action: Action<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.UI.Refresh -> {
                ReduceResult(PaginationState.EmptyLoading(filter = action.filter), SideEffect.LoadNext())
            }
            is Action.UI.LoadMore -> {
                ReduceResult(state)
            }
            is Action.Response.Success -> {
                @Suppress("MaxLineLength")
                when (action.response) {
                    is PaginationResponse.MiddlePage -> ReduceResult(PaginationState.PartiallyLoaded((state.items + action.response.result), filter = state.filter, nextId = action.response.nextId))
                    is PaginationResponse.LastPage   -> ReduceResult(PaginationState.AllLoaded((state.items + action.response.result), filter = state.filter))
                }
            }
            is Action.Response.Error -> {
                @Suppress("MaxLineLength")
                ReduceResult(PaginationState.PartiallyLoadedWithError(state.items, filter = state.filter, nextId = state.nextId, error = action.error))
            }
            is Action.Change -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        copyStateWithNewItems = { updatedItems -> state.copy(items = updatedItems) },
                        action = action
                )
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceStatePartiallyLoadedWithError(
        state: PaginationState.PartiallyLoadedWithError<F, ItemId, RI, PageId>,
        action: Action<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.UI.Refresh -> {
                ReduceResult(PaginationState.EmptyLoading(filter = action.filter), SideEffect.LoadNext())
            }
            is Action.UI.LoadMore -> {
                @Suppress("MaxLineLength")
                ReduceResult(PaginationState.PartiallyLoadedAndLoading(state.items, filter = state.filter, nextId = state.nextId), SideEffect.LoadNext())
            }
            is Action.Change -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        copyStateWithNewItems = { updatedItems -> state.copy(items = updatedItems) },
                        action = action
                )
            }
            is Action.Response.Success,
            is Action.Response.Error -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceStateAllLoaded(
        state: PaginationState.AllLoaded<F, ItemId, RI, PageId>,
        action: Action<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.UI.Refresh -> {
                ReduceResult(PaginationState.EmptyLoading(action.filter), SideEffect.LoadNext())
            }
            is Action.Change -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        copyStateWithNewItems = { updatedItems -> state.copy(items = updatedItems) },
                        action = action
                )
            }
            is Action.UI.LoadMore,
            is Action.Response.Success,
            is Action.Response.Error -> {
                ReduceResult(state)
            }
        }

private inline fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceActionChangeForNonEmptyList(
        state: PaginationState<F, ItemId, RI, PageId>,
        copyStateWithNewItems: (List<RI>) -> PaginationState<F, ItemId, RI, PageId>,
        action: Action.Change<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.Change.SingleItemUpdated -> {
                val updatedItems: List<RI> = state.items.withReplacedFirst(action.updatedItem) { item -> item.id == action.updatedItem.id }
                val sideEffects = if (action.notifyStateUpdated) {
                    emptyArray()
                } else {
                    arrayOf(SideEffect.AvoidNotifyStateUpdated())
                }
                @Suppress("SpreadOperator")
                ReduceResult(copyStateWithNewItems(updatedItems), *sideEffects)
            }
            is Action.Change.SingleItemDeleted -> {
                val updatedItems: List<RI> = state.items.withoutFirst { item -> item.id == action.deletedItemId }
                ReduceResult(copyStateWithNewItems(updatedItems))
            }
        }