package com.ruslan.hlushan.core.pagination.api

sealed class PaginationSideEffect<out Id : Any> {

    class LoadMore<Id : Any>(
            val pagesRequest: PaginationPagesRequest<Id>
    ) : PaginationSideEffect<Id>()

    class AvoidNotifyStateUpdated : PaginationSideEffect<Nothing>()
}