package com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination

import com.ruslan.hlushan.core.api.dto.PaginationPagesRequest

internal sealed class PaginationSideEffect<out Id : Any> {

    class LoadMore<Id : Any>(
            val paginationPagesRequest: PaginationPagesRequest<Id>
    ) : PaginationSideEffect<Id>()

    class AvoidNotifyStateUpdated : PaginationSideEffect<Nothing>()
}