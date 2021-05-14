package com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination

data class PageRelation<out ItemId : Any, out PageId : Any>(
        val pageId: PageId?,
        val itemsIds: List<ItemId>
)