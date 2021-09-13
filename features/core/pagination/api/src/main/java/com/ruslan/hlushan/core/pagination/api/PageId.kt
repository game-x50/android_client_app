package com.ruslan.hlushan.core.pagination.api

sealed class PageId<out Id : Any> {

    // object for equals and hashCode
    object First : PageId<Nothing>()

    data class SecondOrMore<out Id : Any>(
            val value: Id
    ) : PageId<Id>()
}

sealed class PreviousPageId<out Id : Any> {

    data class Existing<out Id : Any>(
            val value: PageId<Id>
    ) : PreviousPageId<Id>()

    // object for equals and hashCode
    object NoPage : PreviousPageId<Nothing>()
}

sealed class NextPageId<out Id : Any> {

    data class Existing<out Id : Any>(
            val value: PageId.SecondOrMore<Id>
    ) : NextPageId<Id>()

    // object for equals and hashCode
    object NoPage : NextPageId<Nothing>()
}