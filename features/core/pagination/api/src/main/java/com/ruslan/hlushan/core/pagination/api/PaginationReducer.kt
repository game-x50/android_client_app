@file:Suppress("TooManyFunctions", "MatchingDeclarationName", "MaxLineLength")

package com.ruslan.hlushan.core.pagination.api

import com.ruslan.hlushan.core.extensions.addAsFirstTo
import com.ruslan.hlushan.core.extensions.withReplacedFirst
import com.ruslan.hlushan.core.extensions.withoutFirst
import com.ruslan.hlushan.core.recycler.item.RecyclerItem

class ReduceResult<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any>(
        val newState: PaginationState<F, ItemId, RI, Id>,
        vararg val sideEffects: PaginationSideEffect<Id>
)

//TODO: #write_unit_tests
fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceState(
        state: PaginationState<F, ItemId, RI, Id>,
        action: PaginationAction<F, ItemId, RI, Id>,
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
        action: PaginationAction<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is PaginationAction.UI.Refresh               -> {
                reduceActionRefresh(action)
            }
            is PaginationAction.UI.LoadMore              -> {
                reduceActionLoadMoreForInitOrActiveEmptyWithError(state = state)
            }
            is PaginationAction.Response.Success,
            is PaginationAction.Response.Error,
            is PaginationAction.Change.SingleItemUpdated,
            is PaginationAction.Change.SingleItemDeleted -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateActiveEmptyLoading(
        state: PaginationState.Active.Empty.Loading<F>,
        action: PaginationAction<F, ItemId, RI, Id>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is PaginationAction.UI.Refresh               -> {
                reduceActionRefresh(action)
            }
            is PaginationAction.Response.Success         -> {
                reduceActionResponseSuccessForActiveEmptyLoading(
                        action = action,
                        state = state,
                        limits = limits
                )
            }
            is PaginationAction.Response.Error           -> {
                ReduceResult(PaginationState.Active.Empty.WithError(filter = state.filter, error = action.error))
            }
            is PaginationAction.UI.LoadMore,
            is PaginationAction.Change.SingleItemUpdated,
            is PaginationAction.Change.SingleItemDeleted -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateActiveEmptyWithError(
        state: PaginationState.Active.Empty.WithError<F>,
        action: PaginationAction<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is PaginationAction.UI.Refresh               -> {
                reduceActionRefresh(action)
            }
            is PaginationAction.UI.LoadMore              -> {
                reduceActionLoadMoreForInitOrActiveEmptyWithError(state = state)
            }
            is PaginationAction.Response.Success,
            is PaginationAction.Response.Error,
            is PaginationAction.Change.SingleItemUpdated,
            is PaginationAction.Change.SingleItemDeleted -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateActivePartiallyLoadedDefault(
        state: PaginationState.Active.PartiallyLoaded.Default<F, ItemId, RI, Id>,
        action: PaginationAction<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is PaginationAction.UI.Refresh     -> {
                reduceActionRefresh(action)
            }
            is PaginationAction.UI.LoadMore    -> {
                reduceActionLoadMoreForActivePartiallyLoadedDefaultOrWithError(action = action, state = state)
            }
            is PaginationAction.Change         -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        copyStateWithNewItems = { updatedItems, updatedCurrentPages ->
                            state.copy(items = updatedItems, currentPages = updatedCurrentPages)
                        },
                        action = action
                )
            }
            is PaginationAction.Response.Success,
            is PaginationAction.Response.Error -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateActivePartiallyLoadedAndLoading(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        action: PaginationAction<F, ItemId, RI, Id>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is PaginationAction.UI.Refresh       -> {
                reduceActionRefresh(action)
            }
            is PaginationAction.UI.LoadMore      -> {
                ReduceResult(state)
            }
            is PaginationAction.Response.Success -> {
                reduceActionResponseSuccessForPartiallyLoadedAndLoading(
                        action = action,
                        state = state,
                        limits = limits
                )
            }
            is PaginationAction.Response.Error   -> {
                ReduceResult(
                        PaginationState.Active.PartiallyLoaded.WithError(
                                items = state.items,
                                filter = state.filter,
                                currentPages = state.currentPages,
                                previousPageId = state.previousPageId,
                                nextPageId = state.nextPageId,
                                direction = state.direction,
                                error = action.error
                        )
                )
            }
            is PaginationAction.Change           -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        copyStateWithNewItems = { updatedItems, updatedCurrentPages ->
                            state.copy(items = updatedItems, currentPages = updatedCurrentPages)
                        },
                        action = action
                )
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateActivePartiallyLoadedWithError(
        state: PaginationState.Active.PartiallyLoaded.WithError<F, ItemId, RI, Id>,
        action: PaginationAction<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is PaginationAction.UI.Refresh     -> {
                reduceActionRefresh(action)
            }
            is PaginationAction.UI.LoadMore    -> {
                reduceActionLoadMoreForActivePartiallyLoadedDefaultOrWithError(action = action, state = state)
            }
            is PaginationAction.Change         -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        copyStateWithNewItems = { updatedItems, updatedCurrentPages ->
                            state.copy(items = updatedItems, currentPages = updatedCurrentPages)
                        },
                        action = action
                )
            }
            is PaginationAction.Response.Success,
            is PaginationAction.Response.Error -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateFinishedWithResults(
        state: PaginationState.Finished.WithResults<F, ItemId, RI, Id>,
        action: PaginationAction<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is PaginationAction.UI.Refresh     -> {
                reduceActionRefresh(action)
            }
            is PaginationAction.Change         -> {
                reduceActionChangeForNonEmptyList(
                        state = state,
                        copyStateWithNewItems = { updatedItems, updatedCurrentPages ->
                            state.copy(items = updatedItems, currentPages = updatedCurrentPages)
                        },
                        action = action
                )
            }
            is PaginationAction.UI.LoadMore,
            is PaginationAction.Response.Success,
            is PaginationAction.Response.Error -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceStateFinishedEmpty(
        state: PaginationState.Finished.Empty<F>,
        action: PaginationAction<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> =
        when (action) {
            is PaginationAction.UI.Refresh     -> {
                reduceActionRefresh(action)
            }
            is PaginationAction.Change.SingleItemUpdated,
            is PaginationAction.Change.SingleItemDeleted,
            is PaginationAction.UI.LoadMore,
            is PaginationAction.Response.Success,
            is PaginationAction.Response.Error -> {
                ReduceResult(state)
            }
        }

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceActionRefresh(
        action: PaginationAction.UI.Refresh<F>
): ReduceResult<F, ItemId, RI, Id> =
        ReduceResult(
                PaginationState.Active.Empty.Loading(filter = action.filter),
                PaginationSideEffect.LoadMore(pagesRequest = PaginationPagesRequest.Init())
        )

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceActionLoadMoreForInitOrActiveEmptyWithError(
        state: PaginationState<F, ItemId, RI, Id>
): ReduceResult<F, ItemId, RI, Id> =
        ReduceResult(
                PaginationState.Active.Empty.Loading(filter = state.filter),
                PaginationSideEffect.LoadMore(pagesRequest = PaginationPagesRequest.Init())
        )

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceActionResponseSuccessForActiveEmptyLoading(
        action: PaginationAction.Response.Success<ItemId, RI, Id>,
        state: PaginationState.Active.Empty.Loading<F>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> {

    val limitedItems = action.response.result.take(limits.maxStoredItemsCount)
    val currentPages = listOf(action.response.toPageRelation())

    val newState = when (action.response) {
        is PaginationResponse.FirstPage  -> {
            PaginationState.Active.PartiallyLoaded.Default(
                    items = limitedItems,
                    filter = state.filter,
                    currentPages = currentPages,
                    previousPageId = PreviousPageId.NoPage,
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
                        currentPages = currentPages
                )
            }
        }
        //unexpected
        is PaginationResponse.MiddlePage -> {
            PaginationState.Active.PartiallyLoaded.Default(
                    items = limitedItems,
                    filter = state.filter,
                    currentPages = currentPages,
                    previousPageId = action.response.previousPageId,
                    nextPageId = action.response.nextPageId
            )
        }
        is PaginationResponse.LastPage   -> {
            PaginationState.Active.PartiallyLoaded.Default(
                    items = limitedItems,
                    filter = state.filter,
                    currentPages = currentPages,
                    previousPageId = action.response.previousPageId,
                    nextPageId = NextPageId.NoPage
            )
        }
    }
    return ReduceResult(newState)
}

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceActionResponseSuccessForPartiallyLoadedAndLoading(
        action: PaginationAction.Response.Success<ItemId, RI, Id>,
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> =
        when (state.direction) {
            PaginationPagesRequest.Direction.PREVIOUS -> {
                reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionPrevious(state = state, action = action, limits = limits)
            }
            PaginationPagesRequest.Direction.NEXT     -> {
                reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionNext(state = state, action = action, limits = limits)
            }
        }

private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionPrevious(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        action: PaginationAction.Response.Success<ItemId, RI, Id>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> =
        when (action.response) {
            is PaginationResponse.FirstPage  -> {
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
        action: PaginationAction.Response.Success<ItemId, RI, Id>,
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
            is PaginationResponse.LastPage   -> {
                reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionNextMiddleOrLastPage(
                        state = state,
                        receivedResult = action.response.result,
                        receivedPageRelation = action.response.toPageRelation(),
                        receivedNextPageId = NextPageId.NoPage,
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

@SuppressWarnings("MaxLineLength")
private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionPreviousFirstOrMiddlePage(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        receivedResult: List<RI>,
        receivedPageRelation: PageRelation<Id, ItemId>,
        receivedPreviousPageId: PreviousPageId<Id>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> {

    val newState = if ((receivedResult.size + state.items.size) > limits.maxStoredItemsCount) {
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

@SuppressWarnings("MaxLineLength")
private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionNextMiddleOrLastPage(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        receivedResult: List<RI>,
        receivedPageRelation: PageRelation<Id, ItemId>,
        receivedNextPageId: NextPageId<Id>,
        limits: PaginationLimits
): ReduceResult<F, ItemId, RI, Id> {

    val newState = if ((receivedResult.size + state.items.size) > limits.maxStoredItemsCount) {
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

@SuppressWarnings("MaxLineLength")
private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionPreviousFirstOrMiddlePageLimitsExceed(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        receivedResult: List<RI>,
        receivedPageRelation: PageRelation<Id, ItemId>,
        receivedPreviousPageId: PreviousPageId<Id>,
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

@SuppressWarnings("MaxLineLength")
private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionPreviousFirstOrMiddlePageLimitsNoExceed(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        receivedResult: List<RI>,
        receivedPageRelation: PageRelation<Id, ItemId>,
        receivedPreviousPageId: PreviousPageId<Id>
): PaginationState<F, ItemId, RI, Id> {

    val updatedItems = (receivedResult + state.items)
    val updatedCurrentPages = receivedPageRelation.addAsFirstTo(state.currentPages)

    return if (isLoadingFinished(previousPageId = receivedPreviousPageId, nextPageId = state.nextPageId)) {
        PaginationState.Finished.WithResults(
                filter = state.filter,
                items = updatedItems,
                currentPages = updatedCurrentPages
        )
    } else {
        PaginationState.Active.PartiallyLoaded.Default(
                items = updatedItems,
                filter = state.filter,
                previousPageId = receivedPreviousPageId,
                nextPageId = state.nextPageId,
                currentPages = updatedCurrentPages
        )
    }
}

@SuppressWarnings("MaxLineLength")
private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionNextMiddleOrLastPageLimitsExceed(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        receivedResult: List<RI>,
        receivedPageRelation: PageRelation<Id, ItemId>,
        receivedNextPageId: NextPageId<Id>,
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

@SuppressWarnings("MaxLineLength")
private fun <F : Any, ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> reduceActionResponseSuccessForPartiallyLoadedAndLoadingDirectionNextMiddleOrLastPageLimitsNoExceed(
        state: PaginationState.Active.PartiallyLoaded.AndLoading<F, ItemId, RI, Id>,
        receivedResult: List<RI>,
        receivedPageRelation: PageRelation<Id, ItemId>,
        receivedNextPageId: NextPageId<Id>
): PaginationState<F, ItemId, RI, Id> {

    val updatedItems = (state.items + receivedResult)
    val updatedCurrentPages = (state.currentPages + receivedPageRelation)

    return if (isLoadingFinished(previousPageId = state.previousPageId, nextPageId = receivedNextPageId)) {
        PaginationState.Finished.WithResults(
                filter = state.filter,
                items = updatedItems,
                currentPages = updatedCurrentPages
        )
    } else {
        PaginationState.Active.PartiallyLoaded.Default(
                items = updatedItems,
                filter = state.filter,
                previousPageId = state.previousPageId,
                nextPageId = receivedNextPageId,
                currentPages = updatedCurrentPages
        )
    }
}

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> reduceActionLoadMoreForActivePartiallyLoadedDefaultOrWithError(
        action: PaginationAction.UI.LoadMore,
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
                PaginationSideEffect.LoadMore(pagesRequest = paginationPagesRequest)
        )
    } else {
        ReduceResult(state)
    }
}

private inline fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any, PS> reduceActionChangeForNonEmptyList(
        state: PS,
        copyStateWithNewItems: (List<RI>, List<PageRelation<Id, ItemId>>) -> PaginationState<F, ItemId, RI, Id>,
        action: PaginationAction.Change<ItemId, RI>
): ReduceResult<F, ItemId, RI, Id>
        where PS : PaginationState<F, ItemId, RI, Id>, PS : PaginationState.WithItems<ItemId, RI, Id> =
        when (action) {
            is PaginationAction.Change.SingleItemUpdated -> {
                val updatedItems: List<RI> = state.items.withReplacedFirst(action.updatedItem) { item -> (item.id == action.updatedItem.id) }
                val sideEffects = if (action.notifyStateUpdated) {
                    emptyArray()
                } else {
                    arrayOf(PaginationSideEffect.AvoidNotifyStateUpdated())
                }
                @Suppress("SpreadOperator")
                ReduceResult(copyStateWithNewItems(updatedItems, state.currentPages), *sideEffects)
            }
            is PaginationAction.Change.SingleItemDeleted -> {
                val updatedItems: List<RI> = state.items.withoutFirst { item -> (item.id == action.deletedItemId) }
                val updatedCurrentPages: List<PageRelation<Id, ItemId>> = state.currentPages
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

private fun <ItemId : Any, Id : Any, RI : RecyclerItem<ItemId>> PaginationResponse<RI, Id>.toPageRelation(): PageRelation<Id, ItemId> =
        PageRelation(
                pageId = this.currentPageId,
                itemsIds = this.result.map { singleItem -> singleItem.id }
        )

private fun <Id : Any> isLoadingFinished(
        previousPageId: PreviousPageId<Id>,
        nextPageId: NextPageId<Id>
): Boolean = ((previousPageId == PreviousPageId.NoPage) && (nextPageId == NextPageId.NoPage))