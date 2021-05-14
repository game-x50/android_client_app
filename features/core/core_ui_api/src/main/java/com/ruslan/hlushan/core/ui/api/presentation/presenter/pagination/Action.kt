package com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination

import com.ruslan.hlushan.core.api.dto.PaginationResponse
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem

//todo: rename to PaginationAction
internal sealed class Action<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out PageId : Any> {

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