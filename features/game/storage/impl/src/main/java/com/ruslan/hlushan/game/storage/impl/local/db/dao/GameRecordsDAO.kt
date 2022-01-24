package com.ruslan.hlushan.game.storage.impl.local.db.dao

import androidx.annotation.IntRange
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.LocalAction
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.play.dto.createNewStateToStoreAfterUnknownSyncResult
import com.ruslan.hlushan.game.storage.impl.local.LocalUpdateRequest
import com.ruslan.hlushan.game.storage.impl.local.db.entities.GameRecordDb
import com.ruslan.hlushan.game.storage.impl.local.db.entities.GameStateDb
import com.ruslan.hlushan.game.storage.impl.local.db.entities.LocalActionTypeDb
import com.ruslan.hlushan.game.storage.impl.local.db.entities.MatrixAndNewItemsStateDb
import com.ruslan.hlushan.game.storage.impl.local.db.entities.fromDbRecord
import com.ruslan.hlushan.game.storage.impl.local.db.entities.toDbGameState
import com.ruslan.hlushan.game.storage.impl.local.db.entities.toMatricesList
import com.ruslan.hlushan.game.storage.impl.local.db.entities.typeDb
import io.reactivex.Observable
import io.reactivex.Single

private const val SELECT_ALL_EXCLUDE_LOCAL_ACTION_TYPE_AND_IDS =
        """
            SELECT * FROM ${GameStateDb.GAME_RECORDS_TABLE}
            WHERE ${GameStateDb.LOCAL_ACTION_TYPE} IS NOT :localActionType
            AND ${GameStateDb.RECORD_ID} NOT IN (:excludedIds)
        """

@SuppressWarnings("TooManyFunctions")
@Dao
internal abstract class GameRecordsDAO {

    @Transaction
    @Query("""
        $SELECT_ALL_EXCLUDE_LOCAL_ACTION_TYPE_AND_IDS
        AND ((:maxTotalSum IS NULL) OR (${GameStateDb.TOTAL_SUM} <= :maxTotalSum))
        ORDER BY ${GameStateDb.TOTAL_SUM} DESC
        LIMIT :limit
        """)
    abstract fun getRecordsExcludeOrderByTotalSumDesc(
            localActionType: LocalActionTypeDb,
            excludedIds: List<Long>,
            maxTotalSum: Int?,
            @IntRange(from = 1) limit: Int
    ): Single<List<GameRecordDb>>

    @Transaction
    @Query("""
        $SELECT_ALL_EXCLUDE_LOCAL_ACTION_TYPE_AND_IDS
        AND ((:minTotalSum IS NULL) OR (${GameStateDb.TOTAL_SUM} >= :minTotalSum))
        ORDER BY ${GameStateDb.TOTAL_SUM} ASC
        LIMIT :limit
        """)
    abstract fun getRecordsExcludeOrderByTotalSumAsc(
            localActionType: LocalActionTypeDb,
            excludedIds: List<Long>,
            minTotalSum: Int?,
            @IntRange(from = 1) limit: Int
    ): Single<List<GameRecordDb>>

    @Transaction
    @Query("""
        $SELECT_ALL_EXCLUDE_LOCAL_ACTION_TYPE_AND_IDS
        AND ((:maxLastModified IS NULL) OR (${GameStateDb.LAST_LOCAL_MODIFIED_TIMESTAMP} <= :maxLastModified))
        ORDER BY ${GameStateDb.LAST_LOCAL_MODIFIED_TIMESTAMP} DESC
        LIMIT :limit
    """)
    abstract fun getRecordsExcludeOrderByLastModifiedDesc(
            localActionType: LocalActionTypeDb,
            excludedIds: List<Long>,
            maxLastModified: RecordSyncState.LastLocalModifiedTimestamp?,
            @IntRange(from = 1) limit: Int
    ): Single<List<GameRecordDb>>

