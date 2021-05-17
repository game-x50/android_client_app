package com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination

import com.ruslan.hlushan.core.api.dto.pagination.NextPageId
import com.ruslan.hlushan.core.api.dto.pagination.PageId
import com.ruslan.hlushan.core.api.dto.pagination.PageRelation
import com.ruslan.hlushan.core.api.dto.pagination.PaginationPagesRequest
import com.ruslan.hlushan.core.api.dto.pagination.PreviousPageId
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem

sealed class PaginationState<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any> {

    abstract val filter: F

    sealed class Active<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any> : PaginationState<F, ItemId, RI, Id>() {

        abstract val additional: Additional

        sealed class Empty<out F : Any> : PaginationState.Active<F, Nothing, Nothing, Nothing>() {

            data class Default<out F : Any>(
                    override val filter: F
            ) : PaginationState.Active.Empty<F>() {
                override val additional: Additional.WaitingForLoadMore get() = Additional.WaitingForLoadMore
            }

            data class Loading<out F : Any>(
                    override val filter: F
            ) : PaginationState.Active.Empty<F>() {
                override val additional: Additional.Loading get() = Additional.Loading
            }

            @Suppress("DataClassPrivateConstructor", "ClassOrdering")
            data class WithError<out F : Any> private constructor(
                    override val filter: F,
                    override val additional: Additional.Error
            ) : PaginationState.Active.Empty<F>(), PaginationState.WithError {

                constructor(
                        filter: F,
                        error: Throwable
                ) : this(
                        filter = filter,
                        additional = Additional.Error(value = error, loadDirection = PaginationPagesRequest.Direction.NEXT)
                )
            }
        }

        sealed class PartiallyLoaded<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any>(
                previousPageId: PreviousPageId<Id>,
                currentPages: List<PageRelation<Id, ItemId>>
        ) : PaginationState.Active<F, ItemId, RI, Id>(), PaginationState.WithItems<ItemId, RI, Id> {

            abstract val previousPageId: PreviousPageId<Id>
            abstract val nextPageId: NextPageId<Id>

            init {
                when (previousPageId) {
                    is PreviousPageId.NoPage -> {
                        if (currentPages.first().pageId !is PageId.First) {
                            throw IllegalArgumentException("For first page pageId should be ${PageId.First}")
                        }
                    }
                    is PreviousPageId.Existing -> {
                        if (currentPages.first().pageId !is PageId.SecondOrMore) {
                            throw IllegalArgumentException("For not first page pageId should be ${PageId.SecondOrMore::class}")
                        }
                    }
                }

                if (currentPages.drop(1).any { pageRelation -> pageRelation.pageId is PageId.First }) {
                    throw IllegalArgumentException("Just first PageRelation can be ${PageId.First}")
                }
            }

            data class Default<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any>(
                    override val items: List<RI>,
                    override val filter: F,
                    override val currentPages: List<PageRelation<Id, ItemId>>,
                    override val previousPageId: PreviousPageId<Id>,
                    override val nextPageId: NextPageId<Id>
            ) : PaginationState.Active.PartiallyLoaded<F, ItemId, RI, Id>(
                    previousPageId = previousPageId,
                    currentPages = currentPages
            ) {
                override val additional: Additional.WaitingForLoadMore get() = Additional.WaitingForLoadMore
            }

            data class AndLoading<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any>(
                    override val items: List<RI>,
                    override val filter: F,
                    override val currentPages: List<PageRelation<Id, ItemId>>,
                    override val previousPageId: PreviousPageId<Id>,
                    override val nextPageId: NextPageId<Id>,
                    val direction: PaginationPagesRequest.Direction
            ) : PaginationState.Active.PartiallyLoaded<F, ItemId, RI, Id>(
                    previousPageId = previousPageId,
                    currentPages = currentPages
            ) {

                override val additional: Additional.Loading get() = Additional.Loading

                init {
                    this.checkPartiallyLoadedLoadingState(direction = direction)
                }
            }

            @Suppress("DataClassPrivateConstructor")
            data class WithError<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any> private constructor(
                    override val items: List<RI>,
                    override val filter: F,
                    override val currentPages: List<PageRelation<Id, ItemId>>,
                    override val previousPageId: PreviousPageId<Id>,
                    override val nextPageId: NextPageId<Id>,
                    override val additional: Additional.Error
            ) : PaginationState.Active.PartiallyLoaded<F, ItemId, RI, Id>(
                    previousPageId = previousPageId,
                    currentPages = currentPages
            ), PaginationState.WithError {

                @SuppressWarnings("LongParameterList")
                constructor(
                        items: List<RI>,
                        filter: F,
                        currentPages: List<PageRelation<Id, ItemId>>,
                        previousPageId: PreviousPageId<Id>,
                        nextPageId: NextPageId<Id>,
                        direction: PaginationPagesRequest.Direction,
                        error: Throwable
                ) : this(
                        items = items,
                        filter = filter,
                        currentPages = currentPages,
                        previousPageId = previousPageId,
                        nextPageId = nextPageId,
                        additional = Additional.Error(value = error, loadDirection = direction)
                ) {
                    this.checkPartiallyLoadedLoadingState(direction = direction)
                }
            }
        }
    }

