@file:Suppress("InvalidPackageDeclaration")

import com.ruslan.hlushan.game.core.api.play.dto.GameRecord
import com.ruslan.hlushan.game.core.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.core.api.play.dto.GameState
import com.ruslan.hlushan.game.core.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.core.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.core.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.core.api.play.dto.toLocalDeletedOrThrow
import com.ruslan.hlushan.game.core.api.test.utils.createLocalUpdatedState
import com.ruslan.hlushan.game.core.api.test.utils.generateFakeGameState
import com.ruslan.hlushan.game.core.api.test.utils.generateFakeRemoteInfo
import com.ruslan.hlushan.game.storage.impl.local.LocalUpdateRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.RemoteRecord
import com.ruslan.hlushan.test.utils.generateFakeDuration
import com.ruslan.hlushan.test.utils.generateFakeInstantTimestamp
import com.ruslan.hlushan.test.utils.generateFakeStringId
import org.junit.Assert.assertEquals
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import sync.stub.LocalRecordsRepoTestImpl

/**
 * @author Ruslan Hlushan on 2019-05-31
 */

internal fun generateFakeRemoteRecord(
        remoteInfo: RemoteInfo = generateFakeRemoteInfo(),
        lastLocalModifiedTimestamp: Instant = generateFakeInstantTimestamp(),
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

    val localActionId = generateFakeStringId()
    val localCreateId = generateFakeStringId()

    var syncState = RecordSyncState.forLocalCreated(
            localActionId = localActionId,
            modifyingNow = modifyingNow,
            localCreatedTimestamp = localCreatedTimestamp
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
    val lastLocalModifiedTimestamp = generateFakeInstantTimestamp()

    val localActionId1 = generateFakeStringId()

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
    val localActionId1 = generateFakeStringId()

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
    val remoteId = generateFakeStringId()
    val remoteActionId = generateFakeStringId()

    var localUpdatedState = RecordSyncState.forSync(
            remoteInfo = RemoteInfo(
                    remoteId = remoteId,
                    remoteActionId = remoteActionId,
                    remoteCreatedTimestamp = remoteCreatedTimestamp,
                    lastRemoteSyncedTimestamp = lastRemoteSyncedTimestamp
            ),
            modifyingNow = modifyingNow,
            lastLocalModifiedTimestamp = lastLocalModifiedTimestamp
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