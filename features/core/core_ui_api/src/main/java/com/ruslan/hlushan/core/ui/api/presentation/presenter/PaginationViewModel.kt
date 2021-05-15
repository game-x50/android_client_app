package com.ruslan.hlushan.core.ui.api.presentation.presenter

import com.ruslan.hlushan.core.api.dto.PaginationPagesRequest
import com.ruslan.hlushan.core.api.dto.PaginationResponse
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.Action
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.PageRelation
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.PaginationLimits
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.PaginationSideEffect
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.PaginationState
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.hasSenseForLoading
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem
import com.ruslan.hlushan.extensions.addAsFirstTo
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
        private val limits: PaginationLimits = PaginationLimits(),
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
            paginationPagesRequest: PaginationPagesRequest<PageId>,
            filter: F
    ): Single<PaginationResponse<RI, PageId>>

    @UiMainThread
    protected abstract fun onStateUpdated()

    @UiMainThread
    fun retryIfError() {
        val additional = state.additional
        if (additional is PaginationState.Additional.Error) {
            loadMoreAction(direction = additional.loadDirection)
        }
    }

    @UiMainThread
    fun refresh() = handleAction(action = Action.UI.Refresh(state.filter))

    @UiMainThread
    fun onScrolled(lastVisibleItemPosition: Int) {
        if ((totalItemCount - limits.itemsOffsetToBorder) <= lastVisibleItemPosition) {
            loadMoreAction(direction = PaginationPagesRequest.Direction.NEXT)
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
    private fun loadMoreAction(direction: PaginationPagesRequest.Direction) =
            handleAction(action = Action.UI.LoadMore(direction = direction))

    @UiMainThread
    private fun handleAction(action: Action<F, ItemId, RI, PageId>) {
        val result = reduceState(
                state = state,
                action = action,
                limits = limits
        )

        val notifyStateUpdated = !result.sideEffects.any { sideEffect -> (sideEffect is PaginationSideEffect.AvoidNotifyStateUpdated) }

        setNewState(newState = result.newState, notifyStateUpdated = notifyStateUpdated)

        result.sideEffects.forEach { sideEffect ->
            when (sideEffect) {
                is PaginationSideEffect.LoadMore                -> {
                    loadMore(params = sideEffect, filter = result.newState.filter)
                }
                is PaginationSideEffect.AvoidNotifyStateUpdated -> {
                    Unit //NOP
                }
            }.exhaustive
        }
    }

    @UiMainThread
    private fun loadMore(params: PaginationSideEffect.LoadMore<PageId>, filter: F) {
        requestDisposable?.safetyDispose()

        requestDisposable = loadData(paginationPagesRequest = params.paginationPagesRequest, filter = filter)
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

internal class ReduceResult<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
        val newState: PaginationState<F, ItemId, RI, PageId>,
        vararg val sideEffects: PaginationSideEffect<PageId>
)

internal fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceState(
        state: PaginationState<F, ItemId, RI, PageId>,
        action: Action<F, ItemId, RI, PageId>,
        limits: PaginationLimits
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
            is Action.UI.Refresh               -> {
                reduceActionRefresh(action)
            }
            is Action.UI.LoadMore              -> {
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
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.UI.Refresh               -> {
                reduceActionRefresh(action)
            }
            is Action.Response.Success         -> {
                reduceActionResponseSuccessForEmptyLoading(
                        action = action,
                        state = state,
                        limits = limits
                )
            }
            is Action.Response.Error           -> {
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
            is Action.UI.Refresh               -> {
                reduceActionRefresh(action)
            }
            is Action.UI.LoadMore              -> {
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
            is Action.UI.Refresh     -> {
                reduceActionRefresh(action)
            }
            is Action.UI.LoadMore    -> {
                reduceActionLoadMoreForPartiallyLoadedOrWithError(action = action, state = state)
            }
            is Action.Change         -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        copyStateWithNewItems = { updatedItems, updatedCurrentPages ->
                            state.copy(items = updatedItems, currentPages = updatedCurrentPages)
                        },
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
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.UI.Refresh       -> {
                reduceActionRefresh(action)
            }
            is Action.UI.LoadMore      -> {
                ReduceResult(state)
            }
            is Action.Response.Success -> {
                reduceActionResponseSuccessForPartiallyLoadedAndLoading(
                        action = action,
                        state = state,
                        limits = limits
                )
            }
            is Action.Response.Error   -> {
                ReduceResult(
                        PaginationState.PartiallyLoadedWithError(
                                items = state.items,
                                filter = state.filter,
                                currentPages = state.currentPages,
                                previousId = state.previousId,
                                nextId = state.nextId,
                                direction = state.direction,
                                error = action.error
                        )
                )
            }
            is Action.Change           -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        copyStateWithNewItems = { updatedItems, updatedCurrentPages ->
                            state.copy(items = updatedItems, currentPages = updatedCurrentPages)
                        },
                        action = action
                )
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceStatePartiallyLoadedWithError(
        state: PaginationState.PartiallyLoadedWithError<F, ItemId, RI, PageId>,
        action: Action<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.UI.Refresh     -> {
                reduceActionRefresh(action)
            }
            is Action.UI.LoadMore    -> {
                reduceActionLoadMoreForPartiallyLoadedOrWithError(action = action, state = state)
            }
            is Action.Change         -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        copyStateWithNewItems = { updatedItems, updatedCurrentPages ->
                            state.copy(items = updatedItems, currentPages = updatedCurrentPages)
                        },
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
            is Action.UI.Refresh     -> {
                reduceActionRefresh(action)
            }
            is Action.Change         -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        copyStateWithNewItems = { updatedItems, updatedCurrentPages ->
                            state.copy(items = updatedItems, currentPages = updatedCurrentPages)
                        },
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
): ReduceResult<F, ItemId, RI, PageId> {
    val newState = PaginationState.EmptyLoading<F, ItemId, RI, PageId>(filter = action.filter)
    return ReduceResult(
            newState,
            PaginationSideEffect.LoadMore(
                    paginationPagesRequest = PaginationPagesRequest.Next(
                            lastLoadedPageId = null,
                            nextPageId = newState.nextId
                    )
            )
    )
}

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceActionLoadMoreForEmptyOrWithError(
        action: Action.UI.LoadMore<F, ItemId, RI, PageId>,
        state: PaginationState<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        ReduceResult(
                PaginationState.EmptyLoading(filter = state.filter),
                createLoadMoreSideEffect(action = action, state = state)
        )

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceActionResponseSuccessForEmptyLoading(
        action: Action.Response.Success<F, ItemId, RI, PageId>,
        state: PaginationState.EmptyLoading<F, ItemId, RI, PageId>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, PageId> {
    val limitedItems = action.response.result.take(limits.maxStoredItemsCount)
    val currentPages = listOf(PageRelation(
            pageId = null,
            itemsIds = limitedItems.map { singleItem -> singleItem.id }
    ))
    val newState = when (action.response) {
        is PaginationResponse.LastPage   -> {
            PaginationState.AllLoaded(
                    items = limitedItems,
                    filter = state.filter,
                    currentPages = currentPages
            )
        }
        is PaginationResponse.MiddlePage -> {
            PaginationState.PartiallyLoaded.withItemsAfter(
                    items = limitedItems,
                    filter = state.filter,
                    currentPages = currentPages,
                    nextId = action.response.nextId
            )
        }
    }
    return ReduceResult(newState)
}

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceActionResponseSuccessForPartiallyLoadedAndLoading(
        action: Action.Response.Success<F, ItemId, RI, PageId>,
        state: PaginationState.PartiallyLoadedAndLoading<F, ItemId, RI, PageId>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, PageId> {
    val itemsExceedsLimit = ((action.response.result.size + state.items.size) > limits.maxStoredItemsCount)
    val newState = if (itemsExceedsLimit) {
        when(state.direction) {
            PaginationPagesRequest.Direction.PREVIOUS -> {
                reduceActionResponseSuccessForPartiallyLoadedAndLoadingLimitExceedDirectionPrevious(state = state, action = action, limits = limits)
            }
            PaginationPagesRequest.Direction.NEXT     -> {
                reduceActionResponseSuccessForPartiallyLoadedAndLoadingLimitExceedDirectionNext(state = state, action = action, limits = limits)
            }
        }
    } else {
        when(state.direction) {
            PaginationPagesRequest.Direction.PREVIOUS -> {
                reduceActionResponseSuccessForPartiallyLoadedAndLoadingLimitNotExceedDirectionPrevious(state = state, action = action)
            }
            PaginationPagesRequest.Direction.NEXT     -> {
                reduceActionResponseSuccessForPartiallyLoadedAndLoadingLimitNotExceedDirectionNext(state = state, action = action)
            }
        }
    }
    return ReduceResult(newState)
}

private fun <F : Any, ItemId : Any, PageId : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingLimitExceedDirectionPrevious(
        state: PaginationState.PartiallyLoadedAndLoading<F, ItemId, RI, PageId>,
        action: Action.Response.Success<F, ItemId, RI, PageId>,
        limits: PaginationLimits
): PaginationState<F, ItemId, RI, PageId> {
    return when (action.response) {
        is PaginationResponse.LastPage   -> {
            val responseIsFirstPage = responseIsFirstPage(state = state, action = action)
            val limitedItems = action.response.result.take(limits.maxStoredItemsCount)
            val currentPages = listOf(PageRelation(
                    pageId = null,
                    itemsIds = limitedItems.map { singleItem -> singleItem.id }
            ))
            if(responseIsFirstPage) {
                PaginationState.AllLoaded(
                        items = limitedItems,
                        filter = state.filter,
                        currentPages = currentPages
                )
            } else {
                PaginationState.PartiallyLoaded.withItemsBefore(
                        items = limitedItems,
                        filter = state.filter,
                        previousId = action.response.previousId,
                        currentPages = currentPages
                )
            }
        }
        is PaginationResponse.MiddlePage -> {
            reduceMiddlePageForExceedLimitLoadDirectionPrevious(state = state, response = action.response, limits = limits)
        }
    }
}

private fun <F : Any, ItemId : Any, PageId : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingLimitExceedDirectionNext(
        state: PaginationState.PartiallyLoadedAndLoading<F, ItemId, RI, PageId>,
        action: Action.Response.Success<F, ItemId, RI, PageId>,
        limits: PaginationLimits
): PaginationState<F, ItemId, RI, PageId> {

    val allCombinedItems = (state.items + action.response.result)

    val firstlyLimitedItems = allCombinedItems.takeLast(limits.maxStoredItemsCount)
    val lastDroppedItemIndex = ((allCombinedItems.size - limits.maxStoredItemsCount) - 1)
    val lastDroppedItem = allCombinedItems[lastDroppedItemIndex]

    val currentPages = mutableListOf<PageRelation<ItemId, PageId>>()

    var lastDroppedPage: PageRelation<ItemId, PageId>? = null

    for (index in state.currentPages.lastIndex downTo 0) {
        val page = state.currentPages[index]

        if (page.itemsIds.contains(lastDroppedItem.id)) {
            lastDroppedPage = page
            break
        } else {
            currentPages.add(0, page)
        }
    }

    val newReceivedPageRelation = action.response.toPageRelation()
    currentPages.add(newReceivedPageRelation)

    val finalLimitedItems = firstlyLimitedItems.dropWhile { singleItem ->
        lastDroppedPage!!.itemsIds.contains(singleItem.id)
    }

    return when (action.response) {
        is PaginationResponse.LastPage   -> {
            PaginationState.PartiallyLoaded.withItemsBefore(
                    items = finalLimitedItems,
                    filter = state.filter,
                    previousId = lastDroppedPage?.pageId,
                    currentPages = currentPages
            )
        }
        is PaginationResponse.MiddlePage -> {
            PaginationState.PartiallyLoaded.withItemsBeforeAndAfter(
                    items = finalLimitedItems,
                    filter = state.filter,
                    previousId = lastDroppedPage?.pageId,
                    nextId = action.response.nextId,
                    currentPages = currentPages
            )
        }
    }
}

private fun <F : Any, ItemId : Any, PageId : Any, RI : RecyclerItem<ItemId>> reduceMiddlePageForExceedLimitLoadDirectionPrevious(
        state: PaginationState.PartiallyLoadedAndLoading<F, ItemId, RI, PageId>,
        response: PaginationResponse.MiddlePage<RI, PageId>,
        limits: PaginationLimits
): PaginationState.PartiallyLoaded<F, ItemId, RI, PageId> {

    val allCombinedItems = (response.result + state.items)

    val firstlyLimitedItems = allCombinedItems.take(limits.maxStoredItemsCount)
    val firstlyDroppedItem = allCombinedItems[limits.maxStoredItemsCount]

    val currentPages = mutableListOf<PageRelation<ItemId, PageId>>()
    val newReceivedPageRelation = response.toPageRelation()
    currentPages.add(newReceivedPageRelation)

    var firstDroppedPage: PageRelation<ItemId, PageId>? = null

    for (page in state.currentPages) {
        if (page.itemsIds.contains(firstlyDroppedItem.id)) {
            firstDroppedPage = page
            break
        } else {
            currentPages.add(page)
        }
    }

    val finalLimitedItems = firstlyLimitedItems.dropLastWhile { singleItem ->
        firstDroppedPage!!.itemsIds.contains(singleItem.id)
    }

    return PaginationState.PartiallyLoaded.withItemsBeforeAndAfter(
            items = finalLimitedItems,
            filter = state.filter,
            previousId = response.previousId,
            nextId = firstDroppedPage?.pageId!!,
            currentPages = currentPages
    )
}

private fun <F : Any, ItemId : Any, PageId : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingLimitNotExceedDirectionPrevious(
        state: PaginationState.PartiallyLoadedAndLoading<F, ItemId, RI, PageId>,
        action: Action.Response.Success<F, ItemId, RI, PageId>
): PaginationState<F, ItemId, RI, PageId> {
    val responseIsFirstPage = responseIsFirstPage(state = state, action = action)
    val responsePageRelation = action.response.toPageRelation()
    return when (action.response) {
        is PaginationResponse.LastPage   -> {
            val updatedCurrentPages = listOf(responsePageRelation)
            if (responseIsFirstPage) {
                PaginationState.AllLoaded(
                        items = action.response.result,
                        filter = state.filter,
                        currentPages = updatedCurrentPages
                )
            } else {
                PaginationState.PartiallyLoaded.withItemsBefore(
                        items = action.response.result,
                        filter = state.filter,
                        previousId = action.response.previousId,
                        currentPages = updatedCurrentPages
                )
            }
        }
        is PaginationResponse.MiddlePage -> {
            val updatedItems = (action.response.result + state.items)
            val updatedCurrentPages = responsePageRelation.addAsFirstTo(state.currentPages)
            when {
                (responseIsFirstPage && state.hasItemsAfter) -> {
                    PaginationState.PartiallyLoaded.withItemsAfter(
                            items = updatedItems,
                            filter = state.filter,
                            nextId = state.nextId!!,
                            currentPages = updatedCurrentPages
                    )
                }
                (responseIsFirstPage && !state.hasItemsAfter) -> {
                    PaginationState.AllLoaded(
                            items = updatedItems,
                            filter = state.filter,
                            currentPages = updatedCurrentPages
                    )
                }
                (!responseIsFirstPage && state.hasItemsAfter) -> {
                    PaginationState.PartiallyLoaded.withItemsBeforeAndAfter(
                            items = updatedItems,
                            filter = state.filter,
                            previousId = action.response.previousId,
                            nextId = state.nextId!!,
                            currentPages = updatedCurrentPages
                    )
                }
                // !responseIsFirstPage && !state.hasItemsAfter
                else -> {
                    PaginationState.PartiallyLoaded.withItemsBefore(
                            items = updatedItems,
                            filter = state.filter,
                            previousId = action.response.previousId,
                            currentPages = updatedCurrentPages
                    )
                }
            }
        }
    }
}

private fun <F : Any, ItemId : Any, PageId : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingLimitNotExceedDirectionNext(
        state: PaginationState.PartiallyLoadedAndLoading<F, ItemId, RI, PageId>,
        action: Action.Response.Success<F, ItemId, RI, PageId>
): PaginationState<F, ItemId, RI, PageId> {
    val updatedItems = (state.items + action.response.result)
    val responsePageRelation = action.response.toPageRelation()
    val updatedCurrentPages = (state.currentPages + responsePageRelation)
    return when (action.response) {
        is PaginationResponse.LastPage   -> {
            if (state.hasItemsBefore) {
                PaginationState.PartiallyLoaded.withItemsBefore(
                        items = updatedItems,
                        filter = state.filter,
                        previousId = state.previousId,
                        currentPages = updatedCurrentPages
                )
            } else {
                PaginationState.AllLoaded(
                        items = updatedItems,
                        filter = state.filter,
                        currentPages = updatedCurrentPages
                )
            }
        }
        is PaginationResponse.MiddlePage -> {
            if (state.hasItemsBefore) {
                PaginationState.PartiallyLoaded.withItemsBeforeAndAfter(
                        items = updatedItems,
                        filter = state.filter,
                        previousId = state.previousId,
                        nextId = action.response.nextId,
                        currentPages = updatedCurrentPages
                )
            } else {
                PaginationState.PartiallyLoaded.withItemsAfter(
                        items = updatedItems,
                        filter = state.filter,
                        nextId = action.response.nextId,
                        currentPages = updatedCurrentPages
                )
            }
        }
    }
}

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceActionLoadMoreForPartiallyLoadedOrWithError(
        action: Action.UI.LoadMore<F, ItemId, RI, PageId>,
        state: PaginationState<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        if (state.hasSenseForLoading(action.direction)) {
            ReduceResult(
                    PaginationState.PartiallyLoadedAndLoading(
                            items = state.items,
                            filter = state.filter,
                            currentPages = state.currentPages,
                            previousId = state.previousId,
                            nextId = state.nextId,
                            direction = action.direction
                    ),
                    createLoadMoreSideEffect(action = action, state = state)
            )
        } else {
            ReduceResult(state)
        }

private fun <F : Any, ItemId : Any, PageId : Any, RI : RecyclerItem<ItemId>> createLoadMoreSideEffect(
        action: Action.UI.LoadMore<F, ItemId, RI, PageId>,
        state: PaginationState<F, ItemId, RI, PageId>
): PaginationSideEffect.LoadMore<PageId> {
    val paginationPagesRequest = when (action.direction) {
        PaginationPagesRequest.Direction.NEXT     -> {
            PaginationPagesRequest.Next(
                    lastLoadedPageId = state.currentPages.lastOrNull()?.pageId,
                    nextPageId = state.nextId
            )
        }
        PaginationPagesRequest.Direction.PREVIOUS -> {
            PaginationPagesRequest.Previous(
                    previousPageId = state.previousId,
                    firstLoadedPageId = state.currentPages.firstOrNull()?.pageId
            )
        }
    }
    return PaginationSideEffect.LoadMore(paginationPagesRequest = paginationPagesRequest)
}

private inline fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> reduceActionChangeForNonEmptyList(
        state: PaginationState<F, ItemId, RI, PageId>,
        copyStateWithNewItems: (List<RI>, List<PageRelation<ItemId, PageId>>) -> PaginationState<F, ItemId, RI, PageId>,
        action: Action.Change<F, ItemId, RI, PageId>
): ReduceResult<F, ItemId, RI, PageId> =
        when (action) {
            is Action.Change.SingleItemUpdated -> {
                val updatedItems: List<RI> = state.items.withReplacedFirst(action.updatedItem) { item -> (item.id == action.updatedItem.id) }
                val sideEffects = if (action.notifyStateUpdated) {
                    emptyArray()
                } else {
                    arrayOf(PaginationSideEffect.AvoidNotifyStateUpdated<PageId>())
                }
                @Suppress("SpreadOperator")
                ReduceResult(copyStateWithNewItems(updatedItems, state.currentPages), *sideEffects)
            }
            is Action.Change.SingleItemDeleted -> {
                val updatedItems: List<RI> = state.items.withoutFirst { item -> (item.id == action.deletedItemId) }
                val updatedCurrentPages: List<PageRelation<ItemId, PageId>> = state.currentPages
                        .mapNotNull { page ->
                            val listWithoutDeleted = page.itemsIds.withoutFirst { singleItemId -> (singleItemId == action.deletedItemId) }
                            val wasRemoved = (listWithoutDeleted.size != page.itemsIds.size)
                            when {
                                (wasRemoved && listWithoutDeleted.isNotEmpty()) -> page.copy(itemsIds = listWithoutDeleted)
                                (wasRemoved && listWithoutDeleted.isEmpty())    -> null
                                else                                            -> page
                            }
                        }
                ReduceResult(copyStateWithNewItems(updatedItems, updatedCurrentPages))
            }
        }

private fun <ItemId : Any, PageId : Any, RI : RecyclerItem<ItemId>> PaginationResponse<RI, PageId>.toPageRelation(): PageRelation<ItemId, PageId> =
        PageRelation(
                pageId = this.currentId,
                itemsIds = this.result.map { singleItem -> singleItem.id }
        )