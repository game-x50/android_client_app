package com.ruslan.hlushan.game.api.play.dto

import com.ruslan.hlushan.core.pagination.api.NextPageId
import com.ruslan.hlushan.core.pagination.api.PageId
import com.ruslan.hlushan.core.pagination.api.PaginationPagesRequest
import com.ruslan.hlushan.core.pagination.api.PaginationResponse
import com.ruslan.hlushan.core.pagination.api.PreviousPageId
import com.ruslan.hlushan.core.pagination.api.createPaginationResponseByLimits
import org.threeten.bp.Instant

sealed class RequestParams {

    abstract val excludedIds: List<Long>

    sealed class OrderTotalSum : RequestParams() {

        class Asc(
                override val excludedIds: List<Long>,
                val minTotalSum: Int?
        ) : RequestParams.OrderTotalSum()

        class Desc(
                override val excludedIds: List<Long>,
                val maxTotalSum: Int?
        ) : RequestParams.OrderTotalSum()
    }

    sealed class OrderLastModified : RequestParams() {

        class Asc(
                override val excludedIds: List<Long>,
                val minLastModifiedTimestamp: Instant?
        ) : RequestParams.OrderLastModified()

        class Desc(
                override val excludedIds: List<Long>,
                val maxLastModifiedTimestamp: Instant?
        ) : RequestParams.OrderLastModified()
    }
}

private val RequestParams.OrderTotalSum.boundarySum: Int?
    get() = when (this) {
        is RequestParams.OrderTotalSum.Asc  -> this.minTotalSum
        is RequestParams.OrderTotalSum.Desc -> this.maxTotalSum
    }

private val RequestParams.OrderLastModified.boundaryTimestamp: Instant?
    get() = when (this) {
        is RequestParams.OrderLastModified.Asc  -> this.minLastModifiedTimestamp
        is RequestParams.OrderLastModified.Desc -> this.maxLastModifiedTimestamp
    }

//TODO: #write_unit_tests
fun combineToRequestParams(
        pagesRequest: PaginationPagesRequest<RequestParams>,
        orderParams: GameRecordWithSyncState.Order.Params
): RequestParams =
        when (pagesRequest) {
            is PaginationPagesRequest.Init     -> {
                createOrderedInitParams(orderParams)
            }
            is PaginationPagesRequest.Next     -> {
                pagesRequest.nextPageId.value.value
            }
            is PaginationPagesRequest.Previous -> {
                when (val previousPageId = pagesRequest.previousPageId) {
                    is PageId.First        -> createOrderedInitParams(orderParams)
                    is PageId.SecondOrMore -> previousPageId.value
                }
            }
        }

//TODO: #write_unit_tests
fun createPaginationResponseFor(
        pageResult: List<GameRecordWithSyncState>,
        pagesRequest: PaginationPagesRequest<RequestParams>,
        requestParams: RequestParams,
        limit: Int
): PaginationResponse<GameRecordWithSyncState, RequestParams> =
        createPaginationResponseByLimits(
                pageResult = pageResult,
                pagesRequest = pagesRequest,
                requestPageId = requestParams,
                limit = limit,
                createNextPageIdFor = ::createNextPageIdFor,
                createPreviousPageIdFor = ::createPreviousPageIdFor
        )

private fun createPreviousPageIdFor(
        requestParams: RequestParams,
        pageResult: List<GameRecordWithSyncState>
): PreviousPageId.Existing<RequestParams> {

    val idValue = createIdValueFor(
            requestParams = requestParams,
            pageResult = pageResult,
            direction = PaginationPagesRequest.Direction.PREVIOUS
    )

    return PreviousPageId.Existing(PageId.SecondOrMore(idValue))
}

private fun createNextPageIdFor(
        requestParams: RequestParams,
        pageResult: List<GameRecordWithSyncState>
): NextPageId.Existing<RequestParams> {

    val idValue = createIdValueFor(
            requestParams = requestParams,
            pageResult = pageResult,
            direction = PaginationPagesRequest.Direction.NEXT
    )

    return NextPageId.Existing(PageId.SecondOrMore(idValue))
}

