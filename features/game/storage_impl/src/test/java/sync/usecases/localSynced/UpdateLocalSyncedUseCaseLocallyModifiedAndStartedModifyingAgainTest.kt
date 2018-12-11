package sync.usecases.localSynced

import assertRecordsWithSyncStateInLocalRepo
import com.ruslan.hlushan.game.core.api.play.dto.GameRecord
import com.ruslan.hlushan.game.core.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.core.api.play.dto.LocalAction
import com.ruslan.hlushan.game.core.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.core.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.core.api.test.utils.generateFakeGameState
import com.ruslan.hlushan.game.core.api.test.utils.generateFakeRemoteInfo
import com.ruslan.hlushan.game.storage.impl.remote.dto.UpdateLocalNonModifiedResponse
import com.ruslan.hlushan.test.utils.generateFakeDuration
import com.ruslan.hlushan.test.utils.generateFakeInstantTimestamp
import generateAndAddLocalSyncedToLocalRepo
import generateFakeRemoteRecord
import org.junit.Test
import org.threeten.bp.Instant

internal class UpdateLocalSyncedUseCaseLocallyModifiedAndStartedModifyingAgainTest : BaseUpdateLocalSyncedUseCaseTest() {

    @Test
    fun testLocalLocallySyncedNoChangesModifiedAndStartedModifyingAgain() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId))
                .subscribe()

        val recordAfterPlayingLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        val lastRemoteSyncedTimestamp = Instant.now()

        val response = UpdateLocalNonModifiedResponse.NoChanges(
                remoteId = original.syncState.remoteInfo!!.remoteId,
                lastRemoteSyncedTimestamp = lastRemoteSyncedTimestamp
        )

        updateLocalSyncedUseCase.handleResponse(original = original, response = response)
                .subscribe()

        val expectedFinal = RecordSyncState(
                remoteInfo = original.syncState.remoteInfo!!.copy(lastRemoteSyncedTimestamp = lastRemoteSyncedTimestamp),
                localAction = LocalAction.Update(actionId = recordAfterPlayingLocalActionId),
                lastLocalModifiedTimestamp = updatedLastLocalModifiedTimestamp,
                localCreateId = null,
                modifyingNow = true, syncStatus = SyncStatus.WAITING
        )

        val expectedRecord = GameRecord(localSyncedRecordId, updatedGameState, updatedTotalPlayed)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(GameRecordWithSyncState(expectedRecord, expectedFinal)))
    }

    @Test
    fun testLocalLocallySyncedChangedModifiedAndStartedModifyingAgain() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId))
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
                modifyingNow = true,
                localCreatedTimestamp = updatedLastLocalModifiedTimestamp
        )
        val expectedFinalSyncedRecord = GameRecord(
                id = localSyncedRecordId,
                gameState = updatedGameState,
                totalPlayed = updatedTotalPlayed
        )
        val expectedFinalSynced = GameRecordWithSyncState(expectedFinalSyncedRecord, expectedFinalSyncState)

        val additionalSyncState = RecordSyncState.forSync(
                remoteInfo = remoteRecord.remoteInfo.copy(remoteId = response.remoteId),
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
    fun testLocalLocallySyncedDeletedModifiedAndStartedModifyingAgain() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId))
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
                modifyingNow = true,
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
    fun testLocalLocallySyncedFailModifiedAndStartedModifyingAgain() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId))
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
                modifyingNow = true,
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