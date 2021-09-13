package com.ruslan.hlushan.game.storage.impl.local

import androidx.annotation.IntRange
import com.ruslan.hlushan.core.api.dto.ValueHolder
import com.ruslan.hlushan.core.pagination.api.PaginationPagesRequest
import com.ruslan.hlushan.core.pagination.api.PaginationResponse
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.GameState
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RequestParams
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.Duration
import org.threeten.bp.Instant

@SuppressWarnings("ComplexInterface", "TooManyFunctions")
internal interface LocalRecordsRepository {

    fun getAvailableRecords(
            pagesRequest: PaginationPagesRequest<RequestParams>,
            filter: GameRecordWithSyncState.Order.Params,
            limit: Int
    ): Single<PaginationResponse<GameRecordWithSyncState, RequestParams>>

    fun observeGameRecord(id: Long): Observable<ValueHolder<GameRecordWithSyncState?>>

    fun getFullRecordData(id: Long): Single<GameRecordWithSyncState>

    fun addNewRecord(request: LocalUpdateRequest): Completable

    fun updateRecord(id: Long, request: LocalUpdateRequest): Completable

    fun addNewAndUpdateSyncState(
            addRequest: LocalUpdateRequest,
            updateId: Long,
            updateSyncState: RecordSyncState
    ): Completable

    fun addNewRecordsList(requests: List<LocalUpdateRequest>): Completable

    fun setPlayingById(id: Long, playing: Boolean): Completable

    fun updateRecordSyncState(id: Long, syncState: RecordSyncState): Completable

    fun removeRecordById(id: Long): Completable

    fun updateSyncStatusOnSyncFail(ids: List<Long>): Completable

    fun updateSyncStatusOnSyncFail(id: Long, actualStateDb: RecordSyncState): Completable

    fun markAsSyncingAndGetWaitingRecords(
            limit: Int,
            createIdGenerator: (GameRecord) -> String
    ): Single<List<GameRecordWithSyncState>>

    fun markAsSyncingAndGetSyncedWhereLastRemoteSyncSmallerThen(
            maxLastRemoteSyncedTimestamp: Instant,
            @IntRange(from = 1) limit: Int
    ): Single<List<GameRecordWithSyncState>>

    fun getLastCreatedTimestampWithExcludedRemoteIds(): Single<LastCreatedTimestampWithExcludedRemoteIds>

    fun storeLastCreatedTimestamp(newLastCreatedTimestamp: Instant): Completable

    fun deleteAllGames(): Completable

    fun getCountOfNotSynchronizedRecords(): Single<Int>

    fun setIsSynchronizing(isSynchronizing: Boolean)

    fun observeIsSynchronizing(): Observable<Boolean>
}

internal fun LocalRecordsRepository.markSynchronizingStarted() =
        this.setIsSynchronizing(isSynchronizing = true)

internal fun LocalRecordsRepository.markSynchronizingFinished() =
        this.setIsSynchronizing(isSynchronizing = false)

internal data class LastCreatedTimestampWithExcludedRemoteIds(
        val lastCreatedTimestamp: Instant,
        val excludedRemoteIds: List<String>
)

internal data class LocalUpdateRequest(
        val gameState: GameState,
        val totalPlayed: Duration,
        val syncState: RecordSyncState
)