private fun createIdValueFor(
        requestParams: RequestParams,
        pageResult: List<GameRecordWithSyncState>,
        direction: PaginationPagesRequest.Direction
) = when (requestParams) {
    is RequestParams.OrderTotalSum     -> {
        createPaginationResponseForOrderTotalSum(
                pageResult = pageResult,
                previousRequestParams = requestParams,
                direction = direction
        )
    }
    is RequestParams.OrderLastModified -> {
        createPaginationResponseForOrderLastModified(
                pageResult = pageResult,
                requestParams,
                direction
        )
    }
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
        previousRequestParams: RequestParams.OrderTotalSum,
        direction: PaginationPagesRequest.Direction
): RequestParams.OrderTotalSum {

    val (boundaryItemTotalSum: Int, allExcludedIds: List<Long>) = createBoundaryItemValueWithAllExcludedIds(
            pageResult = pageResult,
            direction = direction,
            fieldGetter = { item -> item.record.gameState.current.immutableNumbersMatrix.totalSum },
            previousRequestParamsBoundary = previousRequestParams.boundarySum,
            previousRequestParamsExcludedIds = previousRequestParams.excludedIds
    )

    return when (previousRequestParams) {
        is RequestParams.OrderTotalSum.Asc  -> RequestParams.OrderTotalSum.Asc(
                excludedIds = allExcludedIds,
                minTotalSum = boundaryItemTotalSum
        )
        is RequestParams.OrderTotalSum.Desc -> RequestParams.OrderTotalSum.Desc(
                excludedIds = allExcludedIds,
                maxTotalSum = boundaryItemTotalSum
        )
    }
}

private fun createPaginationResponseForOrderLastModified(
        pageResult: List<GameRecordWithSyncState>,
        previousRequestParams: RequestParams.OrderLastModified,
        direction: PaginationPagesRequest.Direction
): RequestParams.OrderLastModified {

    val (boundaryItemTimestamp: Instant, allExcludedIds: List<Long>) = createBoundaryItemValueWithAllExcludedIds(
            pageResult = pageResult,
            direction = direction,
            fieldGetter = { item -> item.syncState.lastLocalModifiedTimestamp },
            previousRequestParamsBoundary = previousRequestParams.boundaryTimestamp,
            previousRequestParamsExcludedIds = previousRequestParams.excludedIds
    )

    return when (previousRequestParams) {
        is RequestParams.OrderLastModified.Asc  -> RequestParams.OrderLastModified.Asc(
                excludedIds = allExcludedIds,
                minLastModifiedTimestamp = boundaryItemTimestamp
        )
        is RequestParams.OrderLastModified.Desc -> RequestParams.OrderLastModified.Desc(
                excludedIds = allExcludedIds,
                maxLastModifiedTimestamp = boundaryItemTimestamp
        )
    }
}

private fun <T : Any> createBoundaryItemValueWithAllExcludedIds(
        pageResult: List<GameRecordWithSyncState>,
        direction: PaginationPagesRequest.Direction,
        fieldGetter: (GameRecordWithSyncState) -> T,
        previousRequestParamsBoundary: T?,
        previousRequestParamsExcludedIds: List<Long>
): Pair<T, List<Long>> {
    val (boundaryItemValue: T, excludedIdsForBoundaryItemInPage: List<Long>) = when (direction) {
        PaginationPagesRequest.Direction.PREVIOUS -> {
            val firstItemValue: T = fieldGetter(pageResult.first())
            val excludedIdsForFirstItemInPage: List<Long> = pageResult
                    .takeWhile { item -> fieldGetter(item) == firstItemValue }
                    .map { item -> item.record.id }

            firstItemValue to excludedIdsForFirstItemInPage
        }
        PaginationPagesRequest.Direction.NEXT     -> {
            val lastItemValue: T = fieldGetter(pageResult.last())
            val excludedIdsForLastItemInPage: List<Long> = pageResult
                    .takeLastWhile { item -> fieldGetter(item) == lastItemValue }
                    .map { item -> item.record.id }

            lastItemValue to excludedIdsForLastItemInPage
        }
    }

    val allExcludedIds = if (previousRequestParamsBoundary == boundaryItemValue) {
        (previousRequestParamsExcludedIds + excludedIdsForBoundaryItemInPage)
    } else {
        excludedIdsForBoundaryItemInPage
    }

    return (boundaryItemValue to allExcludedIds)
}