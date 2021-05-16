package com.ruslan.hlushan.core.api.dto

sealed class PaginationPagesRequest<out Id : Any> {

    class Init : PaginationPagesRequest<Nothing>()

    data class Previous<out Id : Any>(
            val previousPageId: PageId<Id>,
            val firstLoadedPageId: Id
    ) : PaginationPagesRequest<Id>()

    data class Next<out Id : Any>(
            val lastLoadedPageId: PageId<Id>,
            val nextPageId: Id
    ) : PaginationPagesRequest<Id>()

    enum class Direction {
        NEXT,
        PREVIOUS
    }
}