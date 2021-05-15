package com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination

import com.ruslan.hlushan.core.api.dto.FirstPagePreviousPageId
import com.ruslan.hlushan.core.api.dto.LastPageNextPageId
import com.ruslan.hlushan.core.api.dto.NextPageId
import com.ruslan.hlushan.core.api.dto.NoPageNextPageId
import com.ruslan.hlushan.core.api.dto.NoPagePreviousPageId
import com.ruslan.hlushan.core.api.dto.PaginationPagesRequest
import com.ruslan.hlushan.core.api.dto.PreviousPageId
import com.ruslan.hlushan.core.api.dto.SecondOrMorePagePreviousPageId
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem

sealed class PaginationState<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> {

    abstract val items: List<RI>

    abstract val filter: F

    abstract val currentPages: List<PageRelation<ItemId, PageId>>
    abstract val previousId: PreviousPageId<PageId>//todo: rename to previousPageId
    abstract val nextId: NextPageId<PageId>//todo: rename to nextPageId

    abstract val additional: Additional?

    data class Empty<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
            override val filter: F
    ) : PaginationState<F, ItemId, RI, PageId>() {
        override val items: List<RI> get() = emptyList()
        override val currentPages: List<PageRelation<ItemId, PageId>> get() = emptyList()
        override val previousId: PreviousPageId<PageId> get() = NoPagePreviousPageId
        override val nextId: NextPageId<PageId> get() = NoPageNextPageId
        override val additional: Additional? get() = null
    }

    data class EmptyLoading<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
            override val filter: F
    ) : PaginationState<F, ItemId, RI, PageId>() {
        override val items: List<RI> get() = emptyList()
        override val currentPages: List<PageRelation<ItemId, PageId>> get() = emptyList()
        override val previousId: PreviousPageId<PageId> get() = NoPagePreviousPageId
        override val nextId: NextPageId<PageId> get() = NoPageNextPageId
        override val additional: Additional = Additional.Loading()
    }

    @Suppress("DataClassPrivateConstructor", "ClassOrdering")
    data class EmptyWithError<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> private constructor(
            override val filter: F,
            override val additional: Additional.Error
    ) : PaginationState<F, ItemId, RI, PageId>() {

        constructor(
                filter: F,
                error: Throwable
        ) : this(
                filter = filter,
                additional = Additional.Error(value = error, loadDirection = PaginationPagesRequest.Direction.NEXT)
        )

        override val items: List<RI> get() = emptyList()
        override val currentPages: List<PageRelation<ItemId, PageId>> get() = emptyList()
        override val previousId: PreviousPageId<PageId> get() = NoPagePreviousPageId
        override val nextId: NextPageId<PageId> get() = NoPageNextPageId
    }

    data class PartiallyLoaded<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
            override val items: List<RI>,
            override val filter: F,
            override val currentPages: List<PageRelation<ItemId, PageId>>,
            override val previousId: PreviousPageId<PageId>,
            override val nextId: NextPageId<PageId>
    ) : PaginationState<F, ItemId, RI, PageId>() {

        override val additional: Additional? get() = null

//        companion object {
//            fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> withItemsBefore(
//                    items: List<RI>,
//                    filter: F,
//                    previousId: PageId?,
//                    currentPages: List<PageRelation<ItemId, PageId>>
//            ): PartiallyLoaded<F, ItemId, RI, PageId> =
//                    PartiallyLoaded(
//                            items = items,
//                            filter = filter,
//                            previousId = previousId,
//                            currentPages = currentPages,
//                            nextId = null
//                    )
//
//            fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> withItemsAfter(
//                    items: List<RI>,
//                    filter: F,
//                    currentPages: List<PageRelation<ItemId, PageId>>,
//                    nextId: PageId
//            ): PartiallyLoaded<F, ItemId, RI, PageId> =
//                    PartiallyLoaded(
//                            items = items,
//                            filter = filter,
//                            previousId = null,
//                            currentPages = currentPages,
//                            nextId = nextId
//                    )
//
//            fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> withItemsBeforeAndAfter(
//                    items: List<RI>,
//                    filter: F,
//                    previousId: PageId?,
//                    currentPages: List<PageRelation<ItemId, PageId>>,
//                    nextId: PageId
//            ): PartiallyLoaded<F, ItemId, RI, PageId> =
//                    PartiallyLoaded(
//                            items = items,
//                            filter = filter,
//                            previousId = previousId,
//                            currentPages = currentPages,
//                            nextId = nextId
//                    )
//        }
    }

    data class PartiallyLoadedAndLoading<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
            override val items: List<RI>,
            override val filter: F,
            override val currentPages: List<PageRelation<ItemId, PageId>>,
            override val previousId: PreviousPageId<PageId>,
            override val nextId: NextPageId<PageId>,
            val direction: PaginationPagesRequest.Direction
    ) : PaginationState<F, ItemId, RI, PageId>() {

        override val additional: Additional = Additional.Loading()

        init {
            this.checkPartiallyLoadedLoadingState(direction = direction)
        }
    }

    @Suppress("DataClassPrivateConstructor")
    data class PartiallyLoadedWithError<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> private constructor(
            override val items: List<RI>,
            override val filter: F,
            override val currentPages: List<PageRelation<ItemId, PageId>>,
            override val previousId: PreviousPageId<PageId>,
            override val nextId: NextPageId<PageId>,
            override val additional: Additional.Error
    ) : PaginationState<F, ItemId, RI, PageId>() {

        constructor(
                items: List<RI>,
                filter: F,
                currentPages: List<PageRelation<ItemId, PageId>>,
                previousId: PreviousPageId<PageId>,
                nextId: NextPageId<PageId>,
                direction: PaginationPagesRequest.Direction,
                error: Throwable
        ) : this(
                items = items,
                filter = filter,
                currentPages = currentPages,
                previousId = previousId,
                nextId = nextId,
                additional = Additional.Error(value = error, loadDirection = direction)
        ) {
            this.checkPartiallyLoadedLoadingState(direction = direction)
        }
    }

    data class AllLoaded<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
            override val items: List<RI>,
            override val filter: F,
            override val currentPages: List<PageRelation<ItemId, PageId>>
    ) : PaginationState<F, ItemId, RI, PageId>() {
        override val previousId: PreviousPageId<PageId> get() = FirstPagePreviousPageId
        override val nextId: NextPageId<PageId> get() = LastPageNextPageId
        override val additional: Additional.Empty? = if (items.isEmpty()) {
            Additional.Empty()
        } else {
            null
        }
    }

    sealed class Additional {

        //todo: redo to object and remove equals and hashCode
        class Loading : Additional()

        //todo: redo to object and remove equals and hashCode
        class Empty : Additional()

        data class Error(
                val value: Throwable,
                val loadDirection: PaginationPagesRequest.Direction
        ) : Additional()

        override fun equals(other: Any?): Boolean = (this.javaClass == other?.javaClass)
        override fun hashCode(): Int = this.javaClass.hashCode()
    }
}

fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> PaginationState<F, ItemId, RI, PageId>.hasSenseForLoading(
        direction: PaginationPagesRequest.Direction
) : Boolean =
        when(direction) {
            PaginationPagesRequest.Direction.PREVIOUS -> this.hasSenseForLoadingPrevious
            PaginationPagesRequest.Direction.NEXT     -> this.hasSenseForLoadingNext
        }

private val <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> PaginationState<F, ItemId, RI, PageId>.hasSenseForLoadingPrevious: Boolean
    get() = (this.previousId is SecondOrMorePagePreviousPageId)

private val <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> PaginationState<F, ItemId, RI, PageId>.hasSenseForLoadingNext: Boolean
    get() = (this.nextId !is LastPageNextPageId)

private fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> PaginationState<F, ItemId, RI, PageId>.checkPartiallyLoadedLoadingState(
        direction: PaginationPagesRequest.Direction
) {
    if (!this.hasSenseForLoading(direction)) {
        throw IllegalArgumentException("No sense to load new items in direction that has no more items")
    }
}