package com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination

import com.ruslan.hlushan.core.api.dto.PaginationPagesRequest

internal sealed class PaginationSideEffect<out PageId : Any> {

    class LoadMore<PageId : Any>(
            val paginationPagesRequest: PaginationPagesRequest<PageId>
    ) : PaginationSideEffect<PageId>()

    class AvoidNotifyStateUpdated<PageId : Any> : PaginationSideEffect<PageId>()
}