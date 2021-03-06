package sync.usecases.localSynced

import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.LocalAction
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.test.utils.generateFakeGameState
import com.ruslan.hlushan.game.api.test.utils.generateFakeRecordSyncStateLastLocalModifiedTimestamp
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfo
import com.ruslan.hlushan.game.storage.impl.remote.dto.UpdateLocalNonModifiedResponse
import com.ruslan.hlushan.test.utils.generateFakeDuration
import org.junit.Test
import utils.assertRecordsWithSyncStateInLocalRepo
import utils.generateAndAddLocalSyncedToLocalRepo
import utils.generateFakeRemoteRecord

internal class UpdateLocalSyncedUseCaseLocallyModifiedTest : BaseUpdateLocalSyncedUseCaseTest() {

    @Test
    fun testLocalLocallySyncedNoChangesModified() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeRecordSyncStateLastLocalModifiedTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .subscribe()

        val recordAfterPlayingLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        val lastRemoteSyncedTimestamp = RemoteInfo.LastSyncedTimestamp.now()

        val response = UpdateLocalNonModifiedResponse.NoChanges(
                remoteId = original.syncState.remoteInfo!!.remoteId,
                lastRemoteSyncedTimestamp = lastRemoteSyncedTimestamp
        )

        updateLocalSyncedUseCase.handleResponse(original = original, response = response)
                .subscribe()

        val expectedFinal = RecordSyncState(
                remoteInfo = original.syncState.remoteInfo!!.copy(
                        lastRemoteSyncedTimestamp = lastRemoteSyncedTimestamp
                ),
                localAction = LocalAction.Update(actionId = recordAfterPlayingLocalActionId),
                lastLocalModifiedTimestamp = updatedLastLocalModifiedTimestamp,
                localCreateId = null,
                modifyingNow = false, syncStatus = SyncStatus.WAITING
        )

        val expectedRecord = GameRecord(localSyncedRecordId, updatedGameState, updatedTotalPlayed)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(GameRecordWithSyncState(expectedRecord, expectedFinal)))
    }

    @Test
    fun testLocalLocallySyncedChangedModified() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeRecordSyncStateLastLocalModifiedTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .subscribe()

        val recordAfterPlayingLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        val remoteRecord = generateFakeRemoteRecord(
                remoteInfo = generateFakeRemoteInfo().copy(remoteId = original.syncState.remoteInfo!!.remoteId),
        )

        val response = UpdateLocalNonModifiedResponse.Changed(
                remoteId = original.syncState.remoteInfo!!.remoteId,
                remoteRecord = remoteRecord
        )

        updateLocalSyncedUseCase.handleResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState.forLocalCreated(
                localActionId = recordAfterPlayingLocalActionId,
                modifyingNow = false,
                localCreatedTimestamp = updatedLastLocalModifiedTimestamp
        )
        val expectedFinalSyncedRecord = GameRecord(
                id = localSyncedRecordId,
                gameState = updatedGameState,
                totalPlayed = updatedTotalPlayed
        )
        val expectedFinalSynced = GameRecordWithSyncState(expectedFinalSyncedRecord, expectedFinalSyncState)

        val additionalSyncState = RecordSyncState.forSync(
                remoteInfo = remoteRecord.remoteInfo,
                lastLocalModifiedTimestamp = remoteRecord.lastLocalModifiedTimestamp,
                modifyingNow = false
        )

        val additionalRecord = GameRecord(
                id = localSyncedRecordId.inc(),
                gameState = remoteRecord.gameState,
                totalPlayed = remoteRecord.totalPlayed
        )
        val additional = GameRecordWithSyncState(additionalRecord, additionalSyncState)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expectedFinalSynced, additional))
    }

    @Test
    fun testLocalLocallySyncedDeletedModified() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeRecordSyncStateLastLocalModifiedTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .subscribe()

        val recordAfterPlayingLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        val response = UpdateLocalNonModifiedResponse.Deleted(remoteId = original.syncState.remoteInfo!!.remoteId)

        updateLocalSyncedUseCase.handleResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState.forLocalCreated(
                localActionId = recordAfterPlayingLocalActionId,
                modifyingNow = false,
                localCreatedTimestamp = updatedLastLocalModifiedTimestamp
        )
        val expectedFinalSyncedRecord = GameRecord(
                id = localSyncedRecordId,
                gameState = updatedGameState,
                totalPlayed = updatedTotalPlayed
        )
        val expectedFinalSynced = GameRecordWithSyncState(expectedFinalSyncedRecord, expectedFinalSyncState)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expectedFinalSynced))
    }

    @Test
    fun testLocalLocallySyncedFailModified() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeRecordSyncStateLastLocalModifiedTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .subscribe()

        val recordAfterPlayingLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        val response = UpdateLocalNonModifiedResponse.Fail(remoteId = original.syncState.remoteInfo!!.remoteId)

        updateLocalSyncedUseCase.handleResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState(
                remoteInfo = original.syncState.remoteInfo,
                localAction = LocalAction.Update(actionId = recordAfterPlayingLocalActionId),
                lastLocalModifiedTimestamp = updatedLastLocalModifiedTimestamp,
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.WAITING
        )

        val expectedFinalSyncedRecord = GameRecord(
                id = localSyncedRecordId,
                gameState = updatedGameState,
                totalPlayed = updatedTotalPlayed
        )
        val expectedFinalSynced = GameRecordWithSyncState(expectedFinalSyncedRecord, expectedFinalSyncState)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expectedFinalSynced))
    }
}