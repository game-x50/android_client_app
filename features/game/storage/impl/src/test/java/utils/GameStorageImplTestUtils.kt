package utils

import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.GameState
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.play.dto.toLocalDeletedOrThrow
import com.ruslan.hlushan.game.api.test.utils.createLocalUpdatedState
import com.ruslan.hlushan.game.api.test.utils.generateFakeGameState
import com.ruslan.hlushan.game.api.test.utils.generateFakeLocalActionId
import com.ruslan.hlushan.game.api.test.utils.generateFakeRecordSyncStateLastLocalModifiedTimestamp
import com.ruslan.hlushan.game.api.test.utils.generateFakeRecordSyncStateLocalCreateId
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfo
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfoActionId
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfoId
import com.ruslan.hlushan.game.storage.impl.local.LocalUpdateRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.RemoteRecord
import com.ruslan.hlushan.test.utils.generateFakeDuration
import com.ruslan.hlushan.test.utils.generateFakeInstantTimestamp
import org.junit.Assert.assertEquals
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import sync.stub.LocalRecordsRepoTestImpl

internal fun generateFakeRemoteRecord(
        remoteInfo: RemoteInfo = generateFakeRemoteInfo(),
        lastLocalModifiedTimestamp: RecordSyncState.LastLocalModifiedTimestamp =
                generateFakeRecordSyncStateLastLocalModifiedTimestamp(),
        gameState: GameState = generateFakeGameState(),
        totalPlayed: Duration = generateFakeDuration()
): RemoteRecord = RemoteRecord(
        remoteInfo = remoteInfo,
        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp,
        gameState = gameState,
        totalPlayed = totalPlayed
)

internal fun LocalRecordsRepoTestImpl.assertRecordsWithSyncStateInLocalRepo(records: List<GameRecordWithSyncState>) =
        assertEquals(records, this.getAll())

internal fun LocalRecordsRepoTestImpl.generateAndAddLocalCreatedToLocalRepo(
        syncingNow: Boolean,
        modifyingNow: Boolean,
        localCreatedTimestamp: Instant = generateFakeInstantTimestamp()
): GameRecordWithSyncState {

    val localActionId = generateFakeLocalActionId()
    val localCreateId = generateFakeRecordSyncStateLocalCreateId()

    var syncState = RecordSyncState.forLocalCreated(
            localActionId = localActionId,
            modifyingNow = modifyingNow,
            localCreatedTimestamp = RecordSyncState.LastLocalModifiedTimestamp(localCreatedTimestamp)
    )

    if (syncingNow) {
        syncState = syncState.copy(localCreateId = localCreateId, syncStatus = SyncStatus.SYNCHRONIZING)
    }

    val localCreatedGameState = generateFakeGameState()
    val localCreatedTotalPlayed = generateFakeDuration()

    this.addNewRecord(LocalUpdateRequest(
            gameState = localCreatedGameState,
            totalPlayed = localCreatedTotalPlayed,
            syncState = syncState
    ))
            .subscribe()

    val localCreatedRecordId = this.getAll()
            .last()
            .record
            .id

    return GameRecordWithSyncState(
            record = GameRecord(
                    id = localCreatedRecordId,
                    gameState = localCreatedGameState,
                    totalPlayed = localCreatedTotalPlayed
            ),
            syncState = syncState
    )
}

