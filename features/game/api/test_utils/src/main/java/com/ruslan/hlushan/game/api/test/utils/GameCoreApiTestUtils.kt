package com.ruslan.hlushan.game.api.test.utils

import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.api.play.dto.GameState
import com.ruslan.hlushan.game.api.play.dto.ImmutableNumbersMatrix
import com.ruslan.hlushan.game.api.play.dto.LocalAction
import com.ruslan.hlushan.game.api.play.dto.MatrixAndNewItemsState
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.play.dto.toModifyingNowOrThrow
import com.ruslan.hlushan.game.api.play.dto.toNextModifiedAfterModifyingOrThrow
import com.ruslan.hlushan.test.utils.generateFakeInstantTimestamp
import com.ruslan.hlushan.test.utils.generateFakePositiveInt
import com.ruslan.hlushan.test.utils.generateFakeStringId
import org.threeten.bp.Instant

fun generateFakeMatrixAndNewItemsState(): MatrixAndNewItemsState {
    val size = GameSize.values()[(generateFakePositiveInt() % GameSize.values().size)]
    val newItems = listOf(generateFakePositiveInt(), generateFakePositiveInt())
    return MatrixAndNewItemsState(ImmutableNumbersMatrix.emptyForSize(size), newItems)
}

fun LocalAction.copyWithNewId(
        actionId: String
): LocalAction = when (this) {
    is LocalAction.Create -> this.copy(actionId = actionId)
    is LocalAction.Update -> this.copy(actionId = actionId)
    is LocalAction.Delete -> this.copy(actionId = actionId)
}

fun generateFakeGameState(): GameState =
        GameState(
                current = generateFakeMatrixAndNewItemsState(),
                stack = emptyList()
        )

fun generateFakeRemoteInfo(
        remoteId: String = generateFakeStringId(),
        remoteActionId: String = generateFakeStringId(),
        remoteCreatedTimestamp: Instant = Instant.now(),
        lastRemoteSyncedTimestamp: Instant = Instant.now()
): RemoteInfo =
        RemoteInfo(
                remoteId = remoteId,
                remoteActionId = remoteActionId,
                remoteCreatedTimestamp = remoteCreatedTimestamp,
                lastRemoteSyncedTimestamp = lastRemoteSyncedTimestamp
        )

fun createSyncedState(
        remoteInfo: RemoteInfo = generateFakeRemoteInfo(),
        lastLocalModifiedTimestamp: Instant = Instant.now()
): RecordSyncState =
        RecordSyncState.forSync(
                remoteInfo = remoteInfo,
                lastLocalModifiedTimestamp = lastLocalModifiedTimestamp,
                modifyingNow = false
        )

fun createSynchronizingState(
        remoteInfo: RemoteInfo = generateFakeRemoteInfo(),
        lastLocalModifiedTimestamp: Instant = Instant.now()
): RecordSyncState =
        createSyncedState(
                remoteInfo = remoteInfo,
                lastLocalModifiedTimestamp = lastLocalModifiedTimestamp
        ).copy(syncStatus = SyncStatus.SYNCHRONIZING)

fun createLocalUpdatedState(
        remoteInfo: RemoteInfo = generateFakeRemoteInfo(),
        newLocalActionId: String = generateFakeStringId(),
        newLastLocalModifiedTimestamp: Instant = generateFakeInstantTimestamp()
): RecordSyncState =
        RecordSyncState.forSync(
                remoteInfo = remoteInfo,
                lastLocalModifiedTimestamp = Instant.MIN,
                modifyingNow = false
        )
                .toModifyingNowOrThrow()
                .toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = newLocalActionId,
                        newLastLocalModifiedTimestamp = newLastLocalModifiedTimestamp
                )