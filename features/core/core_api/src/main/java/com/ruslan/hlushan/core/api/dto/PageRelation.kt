package com.ruslan.hlushan.core.api.dto

data class PageRelation<out Id : Any, out ItemId : Any, out PI : PageId<Id>>(
        val pageId: PI,
        val itemsIds: List<ItemId>
)

sealed class PageRelationsList<out Id : Any, out ItemId : Any> {

    data class IncludingFirst<out Id : Any, out ItemId : Any>(
            val firstPage: PageRelation<Id, ItemId, PageId.First>,
            val secondAndMorePages: List<PageRelation<Id, ItemId, PageId.SecondOrMore<Id>>>
    ): PageRelationsList<Id, ItemId>()

    data class WithoutFirst<out Id : Any, out ItemId : Any>(
            val previousPageId: PageId<Id>,
             val secondAndMorePages: List<PageRelation<Id, ItemId, PageId.SecondOrMore<Id>>>
    ): PageRelationsList<Id, ItemId>()
}

fun <Id : Any, ItemId : Any, PRL : PageRelationsList<Id, ItemId>> PRL.removeItemId(
        itemId: ItemId
): PRL = PageRelationsList.IncludingFirst(
        firstPage = this,
        secondAndMorePages = pageRelationsList.secondAndMorePages
)

operator fun <Id : Any, ItemId : Any, PRL : PageRelationsList<Id, ItemId>> PRL.plus(
        newPageRelation: PageRelation<Id, ItemId, PageId.SecondOrMore<Id>>
): PRL = PageRelationsList.IncludingFirst(
        firstPage = this,
        secondAndMorePages = pageRelationsList.secondAndMorePages
)