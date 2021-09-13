package com.ruslan.hlushan.core.pagination.api

data class PageRelation<out Id : Any, out ItemId : Any>(
        val pageId: PageId<Id>,
        val itemsIds: List<ItemId>
)