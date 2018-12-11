package com.ruslan.hlushan.core.api.dto

sealed class PaginationResponse<out T : Any, out Id : Any> {

    @SuppressWarnings("ClassOrdering")
    companion object {
        fun <T : Any, Id : Any> create(result: List<T>, nextId: Id?): PaginationResponse<T, Id> =
                if (nextId != null) {
                    MiddlePage<T, Id>(result, nextId)
                } else {
                    LastPage<T, Id>(result)
                }
    }

    abstract val result: List<T>
    abstract val nextId: Id?

    data class LastPage<out T : Any, out Id : Any>(
            override val result: List<T>
    ) : PaginationResponse<T, Id>() {
        override val nextId: Id? get() = null
    }

    data class MiddlePage<out T : Any, out Id : Any>(
            override val result: List<T>,
            override val nextId: Id
    ) : PaginationResponse<T, Id>()
}

fun <T : Any, Id : Any, R : Any> PaginationResponse<T, Id>.map(transform: (T) -> R): PaginationResponse<R, Id> =
        PaginationResponse.create<R, Id>(
                result = this.result.map(transform),
                nextId = this.nextId
        )

fun <T : Any, Id : Any, R : Any> PaginationResponse<T, Id>.mapNotNull(transform: (T) -> R?): PaginationResponse<R, Id> =
        PaginationResponse.create<R, Id>(
                result = this.result.mapNotNull(transform),
                nextId = this.nextId
        )