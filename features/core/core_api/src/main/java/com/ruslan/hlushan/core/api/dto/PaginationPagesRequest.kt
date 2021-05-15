package com.ruslan.hlushan.core.api.dto

sealed class PaginationPagesRequest<out PageId : Any> {

    data class Previous<out PageId : Any>(
            val previousPageId: PreviousPageId<PageId>,
            val firstLoadedPageId: PageId?
    ) : PaginationPagesRequest<PageId>()

    data class Next<out PageId : Any>(
            val lastLoadedPageId: PageId?,
            val nextPageId: NextPageId<PageId>
    ) : PaginationPagesRequest<PageId>()

    enum class Direction {
        NEXT,
        PREVIOUS
    }
}