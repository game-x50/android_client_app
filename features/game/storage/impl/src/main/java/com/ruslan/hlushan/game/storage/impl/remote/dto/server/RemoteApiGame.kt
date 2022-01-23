package com.ruslan.hlushan.game.storage.impl.remote.dto.server

import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.api.play.dto.GameState
import com.ruslan.hlushan.game.api.play.dto.MatrixAndNewItemsState
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.storage.impl.remote.dto.RemoteRecord
import com.ruslan.hlushan.parsing.impl.utils.parsing.DurationAsSecondsSerializer
import com.ruslan.hlushan.parsing.impl.utils.parsing.InstantAsEpochMillisSerializer
import kotlinx.serialization.Serializable
import org.threeten.bp.Duration
import org.threeten.bp.Instant

@SuppressWarnings("LongParameterList")
@Serializable
internal class RemoteApiGame(
        val uid: String,
        @Serializable(with = DurationAsSecondsSerializer::class)
        override val totalPlayedSeconds: Duration,
        override val countRowsAndColumns: Int,
        val lastActionId: String,
        @Serializable(with = InstantAsEpochMillisSerializer::class)
        val createdTimestamp: Instant,
        @Serializable(with = InstantAsEpochMillisSerializer::class)
        val lastSyncedTimestamp: Instant,
        @Serializable(with = InstantAsEpochMillisSerializer::class)
        val lastLocalModifiedTimestamp: Instant,
        override val current: RemoteApiGameState,
        override val stack: List<RemoteApiGameState>
) : BaseRemoteApiGameInfo

internal fun RemoteApiGame.toRemoteRecord(): RemoteRecord {

    val gameSize: GameSize = GameSize.fromCountRowsAndColumns(this.countRowsAndColumns)
    val current: MatrixAndNewItemsState = this.current.toMatrixAndNewItemsState(gameSize)
    val stack: List<MatrixAndNewItemsState> = this.stack
            .map { remoteValue -> remoteValue.toMatrixAndNewItemsState(gameSize) }
    return RemoteRecord(
            remoteInfo = RemoteInfo(
                    remoteId = RemoteInfo.Id(this.uid),
                    remoteActionId = RemoteInfo.ActionId(this.lastActionId),
                    remoteCreatedTimestamp = RemoteInfo.CreatedTimestamp(this.createdTimestamp),
                    lastRemoteSyncedTimestamp = RemoteInfo.LastSyncedTimestamp(this.lastSyncedTimestamp)
            ),
            totalPlayed = this.totalPlayedSeconds,
            lastLocalModifiedTimestamp = RecordSyncState.LastLocalModifiedTimestamp(this.lastLocalModifiedTimestamp),
            gameState = GameState(current = current, stack = stack)
    )
}