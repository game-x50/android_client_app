package com.ruslan.hlushan.game.core.api.play.dto

import com.ruslan.hlushan.core.api.dto.PaginationResponse
import org.threeten.bp.Instant

/**
 * @author Ruslan Hlushan on 2019-07-31
 */
sealed class RequestParams {

    abstract val excludedIds: List<Long>

    sealed class OrderTotalSum : RequestParams() {

        abstract val boundarySum: Int?

        class Asc(
                override val excludedIds: List<Long>,
                val minTotalSum: Int?
        ) : RequestParams.OrderTotalSum() {

            override val boundarySum: Int? get() = minTotalSum
        }

        class Desc(
                override val excludedIds: List<Long>,
                val maxTotalSum: Int?
        ) : RequestParams.OrderTotalSum() {

            override val boundarySum: Int? get() = maxTotalSum
        }
    }

    sealed class OrderLastModified : RequestParams() {

        abstract val boundaryTimestamp: Instant?

        class Asc(
                override val excludedIds: List<Long>,
                val minLastModifiedTimestamp: Instant?
        ) : RequestParams.OrderLastModified() {

            override val boundaryTimestamp: Instant? get() = minLastModifiedTimestamp
        }

        class Desc(
                override val excludedIds: List<Long>,
                val maxLastModifiedTimestamp: Instant?
        ) : RequestParams.OrderLastModified() {

            override val boundaryTimestamp: Instant? get() = maxLastModifiedTimestamp
        }
    }
}

//TODO: #write_unit_tests
fun combineToRequestParams(
        nextId: RequestParams?,
        orderParams: GameRecordWithSyncState.Order.Params
): RequestParams =
        (nextId ?: createOrderedInitParams(orderParams))

//TODO: #write_unit_tests
fun createPaginationResponseFor(
        pageResult: List<GameRecordWithSyncState>,
        previousRequestParams: RequestParams,
        limit: Int
): PaginationResponse<GameRecordWithSyncState, RequestParams> =
        if (pageResult.size < limit) {
            PaginationResponse.LastPage(result = pageResult)
        } else {
            val nextParams = when (previousRequestParams) {
                is RequestParams.OrderTotalSum     -> createPaginationResponseForOrderTotalSum(pageResult, previousRequestParams)
                is RequestParams.OrderLastModified -> createPaginationResponseForOrderLastModified(pageResult, previousRequestParams)
            }
            PaginationResponse.MiddlePage(
                    result = pageResult,
                    nextId = nextParams
            )
        }

private fun createOrderedInitParams(orderParams: GameRecordWithSyncState.Order.Params) =
        when (orderParams.variant) {
            GameRecordWithSyncState.Order.Variant.TOTAL_SUM               -> {
                createOrderedByTotalSumRequestInitParams(orderParams.type)
            }
            GameRecordWithSyncState.Order.Variant.LAST_MODIFIED_TIMESTAMP -> {
                createOrderedByLastModifiedRequestInitParams(orderParams.type)
            }
        }

private fun createOrderedByTotalSumRequestInitParams(
        orderType: OrderType
): RequestParams.OrderTotalSum {

    val excludedIds = emptyList<Long>()
    val boundarySum: Int? = null

    return when (orderType) {
        OrderType.ASC  -> RequestParams.OrderTotalSum.Asc(
                excludedIds = excludedIds,
                minTotalSum = boundarySum
        )
        OrderType.DESC -> RequestParams.OrderTotalSum.Desc(
                excludedIds = excludedIds,
                maxTotalSum = boundarySum
        )
    }
}

private fun createOrderedByLastModifiedRequestInitParams(
        orderType: OrderType
): RequestParams.OrderLastModified {

    val excludedIds = emptyList<Long>()
    val boundaryLastModifiedTimestamp: Instant? = null

    return when (orderType) {
        OrderType.ASC  -> RequestParams.OrderLastModified.Asc(
                excludedIds = excludedIds,
                minLastModifiedTimestamp = boundaryLastModifiedTimestamp
        )
        OrderType.DESC -> RequestParams.OrderLastModified.Desc(
                excludedIds = excludedIds,
                maxLastModifiedTimestamp = boundaryLastModifiedTimestamp
        )
    }
}

private fun createPaginationResponseForOrderTotalSum(
        pageResult: List<GameRecordWithSyncState>,
        previousRequestParams: RequestParams.OrderTotalSum
): RequestParams.OrderTotalSum {
    val fieldGetter: (GameRecordWithSyncState) -> Int = { item -> item.record.gameState.current.immutableNumbersMatrix.totalSum }
    val lastItemTotalSum = fieldGetter(pageResult.last())
    val excludedIdsForLastItemInPage = pageResult.takeLastWhile { item -> fieldGetter(item) == lastItemTotalSum }
            .map { item -> item.record.id }

    val allExcludedIds = if (previousRequestParams.boundarySum == lastItemTotalSum) {
        (previousRequestParams.excludedIds + excludedIdsForLastItemInPage)
    } else {
        excludedIdsForLastItemInPage
    }

    return when (previousRequestParams) {
        is RequestParams.OrderTotalSum.Asc  -> RequestParams.OrderTotalSum.Asc(
                excludedIds = allExcludedIds,
                minTotalSum = lastItemTotalSum
        )
        is RequestParams.OrderTotalSum.Desc -> RequestParams.OrderTotalSum.Desc(
                excludedIds = allExcludedIds,
                maxTotalSum = lastItemTotalSum
        )
    }
}

private fun createPaginationResponseForOrderLastModified(
        pageResult: List<GameRecordWithSyncState>,
        previousRequestParams: RequestParams.OrderLastModified
): RequestParams.OrderLastModified {
    val fieldGetter: (GameRecordWithSyncState) -> Instant = { item -> item.syncState.lastLocalModifiedTimestamp }
    val lastItemTimestamp = fieldGetter(pageResult.last())
    val excludedIdsForLastItemInPage = pageResult.takeLastWhile { item -> fieldGetter(item) == lastItemTimestamp }
            .map { item -> item.record.id }

    val allExcludedIds = if (previousRequestParams.boundaryTimestamp == lastItemTimestamp) {
        (previousRequestParams.excludedIds + excludedIdsForLastItemInPage)
    } else {
        excludedIdsForLastItemInPage
    }

    return when (previousRequestParams) {
        is RequestParams.OrderLastModified.Asc  -> RequestParams.OrderLastModified.Asc(
                excludedIds = allExcludedIds,
                minLastModifiedTimestamp = lastItemTimestamp
        )
        is RequestParams.OrderLastModified.Desc -> RequestParams.OrderLastModified.Desc(
                excludedIds = allExcludedIds,
                maxLastModifiedTimestamp = lastItemTimestamp
        )
    }
}