package com.ruslan.hlushan.game.storage.impl.local.db.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.api.play.dto.GameState
import com.ruslan.hlushan.game.api.play.dto.MatrixAndNewItemsState
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.storage.impl.local.LocalUpdateRequest
import org.threeten.bp.Duration
import org.threeten.bp.Instant

@SuppressWarnings("DataClassShouldBeImmutable")
internal data class GameRecordDb(
        @Embedded
        var stateDb: GameStateDb = GameStateDb(
                recordId = null,
                gameSize = GameSize.SMALL,
                totalSum = 0,
                totalPlayed = Duration.ZERO,
                remoteId = null,
                remoteActionId = null,
                remoteCreatedTimestamp = null,
                lastRemoteSyncedTimestamp = null,
                localActionType = null,
                lastLocalModifiedTimestamp = Instant.MIN,
                localActionId = null,
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.SYNCED
        ),
        @Relation(parentColumn = GameStateDb.RECORD_ID, entityColumn = GameStateDb.RECORD_ID)
        var matrices: List<MatrixAndNewItemsStateDb> = emptyList()
) {

    fun fromDbRecord(): GameRecordWithSyncState {
        val recordId: Long = (this.stateDb.recordId
                              ?: throw IllegalStateException("recordId should't be null: ${this@GameRecordDb}"))

        val current: MatrixAndNewItemsState = this.matrices.last().fromDbModel(this.stateDb.gameSize)
        val stack: List<MatrixAndNewItemsState> = this.matrices.dropLast(1)
                .map { matrixStateDb -> matrixStateDb.fromDbModel(this.stateDb.gameSize) }

        val record = GameRecord(
                id = recordId,
                gameState = GameState(current = current, stack = stack),
                totalPlayed = this.stateDb.totalPlayed
        )

        val syncState = this.stateDb.toSyncState()
        return GameRecordWithSyncState(record = record, syncState = syncState)
    }
}

internal fun LocalUpdateRequest.toDbGameState(localRecordId: Long?): GameStateDb =
        GameStateDb(
                recordId = localRecordId,
                gameSize = this.gameState.current.immutableNumbersMatrix.gameSize,
                totalSum = this.gameState.current.immutableNumbersMatrix.totalSum,
                totalPlayed = this.totalPlayed,
                remoteId = this.syncState.remoteInfo?.remoteId,
                remoteActionId = this.syncState.remoteInfo?.remoteActionId,
                remoteCreatedTimestamp = this.syncState.remoteInfo?.remoteCreatedTimestamp,
                lastRemoteSyncedTimestamp = this.syncState.remoteInfo?.lastRemoteSyncedTimestamp,
                localActionType = this.syncState.localAction?.typeDb,
                lastLocalModifiedTimestamp = this.syncState.lastLocalModifiedTimestamp,
                localActionId = this.syncState.localAction?.actionId,
                localCreateId = this.syncState.localCreateId,
                modifyingNow = this.syncState.modifyingNow,
                syncStatus = this.syncState.syncStatus
        )

internal fun LocalUpdateRequest.toMatricesList(recordId: Long): List<MatrixAndNewItemsStateDb> =
        (this.gameState.stack.mapIndexed { index, matrix -> matrix.toDbModel(recordId, index) }
         + this.gameState.current.toDbModel(recordId, gameState.stack.size))