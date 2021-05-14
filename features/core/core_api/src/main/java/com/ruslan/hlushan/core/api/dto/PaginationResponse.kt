package com.ruslan.hlushan.core.api.dto

sealed class PaginationResponse<out T : Any, out Id : Any> {

    abstract val result: List<T>
    abstract val previousId: Id?

    data class LastPage<out T : Any, out Id : Any >(
            override val result: List<T>,
            override val previousId: Id?
    ) : PaginationResponse<T, Id>()

    data class MiddlePage<out T : Any, out Id : Any>(
            override val result: List<T>,
            override val previousId: Id?,
            val nextId: Id
    ) : PaginationResponse<T, Id>()
}

val <T : Any, Id : Any> PaginationResponse<T, Id>.nextIdOrNull: Id?
    get() = when (this) {
            is PaginationResponse.MiddlePage -> this.nextId
            is PaginationResponse.LastPage -> null
        }

fun <T : Any, Id : Any, R : Any> PaginationResponse<T, Id>.map(transform: (T) -> R): PaginationResponse<R, Id> =
        when (this) {
            is PaginationResponse.MiddlePage -> PaginationResponse.MiddlePage(
                    result = this.result.map(transform),
                    previousId = this.previousId,
                    nextId = this.nextId
            )
            is PaginationResponse.LastPage   -> PaginationResponse.LastPage(
                    result = this.result.map(transform),
                    previousId = this.previousId
            )
        }