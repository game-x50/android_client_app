package com.ruslan.hlushan.game.storage.impl.remote.dto.server

import com.ruslan.hlushan.game.core.api.play.dto.GameRecord
import com.ruslan.hlushan.game.core.api.play.dto.MatrixAndNewItemsState
import com.ruslan.hlushan.parsing.impl.utils.parsing.DurationAsSecondsSerializer
import kotlinx.serialization.Serializable
import org.threeten.bp.Duration

internal interface BaseRemoteApiGameInfo {

    val totalPlayedSeconds: Duration
    val countRowsAndColumns: Int
    val current: RemoteApiGameState
    val stack: List<RemoteApiGameState>

    @Serializable
    class Impl(
            @Serializable(with = DurationAsSecondsSerializer::class)
            override val totalPlayedSeconds: Duration,
            override val countRowsAndColumns: Int,
            override val current: RemoteApiGameState,
            override val stack: List<RemoteApiGameState>
    ) : BaseRemoteApiGameInfo
}

internal fun GameRecord.toBaseRemoteApiGameInfo(): BaseRemoteApiGameInfo.Impl =
        BaseRemoteApiGameInfo.Impl(
                totalPlayedSeconds = this.totalPlayed,
                countRowsAndColumns = this.gameState.current.immutableNumbersMatrix.gameSize.countRowsAndColumns,
                current = this.gameState.current.toRemoteApi(),
                stack = this.gameState.stack.map(MatrixAndNewItemsState::toRemoteApi)
        )