    sealed class Finished<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any> : PaginationState<F, ItemId, RI, Id>() {

        data class WithResults<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any>(
                override val items: List<RI>,
                override val currentPages: List<PageRelation<Id, ItemId>>,
                override val filter: F
        ) : PaginationState.Finished<F, ItemId, RI, Id>(), PaginationState.WithItems<ItemId, RI, Id>

        data class Empty<out F : Any>(
                override val filter: F
        ) : PaginationState.Finished<F, Nothing, Nothing, Nothing>()
    }

    sealed class Additional {

        // object for equals and hashCode
        object Loading : Additional()

        // object for equals and hashCode
        object WaitingForLoadMore : Additional()

        data class Error(
                val value: Throwable,
                val loadDirection: PaginationPagesRequest.Direction
        ) : Additional()
    }

    interface WithError {
        val additional: Additional.Error
    }

    interface WithItems<out ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any> {
        val items: List<RI>
        val currentPages: List<PageRelation<Id, ItemId>>
    }
}

fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> PaginationState<F, ItemId, RI, Id>.itemsOrEmpty(): List<RI> =
        when (this) {
            is PaginationState.Active.Empty,
            is PaginationState.Finished.Empty -> emptyList()

            is PaginationState.Active.PartiallyLoaded -> this.items
            is PaginationState.Finished.WithResults -> this.items
        }

fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> PaginationState<F, ItemId, RI, Id>.itemsCount(): Int =
        itemsOrEmpty().size

fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any>
        PaginationState.Active.PartiallyLoaded<F, ItemId, RI, Id>.createPaginationRequestFor(
        direction: PaginationPagesRequest.Direction
): PaginationPagesRequest<Id>? {
    val statePreviousPageId = this.previousPageId
    val stateNextPageId = this.nextPageId
    return when {
        ((direction == PaginationPagesRequest.Direction.PREVIOUS) && (statePreviousPageId is PreviousPageId.Existing)) -> {
            PaginationPagesRequest.Previous(
                    firstLoadedPageId = (this.currentPages.first().pageId as PageId.SecondOrMore),
                    previousPageId = statePreviousPageId.value
            )
        }
        ((direction == PaginationPagesRequest.Direction.NEXT) && (stateNextPageId is NextPageId.Existing))             -> {
            PaginationPagesRequest.Next(
                    lastLoadedPageId = this.currentPages.last().pageId,
                    nextPageId = stateNextPageId
            )
        }
        else                                                                                                           -> {
            null
        }
    }
}

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any>
        PaginationState.Active.PartiallyLoaded<F, ItemId, RI, Id>.checkPartiallyLoadedLoadingState(
        direction: PaginationPagesRequest.Direction
) {
    if (this.createPaginationRequestFor(direction) == null) {
        throw IllegalArgumentException("No sense to load new items in direction that has no more items")
    }
}