package com.ruslan.hlushan.game.storage.impl.local

import androidx.annotation.IntRange
import com.ruslan.hlushan.core.api.dto.ValueHolder
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.pagination.api.PaginationPagesRequest
import com.ruslan.hlushan.core.pagination.api.PaginationResponse
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RequestParams
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.play.dto.combineToRequestParams
import com.ruslan.hlushan.game.api.play.dto.createPaginationResponseFor
import com.ruslan.hlushan.game.storage.impl.local.db.dao.GameRecordsDAO
import com.ruslan.hlushan.game.storage.impl.local.db.entities.GameRecordDb
import com.ruslan.hlushan.game.storage.impl.local.db.entities.LocalActionTypeDb
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import org.threeten.bp.Instant
import javax.inject.Inject

@SuppressWarnings("TooManyFunctions")
internal open class LocalRecordsRepositoryImpl
@Inject
constructor(
        private val localRecordsRepositoryStorage: LocalRecordsRepositoryStorage,
        private val gameRecordsDAO: GameRecordsDAO,
        private val schedulersManager: SchedulersManager,
        private val appLogger: AppLogger
) : LocalRecordsRepository {

    private val isSynchronizingSubject = BehaviorSubject.createDefault(false)

    init {
        fixAll()
    }

    override fun getAvailableRecords(
            pagesRequest: PaginationPagesRequest<RequestParams>,
            filter: GameRecordWithSyncState.Order.Params,
            limit: Int
    ): Single<PaginationResponse<GameRecordWithSyncState, RequestParams>> =
            Single.fromCallable { combineToRequestParams(pagesRequest, filter) }
                    .flatMap { requestParams ->
                        when (requestParams) {
                            is RequestParams.OrderTotalSum.Asc      -> {
                                gameRecordsDAO.getRecordsExcludeOrderByTotalSumAsc(
                                        localActionType = LocalActionTypeDb.DELETE,
                                        excludedIds = requestParams.excludedIds,
                                        minTotalSum = requestParams.minTotalSum,
                                        limit = limit
                                )
                            }
                            is RequestParams.OrderTotalSum.Desc     -> {
                                gameRecordsDAO.getRecordsExcludeOrderByTotalSumDesc(
                                        localActionType = LocalActionTypeDb.DELETE,
                                        excludedIds = requestParams.excludedIds,
                                        maxTotalSum = requestParams.maxTotalSum,
                                        limit = limit
                                )
                            }
                            is RequestParams.OrderLastModified.Asc  -> {
                                gameRecordsDAO.getRecordsExcludeOrderByLastModifiedAsc(
                                        localActionType = LocalActionTypeDb.DELETE,
                                        excludedIds = requestParams.excludedIds,
                                        minLastModified = requestParams.minLastModifiedTimestamp,
                                        limit = limit
                                )
                            }
                            is RequestParams.OrderLastModified.Desc -> {
                                gameRecordsDAO.getRecordsExcludeOrderByLastModifiedDesc(
                                        localActionType = LocalActionTypeDb.DELETE,
                                        excludedIds = requestParams.excludedIds,
                                        maxLastModified = requestParams.maxLastModifiedTimestamp,
                                        limit = limit
                                )
                            }
                        }.map { dbRecords -> Pair(requestParams, dbRecords) }
                    }
                    .map { (requestParams, dbRecords) ->
                        requestParams to dbRecords.map(GameRecordDb::fromDbRecord)
                    }
                    .map { (requestParams, pageResult) ->
                        createPaginationResponseFor(pageResult, pagesRequest, requestParams, limit = limit)
                    }
                    .subscribeOn(schedulersManager.io)

    override fun observeGameRecord(id: Long): Observable<ValueHolder<GameRecordWithSyncState?>> =
            gameRecordsDAO.observeGameRecord(id)
                    .distinctUntilChanged()
                    .map { dbRecord -> ValueHolder(dbRecord.firstOrNull()?.fromDbRecord()) }
                    .subscribeOn(schedulersManager.io)

    override fun getFullRecordData(id: Long): Single<GameRecordWithSyncState> =
            gameRecordsDAO.getRecordById(id)
                    .map(GameRecordDb::fromDbRecord)
                    .subscribeOn(schedulersManager.io)

    override fun addNewRecord(request: LocalUpdateRequest): Completable =
            updateOrCreateRecord(id = null, request = request)

    override fun updateRecord(id: Long, request: LocalUpdateRequest): Completable =
            updateOrCreateRecord(id = id, request = request)

    override fun addNewAndUpdateSyncState(
            addRequest: LocalUpdateRequest,
            updateId: Long,
            updateSyncState: RecordSyncState
    ): Completable =
            Completable.fromAction { gameRecordsDAO.addNewAndUpdateSyncState(addRequest, updateId, updateSyncState) }
                    .subscribeOn(schedulersManager.io)

    override fun addNewRecordsList(requests: List<LocalUpdateRequest>): Completable =
            Completable.fromAction { gameRecordsDAO.addNewRecordsList(requests) }
                    .subscribeOn(schedulersManager.io)

    override fun setPlayingById(id: Long, playing: Boolean): Completable =
            Completable.fromAction { gameRecordsDAO.setModifyingById(id, playing) }
                    .subscribeOn(schedulersManager.io)

    override fun updateRecordSyncState(id: Long, syncState: RecordSyncState): Completable =
            Completable.fromAction { gameRecordsDAO.updateSyncStateById(id, syncState) }
                    .subscribeOn(schedulersManager.io)

    override fun removeRecordById(id: Long): Completable =
            Completable.fromAction { gameRecordsDAO.removeRecordById(id) }
                    .subscribeOn(schedulersManager.io)

    override fun updateSyncStatusOnSyncFail(ids: List<Long>): Completable =
            Completable.fromAction { gameRecordsDAO.updateSyncStatusOnSyncFail(ids) }
                    .subscribeOn(schedulersManager.io)

    override fun updateSyncStatusOnSyncFail(id: Long, actualStateDb: RecordSyncState): Completable =
            Completable.fromAction { gameRecordsDAO.updateSyncStatusOnSyncFail(id, actualStateDb) }
                    .subscribeOn(schedulersManager.io)

    override fun markAsSyncingAndGetWaitingRecords(
            limit: Int,
            createIdGenerator: (GameRecord) -> String
    ): Single<List<GameRecordWithSyncState>> =
            Single.fromCallable { gameRecordsDAO.markAsSyncingAndGetWaitingRecords(limit, createIdGenerator) }
                    .subscribeOn(schedulersManager.io)

    override fun markAsSyncingAndGetSyncedWhereLastRemoteSyncSmallerThen(
            maxLastRemoteSyncedTimestamp: Instant,
            @IntRange(from = 1) limit: Int
    ): Single<List<GameRecordWithSyncState>> =
            Single.fromCallable {
                gameRecordsDAO.markAsSyncingAndGetSyncedWhereLastRemoteSyncSmallerThen(
                        maxLastRemoteSyncedTimestamp = maxLastRemoteSyncedTimestamp,
                        limit = limit
                )
            }
                    .map { dbRecords -> dbRecords.map(GameRecordDb::fromDbRecord) }
                    .subscribeOn(schedulersManager.io)

    override fun getLastCreatedTimestampWithExcludedRemoteIds(): Single<LastCreatedTimestampWithExcludedRemoteIds> =
            Single.fromCallable { localRecordsRepositoryStorage.lastCreatedTimestamp }
                    .flatMap { lastCreatedTimestamp ->
                        gameRecordsDAO.getRemoteIdsWhereCreatedTimestampGraterOrEqual(lastCreatedTimestamp)
                                .map { excludedRemoteIds ->
                                    LastCreatedTimestampWithExcludedRemoteIds(lastCreatedTimestamp, excludedRemoteIds)
                                }
                    }
                    .subscribeOn(schedulersManager.io)

    override fun storeLastCreatedTimestamp(newLastCreatedTimestamp: Instant): Completable =
            localRecordsRepositoryStorage.storeLastCreatedTimestamp(newLastCreatedTimestamp)

    private fun updateOrCreateRecord(id: Long?, request: LocalUpdateRequest): Completable =
            Completable.fromAction { gameRecordsDAO.saveRecord(id = id, request = request) }
                    .subscribeOn(schedulersManager.io)

    override fun deleteAllGames(): Completable =
            Completable.fromAction { gameRecordsDAO.deleteAll() }
                    .subscribeOn(schedulersManager.io)

    override fun getCountOfNotSynchronizedRecords(): Single<Int> =
            gameRecordsDAO.getCountRecordsWhereSyncStatusNot(SyncStatus.SYNCED)
                    .subscribeOn(schedulersManager.io)

    @Suppress("CheckResult")
    private fun fixAll() {
        Completable.fromAction { gameRecordsDAO.setAllModifying(modifying = false) }
                .andThen(gameRecordsDAO.getAllIdsWhereSyncStatus(syncStatus = SyncStatus.SYNCHRONIZING))
                .map { ids -> gameRecordsDAO.updateSyncStatusOnSyncFail(ids = ids) }
                .subscribeOn(schedulersManager.io)
                .subscribe(
                        { appLogger.log(this@LocalRecordsRepositoryImpl, "fixAll SUCCESS!") },
                        { error -> appLogger.log(this@LocalRecordsRepositoryImpl, "fixAll ERROR!", error) }
                )
    }

    override fun setIsSynchronizing(isSynchronizing: Boolean) =
            isSynchronizingSubject.onNext(isSynchronizing)

    override fun observeIsSynchronizing(): Observable<Boolean> =
            isSynchronizingSubject.distinctUntilChanged()
}