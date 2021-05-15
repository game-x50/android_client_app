package com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination

import androidx.annotation.IntRange

private const val MIN_PAGINATION_LIMITS_VALUE = 1L

class PaginationLimits(
        @IntRange(from = MIN_PAGINATION_LIMITS_VALUE) val itemsOffsetToBorder: Int = 20,
        @IntRange(from = MIN_PAGINATION_LIMITS_VALUE) val maxStoredItemsCount: Int = 25
) {
    init {
        if ((itemsOffsetToBorder < MIN_PAGINATION_LIMITS_VALUE) || (maxStoredItemsCount < MIN_PAGINATION_LIMITS_VALUE)) {
            throw IllegalAccessException(
                    "itemsOffsetToBorder = $itemsOffsetToBorder and maxStoredItemsCount = $maxStoredItemsCount" +
                    " cant be less than $MIN_PAGINATION_LIMITS_VALUE"
            )
        }

        if (maxStoredItemsCount < itemsOffsetToBorder) {
            throw IllegalAccessException(
                    "maxStoredItemsCount = $maxStoredItemsCount cant be less than itemsOffsetToBorder = $itemsOffsetToBorder"
            )
        }
    }
}