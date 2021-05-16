package com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination

import com.ruslan.hlushan.core.api.dto.PageId

data class PageRelation<out Id : Any, out ItemId : Any>(
        val pageId: PageId<Id>,
        val itemsIds: List<ItemId>
)