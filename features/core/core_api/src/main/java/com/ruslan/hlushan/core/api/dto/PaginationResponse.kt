package com.ruslan.hlushan.core.api.dto

data class PaginationResponse<out T : Any, out Id : Any>(
        val result: List<T>,
        val previousId: Id?,
        val nextId: Id?
)

val <T : Any, Id : Any> PaginationResponse<T, Id>.isFirstPage: Boolean get() = (this.previousId == null)

val <T : Any, Id : Any> PaginationResponse<T, Id>.isLastPage: Boolean get() = (this.nextId == null)