    @Transaction
    @Query("""
        $SELECT_ALL_EXCLUDE_LOCAL_ACTION_TYPE_AND_IDS
        AND ((:minLastModified IS NULL) OR (${GameStateDb.LAST_LOCAL_MODIFIED_TIMESTAMP} >= :minLastModified))
        ORDER BY ${GameStateDb.LAST_LOCAL_MODIFIED_TIMESTAMP} ASC
        LIMIT :limit""")
    abstract fun getRecordsExcludeOrderByLastModifiedAsc(
            localActionType: LocalActionTypeDb,
            excludedIds: List<Long>,
            minLastModified: RecordSyncState.LastLocalModifiedTimestamp?,
            @IntRange(from = 1) limit: Int
    ): Single<List<GameRecordDb>>

    @Query("""
        SELECT COUNT(*) FROM ${GameStateDb.GAME_RECORDS_TABLE}
        WHERE ${GameStateDb.SYNC_STATUS} IS NOT :syncStatus
        """)
    abstract fun getCountRecordsWhereSyncStatusNot(syncStatus: SyncStatus): Single<Int>

    @Transaction
    @Query("""
        UPDATE ${GameStateDb.GAME_RECORDS_TABLE}
        SET ${GameStateDb.MODIFYING_NOW} = :modifying
        WHERE ${GameStateDb.MODIFYING_NOW} != :modifying
        """)
    abstract fun setAllModifying(modifying: Boolean)

    @Query("""
        SELECT ${GameStateDb.RECORD_ID} FROM ${GameStateDb.GAME_RECORDS_TABLE}
        WHERE ${GameStateDb.SYNC_STATUS} = :syncStatus
        """)
    abstract fun getAllIdsWhereSyncStatus(syncStatus: SyncStatus): Single<List<Long>>

    @Transaction
    @Query("""
        SELECT * FROM ${GameStateDb.GAME_RECORDS_TABLE}
        WHERE ${GameStateDb.RECORD_ID} = :id
        LIMIT 1
        """)
    abstract fun observeGameRecord(id: Long): Observable<List<GameRecordDb>>

    @Transaction
    @Query("""
        SELECT * FROM ${GameStateDb.GAME_RECORDS_TABLE}
        WHERE ${GameStateDb.RECORD_ID} = :id
        LIMIT 1
        """)
    abstract fun getRecordById(id: Long): Single<GameRecordDb>

    @Transaction
    open fun saveRecord(id: Long?, request: LocalUpdateRequest) {
        val savedRecordId: Long = saveState(request.toDbGameState(id))
        saveMatrices(request.toMatricesList(savedRecordId))
    }

    @Transaction
    open fun addNewRecordsList(requests: List<LocalUpdateRequest>) {
        for (newRecordRequest in requests) {
            saveRecord(id = null, request = newRecordRequest)
        }
    }

    @Query("""
        UPDATE ${GameStateDb.GAME_RECORDS_TABLE}
        SET ${GameStateDb.MODIFYING_NOW} = :modifying
        WHERE ${GameStateDb.RECORD_ID} = :id
        """)
    abstract fun setModifyingById(id: Long, modifying: Boolean)

    @Query("""
        UPDATE ${GameStateDb.GAME_RECORDS_TABLE}
        SET ${GameStateDb.LOCAL_CREATE_ID} = :localCreatedId
        WHERE ${GameStateDb.RECORD_ID} = :id
        """)
    abstract fun setLocalCreatedIdById(
            id: Long,
            localCreatedId: RecordSyncState.LocalCreateId
    )

    @Transaction
    @Query("DELETE FROM ${GameStateDb.GAME_RECORDS_TABLE}")
    abstract fun deleteAll()

    @Transaction
    open fun addNewAndUpdateSyncState(
            addRequest: LocalUpdateRequest,
            updateId: Long,
            updateSyncState: RecordSyncState
    ) {
        saveRecord(id = null, request = addRequest)
        updateSyncStateById(recordId = updateId, updatedSyncState = updateSyncState)
    }

    @Transaction
    open fun updateSyncStatusOnSyncFail(ids: List<Long>) {
        getRecordsByIds(ids)
                .map { dbRec -> dbRec.fromDbRecord() }
                .forEach { rec ->
                    updateSyncStatusOnSyncFail(rec.record.id, rec.syncState)
                }
    }

    @Transaction
    open fun updateSyncStatusOnSyncFail(id: Long, actualStateDb: RecordSyncState) {
        val updatedSyncState = actualStateDb.createNewStateToStoreAfterUnknownSyncResult()
        when {
            (updatedSyncState == actualStateDb) -> Unit
            (updatedSyncState == null)          -> removeRecordById(id)
            (updatedSyncState != actualStateDb) -> updateSyncStateById(id, updatedSyncState)
        }
    }

