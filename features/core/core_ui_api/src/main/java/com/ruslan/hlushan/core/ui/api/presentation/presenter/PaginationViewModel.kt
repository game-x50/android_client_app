package com.ruslan.hlushan.core.ui.api.presentation.presenter

import com.ruslan.hlushan.core.api.dto.PageId
import com.ruslan.hlushan.core.api.dto.PaginationPagesRequest
import com.ruslan.hlushan.core.api.dto.PaginationResponse
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.Action
import com.ruslan.hlushan.core.api.dto.PageRelation
import com.ruslan.hlushan.core.api.dto.PageRelationsList
import com.ruslan.hlushan.core.api.dto.plus
import com.ruslan.hlushan.core.api.dto.removeItemId
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.PaginationLimits
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.PaginationSideEffect
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.PaginationState
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.createPaginationRequestFor
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.itemsCount
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
abstract class PaginationViewModel<F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any>(
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
        get() = state.itemsCount

    @UiMainThread
    protected var state: PaginationState<F, ItemId, RI, Id> = PaginationState.Active.Empty.Default(filter = initFilter)
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
            paginationPagesRequest: PaginationPagesRequest<Id>,
            filter: F
    ): Single<PaginationResponse<RI, Id>>

    @UiMainThread
    protected abstract fun onStateUpdated()

    @UiMainThread
    fun retryIfError() {
        val localState = state
        if (localState is PaginationState.WithError) {
            loadMoreAction(direction = localState.additional.loadDirection)
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
    private fun handleAction(action: Action<F, ItemId, RI, Id>) {
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
    private fun loadMore(params: PaginationSideEffect.LoadMore<Id>, filter: F) {
        requestDisposable?.safetyDispose()

        requestDisposable = loadData(paginationPagesRequest = params.paginationPagesRequest, filter = filter)
                .map<Action<F, ItemId, RI, Id>> { response -> Action.Response.Success(response) }
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
    private fun setNewState(newState: PaginationState<F, ItemId, RI, Id>, notifyStateUpdated: Boolean) {
        if (state != newState) {
            state = newState

            if (notifyStateUpdated) {
                onStateUpdated()
            }
        }
    }
}

internal class ReduceResult<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any>(
        val newState: PaginationState<F, ItemId, RI, Id>,
        vararg val sideEffects: PaginationSideEffect<Id>
)

internal fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceState(
        state: PaginationState<F, ItemId, RI, Id>,
        action: Action<F, ItemId, RI, Id>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> =
        when (state) {
            is PaginationState.Active.Empty.Default              -> reduceStateEmptyDefault(state, action)
            is PaginationState.Active.Empty.Loading              -> reduceStateActiveEmptyLoading(state, action, limits)
            is PaginationState.Active.Empty.WithError            -> reduceStateActiveEmptyWithError(state, action)
            is PaginationState.Active.PartiallyLoaded.Default    -> reduceStateActivePartiallyLoadedDefault(state, action)
            is PaginationState.Active.PartiallyLoaded.AndLoading -> reduceStateActivePartiallyLoadedAndLoading(state, action, limits)
            is PaginationState.Active.PartiallyLoaded.WithError  -> reduceStateActivePartiallyLoadedWithError(state, action)
            is PaginationState.Finished.WithResults              -> reduceStateFinishedWithResults(state, action)
            is PaginationState.Finished.Empty                    -> reduceStateFinishedEmpty(state, action)
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateEmptyDefault(
        state: PaginationState.Active.Empty.Default<F>,
        action: Action<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is Action.UI.Refresh               -> {
                reduceActionRefresh(action)
            }
            is Action.UI.LoadMore              -> {
                reduceActionLoadMoreForInitOrActiveEmptyWithError(state = state)
            }
            is Action.Response.Success,
            is Action.Response.Error,
            is Action.Change.SingleItemUpdated,
            is Action.Change.SingleItemDeleted -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateActiveEmptyLoading(
        state: PaginationState.Active.Empty.Loading<F>,
        action: Action<F, ItemId, RI, Id>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is Action.UI.Refresh               -> {
                reduceActionRefresh(action)
            }
            is Action.Response.Success         -> {
                reduceActionResponseSuccessForActiveEmptyLoading(
                        action = action,
                        state = state,
                        limits = limits
                )
            }
            is Action.Response.Error           -> {
                ReduceResult(PaginationState.Active.Empty.WithError(filter = state.filter, error = action.error))
            }
            is Action.UI.LoadMore,
            is Action.Change.SingleItemUpdated,
            is Action.Change.SingleItemDeleted -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateActiveEmptyWithError(
        state: PaginationState.Active.Empty.WithError<F>,
        action: Action<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is Action.UI.Refresh               -> {
                reduceActionRefresh(action)
            }
            is Action.UI.LoadMore              -> {
                reduceActionLoadMoreForInitOrActiveEmptyWithError(state = state)
            }
            is Action.Response.Success,
            is Action.Response.Error,
            is Action.Change.SingleItemUpdated,
            is Action.Change.SingleItemDeleted -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateActivePartiallyLoadedDefault(
        state: PaginationState.Active.PartiallyLoaded.Default<F, ItemId, RI, Id>,
        action: Action<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is Action.UI.Refresh     -> {
                reduceActionRefresh(action)
            }
            is Action.UI.LoadMore    -> {
                reduceActionLoadMoreForActivePartiallyLoadedDefaultOrWithError(action = action, state = state)
            }
            is Action.Change         -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        pageRelationsList = state.pageRelationsList,
                        copyStateWithNewItems = { updatedItems, updatedPageRelationsList ->
                            state.copy(items = updatedItems, pageRelationsList = updatedPageRelationsList)
                        },
                        action = action
                )
            }
            is Action.Response.Success,
            is Action.Response.Error -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateActivePartiallyLoadedAndLoading(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        action: Action<F, ItemId, RI, Id>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> =
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
                        PaginationState.Active.PartiallyLoaded.WithError(
                                items = state.items,
                                filter = state.filter,
                                pageRelationsList = state.pageRelationsList,
                                nextPageId = state.nextPageId,
                                direction = state.direction,
                                error = action.error
                        )
                )
            }
            is Action.Change           -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        pageRelationsList = state.pageRelationsList,
                        copyStateWithNewItems = { updatedItems, updatedPageRelationsList ->
                            state.copy(items = updatedItems, pageRelationsList = updatedPageRelationsList)
                        },
                        action = action
                )
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateActivePartiallyLoadedWithError(
        state: PaginationState.Active.PartiallyLoaded.WithError<F, ItemId, RI, Id>,
        action: Action<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is Action.UI.Refresh     -> {
                reduceActionRefresh(action)
            }
            is Action.UI.LoadMore    -> {
                reduceActionLoadMoreForActivePartiallyLoadedDefaultOrWithError(action = action, state = state)
            }
            is Action.Change         -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        pageRelationsList = state.pageRelationsList,
                        copyStateWithNewItems = { updatedItems, updatedPageRelationsList ->
                            state.copy(items = updatedItems, pageRelationsList = updatedPageRelationsList)
                        },
                        action = action
                )
            }
            is Action.Response.Success,
            is Action.Response.Error -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateFinishedWithResults(
        state: PaginationState.Finished.WithResults<F, ItemId, RI, Id>,
        action: Action<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is Action.UI.Refresh     -> {
                reduceActionRefresh(action)
            }
            is Action.Change         -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        pageRelationsList = state.pageRelationsList,
                        copyStateWithNewItems = { updatedItems, updatedPageRelationsList ->
                            state.copy(items = updatedItems, pageRelationsList = updatedPageRelationsList)
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

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateFinishedEmpty(
        state: PaginationState.Finished.Empty<F>,
        action: Action<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is Action.UI.Refresh     -> {
                reduceActionRefresh(action)
            }
            is Action.Change.SingleItemUpdated,
            is Action.Change.SingleItemDeleted,
            is Action.UI.LoadMore,
            is Action.Response.Success,
            is Action.Response.Error -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceActionRefresh(
        action: Action.UI.Refresh<F>
): ReduceResult<F, ItemId, RI, Id> =
        ReduceResult(
                PaginationState.Active.Empty.Loading(filter = action.filter),
                PaginationSideEffect.LoadMore(paginationPagesRequest = PaginationPagesRequest.Init())
        )

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceActionLoadMoreForInitOrActiveEmptyWithError(
        state: PaginationState<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> =
        ReduceResult(
                PaginationState.Active.Empty.Loading(filter = state.filter),
                PaginationSideEffect.LoadMore(paginationPagesRequest = PaginationPagesRequest.Init())
        )

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceActionResponseSuccessForActiveEmptyLoading(
        action: Action.Response.Success<ItemId, RI, Id>,
        state: PaginationState.Active.Empty.Loading<F>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> {

    val limitedItems = action.response.result.take(limits.maxStoredItemsCount)

    val newState = when (action.response) {
        is PaginationResponse.FirstPage -> {
            PaginationState.Active.PartiallyLoaded.Default(
                    items = limitedItems,
                    filter = state.filter,
                    pageRelationsList = PageRelationsList.IncludingFirst(
                            firstPage = action.response.toPageRelation(),
                            secondAndMorePages = emptyList()
                    ),
                    nextPageId = action.response.nextPageId
            )
        }
        is PaginationResponse.SinglePage -> {
            if (limitedItems.isEmpty()) {
                PaginationState.Finished.Empty(
                        filter = state.filter
                )
            } else {
                PaginationState.Finished.WithResults(
                        items = limitedItems,
                        filter = state.filter,
                        pageRelationsList = PageRelationsList.IncludingFirst(
                                firstPage = action.response.toPageRelation(),
                                secondAndMorePages = emptyList()
                        )
                )
            }
        }
        //unexpected
        is PaginationResponse.MiddlePage -> {
            PaginationState.Active.PartiallyLoaded.Default(
                    items = limitedItems,
                    filter = state.filter,
                    pageRelationsList = PageRelationsList.WithoutFirst(
                            previousPageId = action.response.previousPageId,
                            secondAndMorePages = listOf(action.response.toPageRelation())
                    ),
                    nextPageId = action.response.nextPageId
            )
        }
        is PaginationResponse.LastPage -> {
            PaginationState.Active.PartiallyLoaded.Default(
                    items = limitedItems,
                    filter = state.filter,
                    pageRelationsList = PageRelationsList.WithoutFirst(
                            previousPageId = action.response.previousPageId,
                            secondAndMorePages = listOf(action.response.toPageRelation())
                    ),
                    nextPageId = null
            )
        }
    }
    return ReduceResult(newState)
}

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceActionResponseSuccessForPartiallyLoadedAndLoading(
        action: Action.Response.Success<ItemId, RI, Id>,
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> = when(state.direction) {
        PaginationPagesRequest.Direction.PREVIOUS -> {
            reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionPrevious(state = state, action = action, limits = limits)
        }
        PaginationPagesRequest.Direction.NEXT     -> {
            reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionNext(state = state, action = action, limits = limits)
        }
    }

private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionPrevious(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        action: Action.Response.Success<ItemId, RI, Id>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> =
     when (action.response) {
        is PaginationResponse.FirstPage -> {
            reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionPreviousFirstOrMiddlePage(
                    state = state,
                    receivedResult = action.response.result,
                    receivedPageRelation = action.response.toPageRelation(),
                    receivedPreviousPageId = PreviousPageId.NoPage,
                    limits = limits
            )
        }
        is PaginationResponse.MiddlePage -> {
            reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionPreviousFirstOrMiddlePage(
                    state = state,
                    receivedResult = action.response.result,
                    receivedPageRelation = action.response.toPageRelation(),
                    receivedPreviousPageId = action.response.previousPageId,
                    limits = limits
            )
        }
        //unexpected
        is PaginationResponse.LastPage,
        is PaginationResponse.SinglePage -> {
            reduceActionResponseSuccessForActiveEmptyLoading(
                    state = PaginationState.Active.Empty.Loading(filter = state.filter),
                    action = action,
                    limits = limits
            )
        }
    }

private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionNext(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        action: Action.Response.Success<ItemId, RI, Id>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> =
        when (action.response) {
            is PaginationResponse.MiddlePage -> {
                reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionNextMiddleOrLastPage(
                        state = state,
                        receivedResult = action.response.result,
                        receivedPageRelation = action.response.toPageRelation(),
                        receivedNextPageId = action.response.nextPageId,
                        limits = limits
                )
            }
            is PaginationResponse.LastPage -> {
                reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionNextMiddleOrLastPage(
                        state = state,
                        receivedResult = action.response.result,
                        receivedPageRelation = action.response.toPageRelation(),
                        receivedNextPageId = null,
                        limits = limits
                )
            }
            //unexpected
            is PaginationResponse.FirstPage,
            is PaginationResponse.SinglePage -> {
                reduceActionResponseSuccessForActiveEmptyLoading(
                        state = PaginationState.Active.Empty.Loading(filter = state.filter),
                        action = action,
                        limits = limits
                )
            }
        }

private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionPreviousFirstOrMiddlePage(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        receivedResult: List<RI>,
        receivedPageRelation: PageRelation<Id, ItemId, PageId<Id>>,
        receivedPreviousPageId: PageId<Id>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> {

    val newState = if (isLimitsExceed(receivedResult, state, limits)) {
        reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionPreviousFirstOrMiddlePageLimitsExceed(
                state = state,
                receivedResult = receivedResult,
                receivedPageRelation = receivedPageRelation,
                receivedPreviousPageId = receivedPreviousPageId,
                limits = limits
        )
    } else {
        reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionPreviousFirstOrMiddlePageLimitsNoExceed(
                state = state,
                receivedResult = receivedResult,
                receivedPageRelation = receivedPageRelation,
                receivedPreviousPageId = receivedPreviousPageId
        )
    }

    return ReduceResult(newState)
}

private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionNextMiddleOrLastPage(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        receivedResult: List<RI>,
        receivedPageRelation: PageRelation<Id, ItemId, PageId.SecondOrMore<Id>>,
        receivedNextPageId: Id?,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> {

    val newState = if (isLimitsExceed(receivedResult, state, limits)) {
        reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionNextMiddleOrLastPageLimitsExceed(
                state = state,
                receivedResult = receivedResult,
                receivedPageRelation = receivedPageRelation,
                receivedNextPageId = receivedNextPageId,
                limits = limits
        )
    } else {
        reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionNextMiddleOrLastPageLimitsNoExceed(
                state = state,
                receivedResult = receivedResult,
                receivedPageRelation = receivedPageRelation,
                receivedNextPageId = receivedNextPageId,
        )
    }

    return ReduceResult(newState)
}

private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionPreviousFirstOrMiddlePageLimitsExceed(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        receivedResult: List<RI>,
        receivedPageRelation: PageRelation<Id, ItemId, PageId<Id>>,
        receivedPreviousPageId: PageId<Id>,
        limits: PaginationLimits
): PaginationState.Active.PartiallyLoaded.Default<F, ItemId, RI, Id> {

    val allCombinedItems = (receivedResult + state.items)

    val firstlyLimitedItems = allCombinedItems.take(limits.maxStoredItemsCount)
    val firstlyDroppedItem = allCombinedItems[limits.maxStoredItemsCount]

    val currentPages = mutableListOf<PageRelation<Id, ItemId>>()
    currentPages.add(receivedPageRelation)

    var firstDroppedPage: PageRelation<Id, ItemId>? = null

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

    val nextPageId = NextPageId.Existing(value = (firstDroppedPage?.pageId as PageId.SecondOrMore))

    return PaginationState.Active.PartiallyLoaded.Default(
            items = finalLimitedItems,
            filter = state.filter,
            previousPageId = receivedPreviousPageId,
            nextPageId = nextPageId,
            currentPages = currentPages
    )
}

private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionPreviousFirstOrMiddlePageLimitsNoExceed(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        receivedResult: List<RI>,
        receivedPageRelation: PageRelation<Id, ItemId, PageId<Id>>,
        receivedPreviousPageId: PageId<Id>? //null in case of received first page
): PaginationState<F, ItemId, RI, Id> {

    val updatedItems = (receivedResult + state.items)
    val updatedPageRelationsList = receivedPageRelation.addAsFirstTo(state.pageRelationsList)

    return if((receivedPreviousPageId == null) && (state.nextPageId == null)) {
        PaginationState.Finished.WithResults(
                filter = state.filter,
                items = updatedItems,
                pageRelationsList = updatedPageRelationsList
        )
    } else {
        PaginationState.Active.PartiallyLoaded.Default(
                items = updatedItems,
                filter = state.filter,
                nextPageId = state.nextPageId,
                pageRelationsList = updatedPageRelationsList
        )
    }
}

private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionNextMiddleOrLastPageLimitsExceed(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        receivedResult: List<RI>,
        receivedPageRelation: PageRelation<Id, ItemId, PageId.SecondOrMore<Id>>,
        receivedNextPageId: Id?,
        limits: PaginationLimits
): PaginationState<F, ItemId, RI, Id> {

    val allCombinedItems = (state.items + receivedResult)

    val firstlyLimitedItems = allCombinedItems.takeLast(limits.maxStoredItemsCount)
    val lastDroppedItemIndex = ((allCombinedItems.size - limits.maxStoredItemsCount) - 1)
    val lastDroppedItem = allCombinedItems[lastDroppedItemIndex]

    val currentPages = mutableListOf<PageRelation<Id, ItemId>>()

    var lastDroppedPage: PageRelation<Id, ItemId>? = null

    for (index in state.currentPages.lastIndex downTo 0) {
        val page = state.currentPages[index]

        if (page.itemsIds.contains(lastDroppedItem.id)) {
            lastDroppedPage = page
            break
        } else {
            currentPages.add(0, page)
        }
    }

    currentPages.add(receivedPageRelation)

    val finalLimitedItems = firstlyLimitedItems.dropWhile { singleItem ->
        lastDroppedPage!!.itemsIds.contains(singleItem.id)
    }

    val previousPageId = PreviousPageId.Existing(value = (lastDroppedPage!!.pageId))

    return PaginationState.Active.PartiallyLoaded.Default(
            items = finalLimitedItems,
            filter = state.filter,
            previousPageId = previousPageId,
            nextPageId = receivedNextPageId,
            currentPages = currentPages
    )
}

private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionNextMiddleOrLastPageLimitsNoExceed(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        receivedResult: List<RI>,
        receivedPageRelation: PageRelation<Id, ItemId, PageId.SecondOrMore<Id>>,
        receivedNextPageId: Id?
): PaginationState<F, ItemId, RI, Id> {

    val updatedItems = (state.items + receivedResult)

    return if((state.pageRelationsList is PageRelationsList.IncludingFirst) && (receivedNextPageId == null)) {
        PaginationState.Finished.WithResults(
                filter = state.filter,
                items = updatedItems,
                pageRelationsList = state.pageRelationsList.plus(receivedPageRelation)
        )
    } else {
        PaginationState.Active.PartiallyLoaded.Default(
                items = updatedItems,
                filter = state.filter,
                nextPageId = receivedNextPageId,
                pageRelationsList = state.pageRelationsList.plus(receivedPageRelation)
        )
    }
}

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceActionLoadMoreForActivePartiallyLoadedDefaultOrWithError(
        action: Action.UI.LoadMore,
        state: PaginationState.Active.PartiallyLoaded<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> {

    val paginationPagesRequest = state.createPaginationRequestFor(direction = action.direction)

    return if (paginationPagesRequest != null) {
        ReduceResult(
                PaginationState.Active.PartiallyLoaded.AndLoading(
                        items = state.items,
                        filter = state.filter,
                        currentPages = state.currentPages,
                        previousPageId = state.previousPageId,
                        nextPageId = state.nextPageId,
                        direction = action.direction
                ),
                PaginationSideEffect.LoadMore(paginationPagesRequest = paginationPagesRequest)
        )
    } else {
        ReduceResult(state)
    }
}

private inline fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any, PRL : PageRelationsList<Id, ItemId>, PS>
        reduceActionChangeForNonEmptyList(
        state: PS,
        pageRelationsList: PRL,
        copyStateWithNewItems: (List<RI>, PRL) -> PS,
        action: Action.Change<ItemId, RI>
): ReduceResult<F, ItemId, RI, Id>
        where PS : PaginationState<F, ItemId, RI, Id>, PS : PaginationState.WithItems<ItemId, RI, Id> =
        when (action) {
            is Action.Change.SingleItemUpdated -> {
                val updatedItems: List<RI> = state.items.withReplacedFirst(action.updatedItem) { item -> (item.id == action.updatedItem.id) }
                val sideEffects = if (action.notifyStateUpdated) {
                    emptyArray()
                } else {
                    arrayOf(PaginationSideEffect.AvoidNotifyStateUpdated())
                }
                @Suppress("SpreadOperator")
                ReduceResult(copyStateWithNewItems(updatedItems, pageRelationsList), *sideEffects)
            }
            is Action.Change.SingleItemDeleted -> {
                val updatedItems: List<RI> = state.items.withoutFirst { item -> (item.id == action.deletedItemId) }
                val updatedPageRelationsList = pageRelationsList.removeItemId(itemId = action.deletedItemId)
                ReduceResult(copyStateWithNewItems(updatedItems, updatedPageRelationsList))
            }
        }

private fun <F : Any, Id : Any, ItemId : Any, RI : RecyclerItem<ItemId>> isLimitsExceed(
        receivedResult: List<RI>,
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        limits: PaginationLimits
): Boolean = ((receivedResult.size + state.items.size) > limits.maxStoredItemsCount)

//todo: to another file
private fun <ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>>
        PaginationResponse.FirstPage<RI, Id>.toPageRelation(): PageRelation<Id, ItemId, PageId.First> =
        PageRelation(
                pageId = this.currentPageId,
                itemsIds = this.result.map { singleItem -> singleItem.id }
        )

private fun <ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>>
        PaginationResponse.MiddlePage<RI, Id>.toPageRelation(): PageRelation<Id, ItemId, PageId.SecondOrMore<Id>> =
        PageRelation(
                pageId = this.currentPageId,
                itemsIds = this.result.map { singleItem -> singleItem.id }
        )

private fun <ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>>
        PaginationResponse.LastPage<RI, Id>.toPageRelation(): PageRelation<Id, ItemId, PageId.SecondOrMore<Id>> =
        PageRelation(
                pageId = this.currentPageId,
                itemsIds = this.result.map { singleItem -> singleItem.id }
        )

private fun <ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>>
        PaginationResponse.SinglePage<RI, Id>.toPageRelation(): PageRelation<Id, ItemId, PageId.First> =
        PageRelation(
                pageId = this.currentPageId,
                itemsIds = this.result.map { singleItem -> singleItem.id }
        )