package com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination

import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem

sealed class PaginationState<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> {

    abstract val items: List<RI>

    abstract val filter: F

    abstract val currentPages: List<PageRelation<ItemId, PageId>>
    abstract val previousId: PageId?
    abstract val nextId: PageId?

    abstract val additional: Additional?

    data class Empty<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
            override val filter: F
    ) : PaginationState<F, ItemId, RI, PageId>() {
        override val items: List<RI> get() = emptyList()
        override val currentPages: List<PageRelation<ItemId, PageId>> get() = emptyList()
        override val previousId: PageId? get() = null
        override val nextId: PageId? get() = null
        override val additional: Additional? get() = null
    }

    data class EmptyLoading<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
            override val filter: F
    ) : PaginationState<F, ItemId, RI, PageId>() {
        override val items: List<RI> get() = emptyList()
        override val currentPages: List<PageRelation<ItemId, PageId>> get() = emptyList()
        override val previousId: PageId? get() = null
        override val nextId: PageId? get() = null
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
                additional = Additional.Error(value = error, direction = LoadDirection.NEXT)
        )

        override val items: List<RI> get() = emptyList()
        override val currentPages: List<PageRelation<ItemId, PageId>> get() = emptyList()
        override val previousId: PageId? get() = null
        override val nextId: PageId? get() = null
    }

    data class PartiallyLoaded<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> private constructor(
            override val items: List<RI>,
            override val filter: F,
            override val currentPages: List<PageRelation<ItemId, PageId>>,
            override val previousId: PageId?,
            override val nextId: PageId?,
            val hasItemsBefore: Boolean
    ) : PaginationState<F, ItemId, RI, PageId>() {

        override val additional: Additional? get() = null

        companion object {
            fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> withItemsBefore(
                    items: List<RI>,
                    filter: F,
                    previousId: PageId?,
                    currentPages: List<PageRelation<ItemId, PageId>>
            ): PartiallyLoaded<F, ItemId, RI, PageId> =
                    PartiallyLoaded(
                            items = items,
                            filter = filter,
                            previousId = previousId,
                            currentPages = currentPages,
                            nextId = null,
                            hasItemsBefore = true
                    )

            fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> withItemsAfter(
                    items: List<RI>,
                    filter: F,
                    currentPages: List<PageRelation<ItemId, PageId>>,
                    nextId: PageId
            ): PartiallyLoaded<F, ItemId, RI, PageId> =
                    PartiallyLoaded(
                            items = items,
                            filter = filter,
                            previousId = null,
                            currentPages = currentPages,
                            nextId = nextId,
                            hasItemsBefore = false
                    )

            fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> withItemsBeforeAndAfter(
                    items: List<RI>,
                    filter: F,
                    previousId: PageId?,
                    currentPages: List<PageRelation<ItemId, PageId>>,
                    nextId: PageId
            ): PartiallyLoaded<F, ItemId, RI, PageId> =
                    PartiallyLoaded(
                            items = items,
                            filter = filter,
                            previousId = previousId,
                            currentPages = currentPages,
                            nextId = nextId,
                            hasItemsBefore = true
                    )
        }
    }

    data class PartiallyLoadedAndLoading<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
            override val items: List<RI>,
            override val filter: F,
            override val currentPages: List<PageRelation<ItemId, PageId>>,
            override val previousId: PageId?,
            override val nextId: PageId?,
            val direction: LoadDirection,
            val hasItemsBefore: Boolean
    ) : PaginationState<F, ItemId, RI, PageId>() {

        override val additional: Additional = Additional.Loading()

        init {
            checkPartiallyLoadedLoadingState(hasItemsBefore = hasItemsBefore, hasItemsAfter = hasItemsAfter, direction = direction)
        }
    }

    @Suppress("DataClassPrivateConstructor")
    data class PartiallyLoadedWithError<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> private constructor(
            override val items: List<RI>,
            override val filter: F,
            override val currentPages: List<PageRelation<ItemId, PageId>>,
            override val previousId: PageId?,
            override val nextId: PageId?,
            override val additional: Additional.Error,
            val hasItemsBefore: Boolean
    ) : PaginationState<F, ItemId, RI, PageId>() {

        constructor(
                items: List<RI>,
                filter: F,
                currentPages: List<PageRelation<ItemId, PageId>>,
                previousId: PageId?,
                nextId: PageId?,
                direction: LoadDirection,
                error: Throwable,
                hasItemsBefore: Boolean
        ) : this(
                items = items,
                filter = filter,
                currentPages = currentPages,
                previousId = previousId,
                nextId = nextId,
                additional = Additional.Error(value = error, direction = direction),
                hasItemsBefore = hasItemsBefore
        ) {
            checkPartiallyLoadedLoadingState(hasItemsBefore = hasItemsBefore, hasItemsAfter = hasItemsAfter, direction = direction)
        }
    }

    data class AllLoaded<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
            override val items: List<RI>,
            override val filter: F,
            override val currentPages: List<PageRelation<ItemId, PageId>>
    ) : PaginationState<F, ItemId, RI, PageId>() {
        override val previousId: PageId? get() = null
        override val nextId: PageId? get() = null
        override val additional: Additional.Empty? = if (items.isEmpty()) {
            Additional.Empty()
        } else {
            null
        }
    }

    sealed class Additional {

        class Loading : Additional()

        class Empty : Additional()

        data class Error(
                val value: Throwable,
                val direction: LoadDirection
        ) : Additional()

        override fun equals(other: Any?): Boolean = (this.javaClass == other?.javaClass)
        override fun hashCode(): Int = this.javaClass.hashCode()
    }

    enum class LoadDirection {
        NEXT,
        PREVIOUS
    }
}

fun <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> PaginationState<F, ItemId, RI, PageId>.pageIdFor(
        direction: PaginationState.LoadDirection
): PageId? = when (direction) {
    PaginationState.LoadDirection.NEXT     -> this.nextId
    PaginationState.LoadDirection.PREVIOUS -> this.previousId
}

val <F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any> PaginationState<F, ItemId, RI, PageId>.hasItemsAfter: Boolean
    get() = (this.nextId != null)

private fun checkPartiallyLoadedLoadingState(
        hasItemsBefore: Boolean,
        hasItemsAfter: Boolean,
        direction: PaginationState.LoadDirection
) {
    if ((!hasItemsBefore && (direction == PaginationState.LoadDirection.PREVIOUS))
        || (!hasItemsAfter && (direction == PaginationState.LoadDirection.NEXT))) {
        throw IllegalArgumentException("No sense to load new items in direction that has no more items")
    }
}