package com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination

internal sealed class PaginationSideEffect<out PageId : Any> {

    class LoadMore<PageId : Any>(
            val direction: PaginationState.LoadDirection,
            val pageId: PageId?
    ) : PaginationSideEffect<PageId>()

    class AvoidNotifyStateUpdated<PageId : Any> : PaginationSideEffect<PageId>()
}