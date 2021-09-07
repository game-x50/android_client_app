package com.ruslan.hlushan.core.ui.api.presentation.viewmodel.pagination

import com.ruslan.hlushan.core.api.dto.pagination.PaginationPagesRequest

internal sealed class PaginationSideEffect<out Id : Any> {

    class LoadMore<Id : Any>(
            val pagesRequest: PaginationPagesRequest<Id>
    ) : PaginationSideEffect<Id>()

    class AvoidNotifyStateUpdated : PaginationSideEffect<Nothing>()
}