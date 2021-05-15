package com.ruslan.hlushan.core.api.dto

sealed class NextPageId<out PageId : Any>

// object for equals and hashCode
object NoPageNextPageId : NextPageId<Nothing>()

data class MiddlePageNextPageId<out PageId : Any>(
        val value: PageId
) : NextPageId<PageId>()

// object for equals and hashCode
object LastPageNextPageId : NextPageId<Nothing>()