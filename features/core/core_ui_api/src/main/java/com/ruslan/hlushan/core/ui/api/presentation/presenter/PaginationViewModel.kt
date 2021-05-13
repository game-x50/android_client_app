package com.ruslan.hlushan.core.ui.api.presentation.presenter

import com.ruslan.hlushan.core.api.dto.PaginationResponse
import com.ruslan.hlushan.core.api.dto.isFirstPage
import com.ruslan.hlushan.core.api.dto.isLastPage
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

private const val FILTER_UPDATE_DEBOUNCE_MILLIS: Long = 300

//TODO: #write_unit_tests
//TODO: separate by files
abstract class PaginationViewModel<F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any>(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        initFilter: F,
        private val limits: Limits,
        protected val schedulersManager: SchedulersManager
) : BaseViewModel(appLogger, threadChecker) {

    private val filterSubject = PublishSubject.create<F>()

    @UiMainThread
    private var requestDisposable: Disposable? = null

    //for overriding in case of not real items that should not be counted(headers,...)
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
    protected abstract fun loadData(
            direction: PaginationState.LoadDirection,
            pageId: PageId?,
            filter: F
    ): Single<PaginationResponse<RI, PageId>>

    @UiMainThread
    protected abstract fun onStateUpdated()

    @UiMainThread
    fun retryIfError() {
        val additional = state.additional
        if (additional is PaginationState.Additional.Error) {
            loadMoreAction(direction = additional.direction)
        }
    }

    @UiMainThread
    fun refresh() = handleAction(action = Action.UI.Refresh(state.filter))

    @UiMainThread
    fun onScrolled(lastVisibleItemPosition: Int) {
        if ((totalItemCount - limits.itemsOffsetToBorder) <= lastVisibleItemPosition) {
            loadMoreAction(direction = PaginationState.LoadDirection.NEXT)
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
    private fun handleFilterUpdated(newFilter: F) =
            handleAction(action = Action.UI.Refresh(newFilter))

    @UiMainThread
    private fun loadMoreAction(direction: PaginationState.LoadDirection) =
            handleAction(action = Action.UI.LoadMore(direction = direction))

    @UiMainThread
    private fun handleAction(action: Action<F, ItemId, RI, PageId>) {
        val result = reduceState(
                state = state,
                action = action,
                limits = limits
        )

        val notifyStateUpdated = !result.sideEffects.any { sideEffect -> (sideEffect is SideEffect.AvoidNotifyStateUpdated) }

        setNewState(newState = result.newState, notifyStateUpdated = notifyStateUpdated)

        result.sideEffects.forEach { sideEffect ->
            when (sideEffect) {
                is SideEffect.LoadMore -> {
                    loadMore(params = sideEffect, filter = result.newState.filter)
                }
                is SideEffect.AvoidNotifyStateUpdated -> {
                    Unit //NOP
                }
            }.exhaustive
        }
    }

    @UiMainThread
    private fun loadMore(params: SideEffect.LoadMore<PageId>, filter: F) {
        requestDisposable?.safetyDispose()

        requestDisposable = loadData(direction = params.direction, pageId = params.pageId, filter = filter)
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

    class Limits(
            val itemsOffsetToBorder: Int = 20,
            val maxStoredItemsCount: Int = 25
    )
}

private sealed class Action<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> {

    sealed class UI<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> : Action<F, ItemId, RI, PageId>() {

        data class LoadMore<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
                val direction: PaginationState.LoadDirection
        ) : UI<F, ItemId, RI, PageId>()

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

private sealed class SideEffect<out PageId : Any> {

    class LoadMore<PageId : Any>(
            val direction: PaginationState.LoadDirection,
            val pageId: PageId?
    ) : SideEffect<PageId>()

    class AvoidNotifyStateUpdated<PageId : Any> : SideEffect<PageId>()
}

private class ReduceResult<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
        val newState: PaginationState<F, ItemId, RI, PageId>,
        vararg val sideEffects: SideEffect<PageId>
)

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceState(
        state: PaginationState<F, ItemId, RI, PageId>,
        action: Action<F, ItemId, RI, PageId>,
        limits: PaginationViewModel.Limits
): ReduceResult<F, ItemId, RI, PageId> =
        when (state) {
            is PaginationState.Empty                     -> reduceStateEmpty(state, action)
            is PaginationState.EmptyLoading              -> reduceStateEmptyLoading(state, action, limits)
            is PaginationState.EmptyWithError            -> reduceStateEmptyWithError(state, action)
            is PaginationState.PartiallyLoaded           -> reduceStatePartiallyLoaded(state, action)
            is PaginationState.PartiallyLoadedAndLoading -> reduceStatePartiallyLoadedAndLoading(state, action, limits)
            is PaginationState.PartiallyLoadedWithError  -> reduceStatePartiallyLoadedWithError(state, action)
            is PaginationState.AllLoaded                 -> reduceStateAllLoaded(state, action)
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceStateEmpty(
        state: PaginationState.Empty<F, ItemId, RI, PageId>,
        action: Action<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.UI.Refresh -> {
                reduceActionRefresh(action)
            }
            is Action.UI.LoadMore -> {
                reduceActionLoadMoreForEmptyOrWithError(action = action, state = state)
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
        action: Action<F, ItemId, RI, PageId>,
        limits: PaginationViewModel.Limits
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.UI.Refresh -> {
                reduceActionRefresh(action)
            }
            is Action.Response.Success -> {
                reduceActionResponseSuccessForLoading(
                        action = action,
                        state = state,
                        direction = PaginationState.LoadDirection.NEXT,
                        limits = limits
                )
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
                reduceActionRefresh(action)
            }
            is Action.UI.LoadMore -> {
                reduceActionLoadMoreForEmptyOrWithError(action = action, state = state)
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
                reduceActionRefresh(action)
            }
            is Action.UI.LoadMore -> {
                reduceActionLoadMoreForPartiallyLoadedOrWithError(action = action, state = state)
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
        action: Action<F, ItemId, RI, PageId>,
        limits: PaginationViewModel.Limits
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.UI.Refresh -> {
                reduceActionRefresh(action)
            }
            is Action.UI.LoadMore -> {
                ReduceResult(state)
            }
            is Action.Response.Success -> {
                reduceActionResponseSuccessForLoading(
                        action = action,
                        state = state,
                        direction = state.direction,
                        limits = limits
                )
            }
            is Action.Response.Error -> {
                ReduceResult(
                        PaginationState.PartiallyLoadedWithError(
                                items = state.items,
                                filter = state.filter,
                                previousId = state.previousId,
                                nextId = state.nextId,
                                error = action.error,
                                direction = state.direction
                        )
                )
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
                reduceActionRefresh(action)
            }
            is Action.UI.LoadMore -> {
                reduceActionLoadMoreForPartiallyLoadedOrWithError(action = action, state = state)
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
                reduceActionRefresh(action)
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

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceActionRefresh(
        action: Action.UI.Refresh<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        ReduceResult(
                PaginationState.EmptyLoading(filter = action.filter),
                SideEffect.LoadMore(direction = PaginationState.LoadDirection.NEXT, pageId = null)
        )

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceActionLoadMoreForEmptyOrWithError(
        action: Action.UI.LoadMore<F, ItemId, RI, PageId>,
        state: PaginationState<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        ReduceResult(
                PaginationState.EmptyLoading(filter = state.filter),
                createLoadMoreSideEffect(action = action, state = state)
        )

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceActionResponseSuccessForLoading(
        action: Action.Response.Success<F, ItemId, RI, PageId>,
        state: PaginationState<F, ItemId, RI, PageId>,
        direction: PaginationState.LoadDirection,
        limits: PaginationViewModel.Limits
): ReduceResult<F, ItemId, RI, PageId> {
    val updatedItems = when(direction) {
        PaginationState.LoadDirection.PREVIOUS -> (action.response.result + state.items)
        PaginationState.LoadDirection.NEXT     -> (state.items + action.response.result)
    }
    val newState = if (updatedItems.size <= limits.maxStoredItemsCount) {
        when(direction) {
            PaginationState.LoadDirection.PREVIOUS ->
            PaginationState.LoadDirection.NEXT     ->
        }
    } else {

    }
    return ReduceResult(newState)
    @Suppress("MaxLineLength")
    when (action.response) {
        is PaginationResponse.MiddlePage -> ReduceResult(PaginationState.PartiallyLoaded((state.items + action.response.result), filter = state.filter, nextId = action.response.nextId))
        is PaginationResponse.LastPage   -> ReduceResult(PaginationState.AllLoaded((state.items + action.response.result), filter = state.filter))
    }
}

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceActionLoadMoreForPartiallyLoadedOrWithError(
        action: Action.UI.LoadMore<F, ItemId, RI, PageId>,
        state: PaginationState<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        ReduceResult(
                PaginationState.PartiallyLoadedAndLoading(
                        items = state.items,
                        filter = state.filter,
                        previousId = state.previousId,
                        nextId = state.nextId,
                        direction = action.direction
                ),
                createLoadMoreSideEffect(action = action, state = state)
        )

private fun <F : Any, ItemId : Any, PageId : Any, RI : RecyclerItem<ItemId>> createLoadMoreSideEffect(
        action: Action.UI.LoadMore<F, ItemId, RI, PageId>,
        state: PaginationState<F, ItemId, RI, PageId>
): SideEffect.LoadMore<PageId> =
        SideEffect.LoadMore(
                direction = action.direction,
                pageId = state.loadPageIdFor(action.direction)
        )

private inline fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceActionChangeForNonEmptyList(
        state: PaginationState<F, ItemId, RI, PageId>,
        copyStateWithNewItems: (List<RI>) -> PaginationState<F, ItemId, RI, PageId>,
        action: Action.Change<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.Change.SingleItemUpdated -> {
                val updatedItems: List<RI> = state.items.withReplacedFirst(action.updatedItem) { item -> (item.id == action.updatedItem.id) }
                val sideEffects = if (action.notifyStateUpdated) {
                    emptyArray()
                } else {
                    arrayOf(SideEffect.AvoidNotifyStateUpdated<PageId>())
                }
                @Suppress("SpreadOperator")
                ReduceResult(copyStateWithNewItems(updatedItems), *sideEffects)
            }
            is Action.Change.SingleItemDeleted -> {
                val updatedItems: List<RI> = state.items.withoutFirst { item -> (item.id == action.deletedItemId) }
                ReduceResult(copyStateWithNewItems(updatedItems))
            }
        }