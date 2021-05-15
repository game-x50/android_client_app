package com.ruslan.hlushan.core.api.dto

sealed class PaginationResponse<out T : Any, out PageId : Any> {

    abstract val result: List<T>
    abstract val previousId: PreviousPageId<PageId>//todo: rename to previousPageId
    abstract val currentId: PageId?//todo: rename to currentPageId

    data class LastPage<out T : Any, out PageId : Any >(
            override val result: List<T>,
            override val previousId: PreviousPageId<PageId>,
            override val currentId: PageId?
    ) : PaginationResponse<T, PageId>()

    data class MiddlePage<out T : Any, out PageId : Any>(
            override val result: List<T>,
            override val previousId: PreviousPageId<PageId>,
            override val currentId: PageId?,
            val nextId: PageId
    ) : PaginationResponse<T, PageId>()
}

fun <T : Any, PageId : Any, R : Any> PaginationResponse<T, PageId>.map(transform: (T) -> R): PaginationResponse<R, PageId> =
        when (this) {
            is PaginationResponse.MiddlePage -> PaginationResponse.MiddlePage(
                    result = this.result.map(transform),
                    previousId = this.previousId,
                    currentId = this.currentId,
                    nextId = this.nextId
            )
            is PaginationResponse.LastPage   -> PaginationResponse.LastPage(
                    result = this.result.map(transform),
                    currentId = this.currentId,
                    previousId = this.previousId
            )
        }