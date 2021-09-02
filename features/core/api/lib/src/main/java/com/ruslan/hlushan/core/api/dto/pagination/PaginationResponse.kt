package com.ruslan.hlushan.core.api.dto.pagination

sealed class PaginationResponse<out T : Any, out Id : Any> {

    abstract val result: List<T>
    abstract val currentPageId: PageId<Id>

    //has pages after
    data class FirstPage<out T : Any, out Id : Any>(
            override val result: List<T>,
            val nextPageId: NextPageId.Existing<Id>
    ) : PaginationResponse<T, Id>() {
        override val currentPageId: PageId.First get() = PageId.First
    }

    //has pages before and after
    data class MiddlePage<out T : Any, out Id : Any>(
            override val result: List<T>,
            val previousPageId: PreviousPageId.Existing<Id>,
            override val currentPageId: PageId.SecondOrMore<Id>,
            val nextPageId: NextPageId.Existing<Id>
    ) : PaginationResponse<T, Id>()

    //has pages before
    data class LastPage<out T : Any, out Id : Any>(
            override val result: List<T>,
            val previousPageId: PreviousPageId.Existing<Id>,
            override val currentPageId: PageId.SecondOrMore<Id>
    ) : PaginationResponse<T, Id>()

    data class SinglePage<out T : Any, out Id : Any>(
            override val result: List<T>
    ) : PaginationResponse<T, Id>() {
        override val currentPageId: PageId.First get() = PageId.First
    }
}

fun <T : Any, Id : Any, R : Any> PaginationResponse<T, Id>.map(transform: (T) -> R): PaginationResponse<R, Id> {
    val transformedResult = this.result.map(transform)

    return when (this) {
        is PaginationResponse.FirstPage -> PaginationResponse.FirstPage(
                result = transformedResult,
                nextPageId = this.nextPageId
        )
        is PaginationResponse.MiddlePage -> PaginationResponse.MiddlePage(
                result = transformedResult,
                previousPageId = this.previousPageId,
                currentPageId = this.currentPageId,
                nextPageId = this.nextPageId
        )
        is PaginationResponse.LastPage -> PaginationResponse.LastPage(
                result = transformedResult,
                previousPageId = this.previousPageId,
                currentPageId = this.currentPageId,
        )
        is PaginationResponse.SinglePage -> PaginationResponse.SinglePage(
                result = transformedResult,
        )
    }
}

//TODO: #write_unit_tests
@SuppressWarnings("LongParameterList")
fun <Id : Any, RequestId : Id?, Item : Any> createPaginationResponseByLimits(
        pageResult: List<Item>,
        pagesRequest: PaginationPagesRequest<Id>,
        requestPageId: RequestId,
        limit: Int,
        createNextPageIdFor: (RequestId, List<Item>) -> NextPageId.Existing<Id>,
        createPreviousPageIdFor: (RequestId, List<Item>) -> PreviousPageId.Existing<Id>
): PaginationResponse<Item, Id> =
        when (pagesRequest) {
            is PaginationPagesRequest.Init -> {
                if (isLastOrFirstPage(pageResult, limit)) {
                    PaginationResponse.SinglePage(result = pageResult)
                } else {
                    PaginationResponse.FirstPage(
                            result = pageResult,
                            nextPageId = createNextPageIdFor(requestPageId, pageResult)
                    )
                }
            }
            is PaginationPagesRequest.Next -> {
                val currentPageId = PageId.SecondOrMore(requestPageId!!)
                val previousPageId = PreviousPageId.Existing(pagesRequest.lastLoadedPageId)
                if (isLastOrFirstPage(pageResult, limit)) {
                    PaginationResponse.LastPage(
                            result = pageResult,
                            previousPageId = previousPageId,
                            currentPageId = currentPageId
                    )
                } else {
                    PaginationResponse.MiddlePage(
                            result = pageResult,
                            previousPageId = previousPageId,
                            currentPageId = currentPageId,
                            nextPageId = createNextPageIdFor(requestPageId, pageResult)
                    )
                }
            }
            is PaginationPagesRequest.Previous -> {
                val nextPageId = NextPageId.Existing(pagesRequest.firstLoadedPageId)

                if (isLastOrFirstPage(pageResult, limit)) {
                    PaginationResponse.FirstPage(
                            result = pageResult,
                            nextPageId = nextPageId
                    )
                } else {
                    PaginationResponse.MiddlePage(
                            result = pageResult,
                            previousPageId = createPreviousPageIdFor(requestPageId, pageResult),
                            currentPageId = PageId.SecondOrMore(requestPageId!!),
                            nextPageId = nextPageId
                    )
                }
            }
        }

private fun isLastOrFirstPage(pageResult: List<*>, limit: Int): Boolean =
        (pageResult.size < limit)