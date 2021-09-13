package sync.stub

import androidx.annotation.IntRange
import com.ruslan.hlushan.core.extensions.clearAndAddAll
import com.ruslan.hlushan.core.extensions.replace
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.storage.impl.local.db.dao.GameRecordsDAO
import com.ruslan.hlushan.game.storage.impl.local.db.entities.GameRecordDb
import com.ruslan.hlushan.game.storage.impl.local.db.entities.GameStateDb
import com.ruslan.hlushan.game.storage.impl.local.db.entities.LocalActionTypeDb
import com.ruslan.hlushan.game.storage.impl.local.db.entities.MatrixAndNewItemsStateDb
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.Instant

internal class GameRecordsDAOStubImpl : GameRecordsDAO() {

    private val records: MutableList<GameStateDb> = mutableListOf()
    private val matrices: MutableMap<Long, List<MatrixAndNewItemsStateDb>> = mutableMapOf()

    private var newRecordId: Long = 1L

    fun getAll(): List<GameRecordDb> = records.map { rec -> GameRecordDb(rec, matrices[rec.recordId!!]!!) }

    override fun getRecordsExcludeOrderByTotalSumDesc(
            localActionType: LocalActionTypeDb,
            excludedIds: List<Long>,
            maxTotalSum: Int?,
            limit: Int
    ): Single<List<GameRecordDb>> = throw UnsupportedOperationException()

    override fun getRecordsExcludeOrderByTotalSumAsc(
            localActionType: LocalActionTypeDb,
            excludedIds: List<Long>,
            minTotalSum: Int?,
            limit: Int
    ): Single<List<GameRecordDb>> = throw UnsupportedOperationException()

    override fun getRecordsExcludeOrderByLastModifiedDesc(
            localActionType: LocalActionTypeDb,
            excludedIds: List<Long>,
            maxLastModified: Instant?,
            limit: Int
    ): Single<List<GameRecordDb>> = throw UnsupportedOperationException()

    override fun getRecordsExcludeOrderByLastModifiedAsc(
            localActionType: LocalActionTypeDb,
            excludedIds: List<Long>,
            minLastModified: Instant?,
            limit: Int
    ): Single<List<GameRecordDb>> = throw UnsupportedOperationException()

    override fun observeGameRecord(id: Long): Observable<List<GameRecordDb>> =
            throw UnsupportedOperationException()

    override fun getCountRecordsWhereSyncStatusNot(syncStatus: SyncStatus): Single<Int> =
            Single.fromCallable { records.filter { rec -> rec.syncStatus != syncStatus }.size }

    override fun setAllModifying(modifying: Boolean) {
        val updated = records.map { rec ->
            if (rec.modifyingNow != modifying) {
                rec.copy(modifyingNow = modifying)
            } else {
                rec
            }
        }

        records.clearAndAddAll(updated)
    }

    override fun getAllIdsWhereSyncStatus(syncStatus: SyncStatus): Single<List<Long>> =
            Single.fromCallable {
                records
                        .filter { rec -> rec.syncStatus == syncStatus }
                        .map { rec -> rec.recordId!! }
            }

    override fun getRecordById(id: Long): Single<GameRecordDb> =
            Single.fromCallable { GameRecordDb(findRecordById(id), matrices[id]!!) }

    override fun setModifyingById(id: Long, modifying: Boolean) {
        val old = findRecordById(id)
        val new = old.copy(modifyingNow = modifying)
        records.replace(old, new)
    }

    override fun setLocalCreatedIdById(id: Long, localCreatedId: String) {
        val old = findRecordById(id)
        val new = old.copy(localCreateId = localCreatedId)
        records.replace(old, new)
    }

    override fun getRemoteIdsWhereCreatedTimestampGraterOrEqual(
            minRemoteCreatedTimestamp: Instant
    ): Single<List<String>> =
            Single.fromCallable {
                records.filter { rec ->
                    val recRemoteCreatedTimestamp = rec.remoteCreatedTimestamp
                    ((recRemoteCreatedTimestamp != null) && (recRemoteCreatedTimestamp >= minRemoteCreatedTimestamp))
                }
                        .map { it.remoteId!! }
            }

    override fun updateSyncStateById(
            id: Long,
            remoteId: String?,
            remoteActionId: String?,
            remoteCreatedTimestamp: Instant?,
            lastRemoteSyncedTimestamp: Instant?,
            localActionType: LocalActionTypeDb?,
            lastLocalModifiedTimestamp: Instant,
            localActionId: String?,
            localCreateId: String?,
            modifyingNow: Boolean,
            syncStatus: SyncStatus
    ) {
        val old = findRecordById(id)
        val new = GameStateDb(
                recordId = id,
                gameSize = old.gameSize,
                totalSum = old.totalSum,
                totalPlayed = old.totalPlayed,
                remoteId = remoteId,
                remoteActionId = remoteActionId,
                remoteCreatedTimestamp = remoteCreatedTimestamp,
                lastRemoteSyncedTimestamp = lastRemoteSyncedTimestamp,
                localActionType = localActionType,
                lastLocalModifiedTimestamp = lastLocalModifiedTimestamp,
                localActionId = localActionId,
                localCreateId = localCreateId,
                modifyingNow = modifyingNow,
                syncStatus = syncStatus
        )
        records.replace(old, new)
    }

    override fun removeRecordById(id: Long) {
        @Suppress("NewApi")
        records.removeIf { rec -> rec.recordId == id }
        matrices.remove(id)
    }

    override fun saveState(record: GameStateDb): Long =
            if (record.recordId == null) {
                records.add(record.copy(recordId = ++newRecordId))
                newRecordId
            } else {
                val old = findRecordById(record.recordId)
                records.replace(old, record)
                record.recordId
            }

    override fun saveMatrices(matrices: List<MatrixAndNewItemsStateDb>) {
        this.matrices[matrices.first().recordId] = matrices
    }

    override fun getRecordsWithSyncAndPlayingStatus(
            syncStatus: SyncStatus,
            modifying: Boolean,
            @IntRange(from = 1) limit: Int
    ): List<GameRecordDb> =
            records.filter { rec -> rec.syncStatus == syncStatus && rec.modifyingNow == modifying }
                    .take(limit)
                    .map { rec -> GameRecordDb(rec, matrices[rec.recordId!!]!!) }

    override fun getWhereLastRemoteSyncSmallerThen(
            syncStatus: SyncStatus,
            modifying: Boolean,
            maxLastRemoteSyncedTimestamp: Instant,
            limit: Int
    ): List<GameRecordDb> =
            records.filter { rec ->
                rec.syncStatus == syncStatus
                && rec.modifyingNow == modifying
                && rec.lastRemoteSyncedTimestamp != null
                && rec.lastRemoteSyncedTimestamp < maxLastRemoteSyncedTimestamp
            }
                    .take(limit)
                    .map { rec -> GameRecordDb(rec, matrices[rec.recordId!!]!!) }

    override fun setSyncStatusByIds(syncStatus: SyncStatus, ids: List<Long>) {
        records.filter { rec -> ids.contains(rec.recordId) }
                .forEach { oldRec ->
                    records.replace(oldRec, oldRec.copy(syncStatus = syncStatus))
                }
    }

    override fun getRecordsByIds(ids: List<Long>): List<GameRecordDb> =
            records.filter { rec -> ids.contains(rec.recordId) }
                    .map { rec -> GameRecordDb(rec, matrices[rec.recordId!!]!!) }

    override fun deleteAll() {
        records.clear()
        matrices.clear()
    }

    private fun findRecordById(id: Long): GameStateDb = records.first { rec -> rec.recordId == id }
}