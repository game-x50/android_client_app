package com.ruslan.hlushan.core.ui.api.presentation.presenter

import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem

sealed class PaginationState<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> {

    abstract val items: List<RI>
    abstract val filter: F
    abstract val nextId: PageId?

    abstract val additional: Additional?

    data class Empty<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
            override val filter: F
    ) : PaginationState<F, ItemId, RI, PageId>() {
        override val items: List<RI> get() = emptyList()
        override val nextId: PageId? get() = null
        override val additional: Additional? get() = null
    }

    data class EmptyLoading<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
            override val filter: F
    ) : PaginationState<F, ItemId, RI, PageId>() {
        override val items: List<RI> get() = emptyList()
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
        ) : this(filter, Additional.Error(error))

        override val items: List<RI> get() = emptyList()
        override val nextId: PageId? get() = null
    }

    data class PartiallyLoaded<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
            override val items: List<RI>,
            override val filter: F,
            override val nextId: PageId
    ) : PaginationState<F, ItemId, RI, PageId>() {
        override val additional: Additional? get() = null
    }

    data class PartiallyLoadedAndLoading<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
            override val items: List<RI>,
            override val filter: F,
            override val nextId: PageId
    ) : PaginationState<F, ItemId, RI, PageId>() {
        override val additional: Additional = Additional.Loading()
    }

    @Suppress("DataClassPrivateConstructor")
    data class PartiallyLoadedWithError<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> private constructor(
            override val items: List<RI>,
            override val filter: F,
            override val nextId: PageId,
            override val additional: Additional.Error
    ) : PaginationState<F, ItemId, RI, PageId>() {

        constructor(
                items: List<RI>,
                filter: F,
                nextId: PageId,
                error: Throwable
        ) : this(items, filter, nextId, Additional.Error(error))
    }

    data class AllLoaded<out F : Any, ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any>(
            override val items: List<RI>,
            override val filter: F
    ) : PaginationState<F, ItemId, RI, PageId>() {
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
        data class Error(val value: Throwable) : Additional()

        override fun equals(other: Any?): Boolean = (this.javaClass == other?.javaClass)
        override fun hashCode(): Int = this.javaClass.hashCode()
    }
}