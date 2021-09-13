package com.ruslan.hlushan.core.pagination.api

sealed class PaginationPagesRequest<out Id : Any> {

    class Init : PaginationPagesRequest<Nothing>()

    data class Previous<out Id : Any>(
            val previousPageId: PageId<Id>,
            val firstLoadedPageId: PageId.SecondOrMore<Id>
    ) : PaginationPagesRequest<Id>()

    data class Next<out Id : Any>(
            val lastLoadedPageId: PageId<Id>,
            val nextPageId: NextPageId.Existing<Id>
    ) : PaginationPagesRequest<Id>()

    enum class Direction {
        NEXT,
        PREVIOUS
    }
}