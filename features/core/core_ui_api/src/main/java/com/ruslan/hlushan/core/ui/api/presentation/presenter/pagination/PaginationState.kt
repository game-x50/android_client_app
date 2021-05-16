package com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination

import com.ruslan.hlushan.core.api.dto.NextPageId
import com.ruslan.hlushan.core.api.dto.PageId
import com.ruslan.hlushan.core.api.dto.PageRelationsList
import com.ruslan.hlushan.core.api.dto.PaginationPagesRequest
import com.ruslan.hlushan.core.api.dto.PreviousPageId
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem

sealed class PaginationState<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any> {

    abstract val filter: F

    sealed class Active<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any>
        : PaginationState<F, ItemId, RI, Id>() {

        abstract val additional: Additional

        sealed class Empty<out F : Any> : PaginationState.Active<F, Nothing, Nothing, Nothing>() {

            data class Default<out F : Any>(
                    override val filter: F
            ) : PaginationState.Active.Empty<F>() {
                override val additional: Additional.WaitingForLoadMore = Additional.WaitingForLoadMore()
            }

            data class Loading<out F : Any>(
                    override val filter: F
            ) : PaginationState.Active.Empty<F>() {
                override val additional: Additional.Loading = Additional.Loading()
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

        sealed class PartiallyLoaded<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any>
            : PaginationState.Active<F, ItemId, RI, Id>(), PaginationState.WithItems<ItemId, RI, Id> {

            abstract val nextPageId: Id?

            data class Default<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any>(
                    override val items: List<RI>,
                    override val filter: F,
                    override val pageRelationsList: PageRelationsList<Id, ItemId>,
                    override val nextPageId: Id?
            ) : PaginationState.Active.PartiallyLoaded<F, ItemId, RI, Id>() {
                override val additional: Additional.WaitingForLoadMore = Additional.WaitingForLoadMore()
            }

            data class AndLoading<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any>(
                    override val items: List<RI>,
                    override val filter: F,
                    override val pageRelationsList: PageRelationsList<Id, ItemId>,
                    override val nextPageId: Id?,
                    val direction: PaginationPagesRequest.Direction
            ) : PaginationState.Active.PartiallyLoaded<F, ItemId, RI, Id>() {

                override val additional: Additional.Loading = Additional.Loading()

                init {
                    this.checkPartiallyLoadedLoadingState(direction = direction)
                }
            }

            @Suppress("DataClassPrivateConstructor")
            data class WithError<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any> private constructor(
                    override val items: List<RI>,
                    override val filter: F,
                    override val pageRelationsList: PageRelationsList<Id, ItemId>,
                    override val nextPageId: Id?,
                    override val additional: Additional.Error
            ) : PaginationState.Active.PartiallyLoaded<F, ItemId, RI, Id>(), PaginationState.WithError {

                constructor(
                        items: List<RI>,
                        filter: F,
                        pageRelationsList: PageRelationsList<Id, ItemId>,
                        nextPageId: Id?,
                        direction: PaginationPagesRequest.Direction,
                        error: Throwable
                ) : this(
                        items = items,
                        filter = filter,
                        pageRelationsList = pageRelationsList,
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
                override val pageRelationsList: PageRelationsList.IncludingFirst<Id, ItemId>,
                override val filter: F
        ) : PaginationState.Finished<F, ItemId, RI, Id>(), PaginationState.WithItems<ItemId, RI, Id>

        data class Empty<out F : Any>(
                override val filter: F
        ) : PaginationState.Finished<F, Nothing, Nothing, Nothing>()
    }

    sealed class Additional {

        //todo: redo to object and remove equals and hashCode; redo get()= in states above
        class Loading : Additional()

        //todo: redo to object and remove equals and hashCode; redo get()= in states above
        class WaitingForLoadMore : Additional()

        data class Error(
                val value: Throwable,
                val loadDirection: PaginationPagesRequest.Direction
        ) : Additional()

        override fun equals(other: Any?): Boolean = (this.javaClass == other?.javaClass)
        override fun hashCode(): Int = this.javaClass.hashCode()
    }

    interface WithError {
        val additional: Additional.Error
    }

    interface WithItems<out ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any> {
        val items: List<RI>
        val pageRelationsList: PageRelationsList<Id, ItemId>
    }
}

val <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any> PaginationState<F, ItemId, RI, Id>.itemsCount: Int
    get() = when (this) {
        is PaginationState.Active.Empty,
        is PaginationState.Finished.Empty -> 0

        is PaginationState.Active.PartiallyLoaded -> this.items.size
        is PaginationState.Finished.WithResults -> this.items.size
    }

fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any>
        PaginationState.Active.PartiallyLoaded<F, ItemId, RI, Id>.createPaginationRequestFor(
        direction: PaginationPagesRequest.Direction
): PaginationPagesRequest<Id>? {
    val statePreviousPageId = this.previousPageId
    val stateNextPageId = this.nextPageId
    return when {
        ((direction == PaginationPagesRequest.Direction.PREVIOUS) && (statePreviousPageId is PreviousPageId.Existing)) -> {
            PaginationPagesRequest.Previous(
                    firstLoadedPageId = (this.currentPages.first().pageId as PageId.SecondOrMore).value,
                    previousPageId = statePreviousPageId.value
            )
        }
        ((direction == PaginationPagesRequest.Direction.NEXT) && (stateNextPageId is NextPageId.Existing))             -> {
            PaginationPagesRequest.Next(
                    lastLoadedPageId = this.currentPages.last().pageId,
                    nextPageId = stateNextPageId.value.value
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
    if (this.createPaginationRequestFor(direction) != null) {
        throw IllegalArgumentException("No sense to load new items in direction that has no more items")
    }
}