package com.ruslan.hlushan.game.core.api.play

import com.ruslan.hlushan.core.api.dto.ValueHolder
import com.ruslan.hlushan.core.api.dto.pagination.PaginationPagesRequest
import com.ruslan.hlushan.core.api.dto.pagination.PaginationResponse
import com.ruslan.hlushan.game.core.api.play.dto.GameRecord
import com.ruslan.hlushan.game.core.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.core.api.play.dto.GameState
import com.ruslan.hlushan.game.core.api.play.dto.RequestParams
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.Duration
import org.threeten.bp.Instant

interface PlayRecordsInteractor {

    fun getAvailableRecords(
            pagesRequest: PaginationPagesRequest<RequestParams>,
            filter: GameRecordWithSyncState.Order.Params,
            limit: Int
    ): Single<PaginationResponse<GameRecordWithSyncState, RequestParams>>

    fun observeGameRecord(id: Long): Observable<ValueHolder<GameRecordWithSyncState?>>

    fun updateAndGetRecordForPlaying(id: Long): Single<GameRecord>

    fun markAsNonPlaying(id: Long): Completable

    fun markAsNonPlayingAsynchronously(id: Long)

    fun removeRecordById(id: Long): Completable

    fun updateRecordAfterPlaying(
            id: Long,
            gameState: GameState,
            totalPlayed: Duration,
            localModifiedTimestamp: Instant
    ): Completable

    fun addNewRecordAfterPlaying(
            gameState: GameState,
            totalPlayed: Duration,
            localCreatedTimestamp: Instant
    ): Completable

    fun getCountOfNotSynchronizedRecords(): Single<Int>
}