    @Transaction
    open fun markAsSyncingAndGetWaitingRecords(
            @IntRange(from = 1) limit: Int,
            createIdGenerator: (GameRecord) -> RecordSyncState.LocalCreateId
    ): List<GameRecordWithSyncState> {
        val records = getRecordsWithSyncAndPlayingStatus(
                syncStatus = SyncStatus.WAITING,
                modifying = false,
                limit = limit
        )

        setSyncStatusByIds(SyncStatus.SYNCHRONIZING,
                           records.map { recordDb ->
                               @Suppress("UnsafeCallOnNullableType")
                               recordDb.stateDb.recordId!!
                           })

        return records
                .map { dbRec -> dbRec.fromDbRecord() }
                .map { rec ->
                    if ((rec.syncState.localAction is LocalAction.Create) && (rec.syncState.localCreateId == null)) {
                        val generatedLocalCreatedIdById = createIdGenerator(rec.record)
                        setLocalCreatedIdById(rec.record.id, generatedLocalCreatedIdById)
                        rec.copy(syncState = rec.syncState.copy(localCreateId = generatedLocalCreatedIdById,
                                                                syncStatus = SyncStatus.SYNCHRONIZING))
                    } else {
                        rec.copy(syncState = rec.syncState.copy(syncStatus = SyncStatus.SYNCHRONIZING))
                    }
                }
    }

    @Transaction
    open fun markAsSyncingAndGetSyncedWhereLastRemoteSyncSmallerThen(
            maxLastRemoteSyncedTimestamp: RemoteInfo.LastSyncedTimestamp,
            @IntRange(from = 1) limit: Int
    ): List<GameRecordDb> {
        val records = getWhereLastRemoteSyncSmallerThen(
                syncStatus = SyncStatus.SYNCED,
                modifying = false,
                maxLastRemoteSyncedTimestamp = maxLastRemoteSyncedTimestamp,
                limit = limit
        )

        setSyncStatusByIds(SyncStatus.SYNCHRONIZING,
                           records.map { recordDb ->
                               @Suppress("UnsafeCallOnNullableType")
                               recordDb.stateDb.recordId!!
                           })

        return records
                .map { recordDb ->
                    recordDb.copy(stateDb = recordDb.stateDb.copy(syncStatus = SyncStatus.SYNCHRONIZING))
                }
    }

    @Transaction
    @Query("""
        SELECT ${GameStateDb.REMOTE_RECORD_ID} FROM ${GameStateDb.GAME_RECORDS_TABLE}
        WHERE ${GameStateDb.REMOTE_CREATED_TIMESTAMP} >= :minRemoteCreatedTimestamp
        """)
    abstract fun getRemoteIdsWhereCreatedTimestampGraterOrEqual(
            minRemoteCreatedTimestamp: RemoteInfo.CreatedTimestamp
    ): Single<List<RemoteInfo.Id>>

    fun updateSyncStateById(recordId: Long, updatedSyncState: RecordSyncState) =
            updateSyncStateById(
                    id = recordId,
                    remoteId = updatedSyncState.remoteInfo?.remoteId,
                    remoteActionId = updatedSyncState.remoteInfo?.remoteActionId,
                    remoteCreatedTimestamp = updatedSyncState.remoteInfo?.remoteCreatedTimestamp,
                    lastRemoteSyncedTimestamp = updatedSyncState.remoteInfo?.lastRemoteSyncedTimestamp,
                    localActionType = updatedSyncState.localAction?.typeDb,
                    lastLocalModifiedTimestamp = updatedSyncState.lastLocalModifiedTimestamp,
                    localActionId = updatedSyncState.localAction?.actionId,
                    localCreateId = updatedSyncState.localCreateId,
                    modifyingNow = updatedSyncState.modifyingNow,
                    syncStatus = updatedSyncState.syncStatus
            )

