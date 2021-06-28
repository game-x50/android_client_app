package com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination

import com.ruslan.hlushan.core.api.dto.pagination.PaginationPagesRequest
import com.ruslan.hlushan.core.api.dto.pagination.PaginationResponse
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem

internal sealed class PaginationAction<out F : Any, out ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any> {

    sealed class UI<out F : Any> : PaginationAction<F, Nothing, Nothing, Nothing>() {

        data class LoadMore(
                val direction: PaginationPagesRequest.Direction
        ) : UI<Nothing>()

        data class Refresh<out F : Any>(
                val filter: F
        ) : UI<F>()
    }

    sealed class Response<
            out ItemId : Any,
            out RI : RecyclerItem<ItemId>,
            out Id : Any
            > : PaginationAction<Nothing, ItemId, RI, Id>() {

        class Success<out ItemId : Any, out RI : RecyclerItem<ItemId>, out Id : Any>(
                val response: PaginationResponse<RI, Id>
        ) : Response<ItemId, RI, Id>()

        class Error(
                val error: Throwable
        ) : Response<Nothing, Nothing, Nothing>()
    }

    sealed class Change<
            out ItemId : Any,
            out RI : RecyclerItem<ItemId>
            > : PaginationAction<Nothing, ItemId, RI, Nothing>() {

        class SingleItemUpdated<out ItemId : Any, out RI : RecyclerItem<ItemId>>(
                val updatedItem: RI,
                val notifyStateUpdated: Boolean
        ) : Change<ItemId, RI>()

        class SingleItemDeleted<out ItemId : Any>(
                val deletedItemId: ItemId
        ) : Change<ItemId, Nothing>()
    }
}