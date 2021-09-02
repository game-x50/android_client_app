package com.ruslan.hlushan.game.storage.impl.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import org.threeten.bp.Duration
import org.threeten.bp.Instant

@SuppressWarnings("LongParameterList")
@Entity(tableName = GameStateDb.GAME_RECORDS_TABLE,
        indices = [
            Index(name = "GameStateDbLastRemoteCreatedTimestampIndex",
                  unique = false,
                  value = [GameStateDb.REMOTE_CREATED_TIMESTAMP]
            ),
            Index(name = "GameStateDbLastRemoteSyncedTimestampIndex",
                  unique = false,
                  value = [GameStateDb.LAST_REMOTE_SYNCED_TIMESTAMP]
            ),
            Index(name = "GameStateDbLastLocalModifiedTimestampIndex",
                  unique = false,
                  value = [GameStateDb.LAST_LOCAL_MODIFIED_TIMESTAMP]
            ),
            Index(name = "GameStateDbSyncStatusTimestampIndex",
                  unique = false,
                  value = [GameStateDb.SYNC_STATUS]
            ),
            Index(name = "GameStateDbTotalSumIndex",
                  unique = false,
                  value = [GameStateDb.TOTAL_SUM]
            )
        ])
internal data class GameStateDb(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = RECORD_ID) val recordId: Long? = null,
        val gameSize: GameSize,
        @ColumnInfo(name = TOTAL_SUM) val totalSum: Int,
        val totalPlayed: Duration,
        // sync state
        @ColumnInfo(name = REMOTE_RECORD_ID) val remoteId: String?,
        @ColumnInfo(name = REMOTE_ACTION_ID) val remoteActionId: String?,
        @ColumnInfo(name = REMOTE_CREATED_TIMESTAMP) val remoteCreatedTimestamp: Instant?,
        @ColumnInfo(name = LAST_REMOTE_SYNCED_TIMESTAMP) val lastRemoteSyncedTimestamp: Instant?,
        @ColumnInfo(name = LOCAL_ACTION_TYPE) val localActionType: LocalActionTypeDb?,
        @ColumnInfo(name = LAST_LOCAL_MODIFIED_TIMESTAMP) val lastLocalModifiedTimestamp: Instant,
        @ColumnInfo(name = LOCAL_ACTION_ID) val localActionId: String?,
        @ColumnInfo(name = LOCAL_CREATE_ID) val localCreateId: String?,
        @ColumnInfo(name = MODIFYING_NOW) val modifyingNow: Boolean,
        @ColumnInfo(name = SYNC_STATUS) val syncStatus: SyncStatus
) {

    fun toSyncState(): RecordSyncState {
        @SuppressWarnings("ComplexCondition")
        val remoteInfo = if ((this.remoteId != null)
                             && (this.remoteActionId != null)
                             && (this.remoteCreatedTimestamp != null)
                             && (this.lastRemoteSyncedTimestamp != null)) {
            RemoteInfo(
                    remoteId = this.remoteId,
                    remoteActionId = this.remoteActionId,
                    remoteCreatedTimestamp = this.remoteCreatedTimestamp,
                    lastRemoteSyncedTimestamp = this.lastRemoteSyncedTimestamp
            )
        } else {
            null
        }

        val localAction = if ((this.localActionType != null) && (this.localActionId != null)) {
            this.localActionType.toLocalAction(actionId = this.localActionId)
        } else {
            null
        }

        return RecordSyncState(
                remoteInfo = remoteInfo,
                localAction = localAction,
                lastLocalModifiedTimestamp = this.lastLocalModifiedTimestamp,
                localCreateId = this.localCreateId,
                modifyingNow = this.modifyingNow,
                syncStatus = this.syncStatus
        )
    }

    companion object {
        const val GAME_RECORDS_TABLE = "game_records"

        const val RECORD_ID = "record_id"
        const val TOTAL_SUM = "total_sum"

        const val REMOTE_RECORD_ID = "remote_record_id"
        const val REMOTE_ACTION_ID = "remote_action_id"
        const val REMOTE_CREATED_TIMESTAMP = "remote_created_timestamp"
        const val LAST_REMOTE_SYNCED_TIMESTAMP = "last_remote_synced_timestamp"
        const val LAST_LOCAL_MODIFIED_TIMESTAMP = "last_local_modified_timestamp"

        const val LOCAL_ACTION_TYPE = "local_action_type"
        const val LOCAL_ACTION_ID = "local_action_id"
        const val LOCAL_CREATE_ID = "local_create_id"

        const val SYNC_STATUS = "sync_status"
        const val MODIFYING_NOW = "modifying_now"
    }
}