    @SuppressWarnings("LongParameterList")
    @Query("""
        UPDATE ${GameStateDb.GAME_RECORDS_TABLE} SET
        ${GameStateDb.REMOTE_RECORD_ID} = :remoteId,
        ${GameStateDb.REMOTE_ACTION_ID} = :remoteActionId,
        ${GameStateDb.REMOTE_CREATED_TIMESTAMP} = :remoteCreatedTimestamp,
        ${GameStateDb.LAST_REMOTE_SYNCED_TIMESTAMP} = :lastRemoteSyncedTimestamp,
        ${GameStateDb.LOCAL_ACTION_TYPE} = :localActionType,
        ${GameStateDb.LAST_LOCAL_MODIFIED_TIMESTAMP} = :lastLocalModifiedTimestamp,
        ${GameStateDb.LOCAL_ACTION_ID} = :localActionId,
        ${GameStateDb.LOCAL_CREATE_ID} = :localCreateId,
        ${GameStateDb.MODIFYING_NOW} = :modifyingNow,
        ${GameStateDb.SYNC_STATUS} = :syncStatus
        WHERE ${GameStateDb.RECORD_ID} = :id
           """)
    protected abstract fun updateSyncStateById(
            id: Long,
            remoteId: RemoteInfo.Id?,
            remoteActionId: RemoteInfo.ActionId?,
            remoteCreatedTimestamp: RemoteInfo.CreatedTimestamp?,
            lastRemoteSyncedTimestamp: RemoteInfo.LastSyncedTimestamp?,
            localActionType: LocalActionTypeDb?,
            lastLocalModifiedTimestamp: RecordSyncState.LastLocalModifiedTimestamp,
            localActionId: LocalAction.Id?,
            localCreateId: RecordSyncState.LocalCreateId?,
            modifyingNow: Boolean,
            syncStatus: SyncStatus
    )

    @Transaction
    @Query("""
        DELETE FROM ${GameStateDb.GAME_RECORDS_TABLE}
        WHERE ${GameStateDb.RECORD_ID} = :id
        """)
    abstract fun removeRecordById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun saveState(record: GameStateDb): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun saveMatrices(matrices: List<MatrixAndNewItemsStateDb>)

    @Transaction
    @Query("""
        SELECT * FROM ${GameStateDb.GAME_RECORDS_TABLE}
        WHERE ${GameStateDb.SYNC_STATUS} = :syncStatus AND ${GameStateDb.MODIFYING_NOW} = :modifying
        LIMIT :limit
        """)
    protected abstract fun getRecordsWithSyncAndPlayingStatus(
            syncStatus: SyncStatus,
            modifying: Boolean,
            @IntRange(from = 1) limit: Int
    ): List<GameRecordDb>

    @Transaction
    @Query("""
        SELECT * FROM ${GameStateDb.GAME_RECORDS_TABLE}
        WHERE ${GameStateDb.SYNC_STATUS} = :syncStatus
        AND ${GameStateDb.MODIFYING_NOW} = :modifying
        AND ${GameStateDb.LAST_REMOTE_SYNCED_TIMESTAMP} < :maxLastRemoteSyncedTimestamp
        ORDER BY ${GameStateDb.LAST_REMOTE_SYNCED_TIMESTAMP} ASC
        LIMIT :limit
        """)
    protected abstract fun getWhereLastRemoteSyncSmallerThen(
            syncStatus: SyncStatus,
            modifying: Boolean,
            maxLastRemoteSyncedTimestamp: RemoteInfo.LastSyncedTimestamp,
            @IntRange(from = 1) limit: Int
    ): List<GameRecordDb>

    @Transaction
    @Query("""
        UPDATE ${GameStateDb.GAME_RECORDS_TABLE}
        SET ${GameStateDb.SYNC_STATUS} = :syncStatus
        WHERE ${GameStateDb.RECORD_ID} IN (:ids)
        AND ${GameStateDb.SYNC_STATUS} != :syncStatus
           """)
    protected abstract fun setSyncStatusByIds(syncStatus: SyncStatus, ids: List<Long>)

    @Transaction
    @Query("""
        SELECT * FROM ${GameStateDb.GAME_RECORDS_TABLE}
        WHERE ${GameStateDb.RECORD_ID} IN (:ids)
        """)
    protected abstract fun getRecordsByIds(ids: List<Long>): List<GameRecordDb>
}