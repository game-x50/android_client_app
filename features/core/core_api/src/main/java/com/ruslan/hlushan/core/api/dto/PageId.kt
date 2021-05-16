package com.ruslan.hlushan.core.api.dto

sealed class PageId<out Id : Any> {

    // object for equals and hashCode
    object First : PageId<Nothing>()

    data class SecondOrMore<out Id : Any>(
            val value: Id
    ) : PageId<Id>()
}