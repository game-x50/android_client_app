package com.ruslan.hlushan.core.api.dto

sealed class PreviousPageId<out PageId : Any>

// object for equals and hashCode
object NoPagePreviousPageId : PreviousPageId<Nothing>()

// object for equals and hashCode
object FirstPagePreviousPageId : PreviousPageId<Nothing>()

data class SecondOrMorePagePreviousPageId<out PageId : Any>(
        val value: PageId
) : PreviousPageId<PageId>()