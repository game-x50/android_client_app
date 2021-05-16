package com.ruslan.hlushan.core.api.dto

sealed class PaginationResponse<out T : Any, out Id : Any> {

    abstract val result: List<T>
    abstract val currentPageId: PageId<Id>

    //has pages after
    data class FirstPage<out T : Any, out Id : Any>(
            override val result: List<T>,
            val nextPageId: NextPageId.Existing<Id>
    ) : PaginationResponse<T, Id>() {
        override val currentPageId: PageId<Id> get() = PageId.First
    }

    //has pages before and after
    data class MiddlePage<out T : Any, out Id : Any> private constructor(
            override val result: List<T>,
            val previousPageId: PreviousPageId.Existing<Id>,
            override val currentPageId: PageId.SecondOrMore<Id>,
            val nextPageId: NextPageId.Existing<Id>
    ) : PaginationResponse<T, Id>() {

        constructor(
                result: List<T>,
                previousPageId: PreviousPageId.Existing<Id>,
                currentPageId: Id,
                nextPageId: NextPageId.Existing<Id>
        ) : this(
                result = result,
                previousPageId = previousPageId,
                currentPageId = PageId.SecondOrMore(value = currentPageId),
                nextPageId = nextPageId
        )
    }

    //has pages before
    data class LastPage<out T : Any, out Id : Any> private constructor(
            override val result: List<T>,
            val previousPageId: PreviousPageId.Existing<Id>,
            override val currentPageId: PageId.SecondOrMore<Id>
    ) : PaginationResponse<T, Id>() {

        constructor(
                result: List<T>,
                previousPageId: PreviousPageId.Existing<Id>,
                currentPageId: Id
        ) : this(
                result = result,
                previousPageId = previousPageId,
                currentPageId = PageId.SecondOrMore(value = currentPageId)
        )
    }

    data class SinglePage<out T : Any, out Id : Any>(
            override val result: List<T>
    ) : PaginationResponse<T, Id>() {
        override val currentPageId: PageId<Id> get() = PageId.First
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
                currentPageId = this.currentPageId.value,
                nextPageId = this.nextPageId
        )
        is PaginationResponse.LastPage -> PaginationResponse.LastPage(
                result = transformedResult,
                previousPageId = this.previousPageId,
                currentPageId = this.currentPageId.value,
        )
        is PaginationResponse.SinglePage -> PaginationResponse.SinglePage(
                result = transformedResult,
        )
    }
}