internal fun LocalRecordsRepoTestImpl.generateAndAddLocalUpdatedToLocalRepo(
        syncingNow: Boolean,
        modifyingNow: Boolean
): GameRecordWithSyncState {

    val remoteInfo = generateFakeRemoteInfo()
    val lastLocalModifiedTimestamp = generateFakeRecordSyncStateLastLocalModifiedTimestamp()

    val localActionId1 = generateFakeLocalActionId()

    var localUpdatedState = createLocalUpdatedState(
            remoteInfo = remoteInfo,
            newLocalActionId = localActionId1,
            newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp
    )

    if (syncingNow) {
        localUpdatedState = localUpdatedState.copy(syncStatus = SyncStatus.SYNCHRONIZING)
    }

    if (modifyingNow) {
        localUpdatedState = localUpdatedState.copy(modifyingNow = modifyingNow)
    }

    val localUpdatedGameState = generateFakeGameState()
    val localUpdatedTotalPlayed = generateFakeDuration()

    this.addNewRecord(LocalUpdateRequest(
            gameState = localUpdatedGameState,
            totalPlayed = localUpdatedTotalPlayed,
            syncState = localUpdatedState
    ))
            .subscribe()

    val localUpdatedRecordId = this.getAll()
            .last()
            .record
            .id

    return GameRecordWithSyncState(
            record = GameRecord(
                    id = localUpdatedRecordId,
                    gameState = localUpdatedGameState,
                    totalPlayed = localUpdatedTotalPlayed
            ),
            syncState = localUpdatedState
    )
}

internal fun LocalRecordsRepoTestImpl.generateAndAddLocalDeletedToLocalRepo(
        syncingNow: Boolean
): GameRecordWithSyncState {
    val localActionId1 = generateFakeLocalActionId()

    var localDeletedState = createLocalUpdatedState()
            .toLocalDeletedOrThrow(newLocalActionId = localActionId1)

    if (syncingNow) {
        localDeletedState = localDeletedState.copy(syncStatus = SyncStatus.SYNCHRONIZING)
    }

    val localDeletedGameState = generateFakeGameState()
    val localDeletedTotalPlayed = generateFakeDuration()

    this.addNewRecord(LocalUpdateRequest(
            gameState = localDeletedGameState,
            totalPlayed = localDeletedTotalPlayed,
            syncState = localDeletedState
    ))
            .subscribe()

    val localDeletedRecordId = this.getAll()
            .last()
            .record
            .id

    return GameRecordWithSyncState(
            GameRecord(
                    id = localDeletedRecordId,
                    gameState = localDeletedGameState,
                    totalPlayed = localDeletedTotalPlayed
            ),
            localDeletedState
    )
}

internal fun LocalRecordsRepoTestImpl.generateAndAddLocalSyncedToLocalRepo(
        syncingNow: Boolean,
        modifyingNow: Boolean,
        remoteCreatedTimestamp: Instant = Instant.now(),
        lastRemoteSyncedTimestamp: Instant = Instant.now(),
        lastLocalModifiedTimestamp: Instant = Instant.now()
): GameRecordWithSyncState {
    val remoteId = generateFakeRemoteInfoId()
    val remoteActionId = generateFakeRemoteInfoActionId()

    var localUpdatedState = RecordSyncState.forSync(
            remoteInfo = RemoteInfo(
                    remoteId = remoteId,
                    remoteActionId = remoteActionId,
                    remoteCreatedTimestamp = RemoteInfo.CreatedTimestamp(remoteCreatedTimestamp),
                    lastRemoteSyncedTimestamp = RemoteInfo.LastSyncedTimestamp(lastRemoteSyncedTimestamp)
            ),
            modifyingNow = modifyingNow,
            lastLocalModifiedTimestamp = RecordSyncState.LastLocalModifiedTimestamp(lastLocalModifiedTimestamp)
    )

    if (syncingNow) {
        localUpdatedState = localUpdatedState.copy(syncStatus = SyncStatus.SYNCHRONIZING)
    }

    val localUpdatedGameState = generateFakeGameState()
    val localUpdatedTotalPlayed = generateFakeDuration()

    this.addNewRecord(LocalUpdateRequest(
            gameState = localUpdatedGameState,
            totalPlayed = localUpdatedTotalPlayed,
            syncState = localUpdatedState
    ))
            .subscribe()

    val localUpdatedRecordId = this.getAll()
            .last()
            .record
            .id

    return GameRecordWithSyncState(
            record = GameRecord(
                    id = localUpdatedRecordId,
                    gameState = localUpdatedGameState,
                    totalPlayed = localUpdatedTotalPlayed
            ),
            syncState = localUpdatedState
    )
}