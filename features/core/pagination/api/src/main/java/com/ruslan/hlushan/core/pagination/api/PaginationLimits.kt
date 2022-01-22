package com.ruslan.hlushan.core.pagination.api

import androidx.annotation.IntRange

private const val MIN_PAGINATION_LIMITS_VALUE = 1L

class PaginationLimits(
        @IntRange(from = MIN_PAGINATION_LIMITS_VALUE) val itemsOffsetToBorder: Int = 6,
        @IntRange(from = MIN_PAGINATION_LIMITS_VALUE) val maxStoredItemsCount: Int = 1_000
) {
    //TODO: #write_unit_tests
    init {
        if ((itemsOffsetToBorder < MIN_PAGINATION_LIMITS_VALUE)
            || (maxStoredItemsCount < MIN_PAGINATION_LIMITS_VALUE)) {
            throw IllegalAccessException(
                    "itemsOffsetToBorder = $itemsOffsetToBorder and maxStoredItemsCount = $maxStoredItemsCount" +
                    " cant be less than $MIN_PAGINATION_LIMITS_VALUE"
            )
        }

        if (maxStoredItemsCount < itemsOffsetToBorder) {
            throw IllegalAccessException(
                    "maxStoredItemsCount = $maxStoredItemsCount" +
                    " cant be less than itemsOffsetToBorder = $itemsOffsetToBorder"
            )
        }
    }
}