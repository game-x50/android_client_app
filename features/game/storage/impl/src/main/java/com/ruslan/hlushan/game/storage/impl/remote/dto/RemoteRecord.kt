package com.ruslan.hlushan.game.storage.impl.remote.dto

import com.ruslan.hlushan.game.api.play.dto.GameState
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.storage.impl.local.LocalUpdateRequest
import org.threeten.bp.Duration

internal data class RemoteRecord(
        val remoteInfo: RemoteInfo,
        val lastLocalModifiedTimestamp: RecordSyncState.LastLocalModifiedTimestamp,
        val gameState: GameState,
        val totalPlayed: Duration
)

internal fun RemoteRecord.toSyncLocalUpdateRequest(modifyingNow: Boolean): LocalUpdateRequest =
        LocalUpdateRequest(
                gameState = this.gameState,
                totalPlayed = this.totalPlayed,
                syncState = RecordSyncState.forSync(
                        remoteInfo = this.remoteInfo,
                        lastLocalModifiedTimestamp = this.lastLocalModifiedTimestamp,
                        modifyingNow = modifyingNow
                )
        )