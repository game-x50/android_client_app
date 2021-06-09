package sync.usecases.localSynced

import assertRecordsWithSyncStateInLocalRepo
import com.ruslan.hlushan.game.core.api.play.dto.GameRecord
import com.ruslan.hlushan.game.core.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.core.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.core.api.play.dto.toLocalDeletedOrThrow
import com.ruslan.hlushan.game.core.api.test.utils.generateFakeGameState
import com.ruslan.hlushan.game.core.api.test.utils.generateFakeRemoteInfo
import com.ruslan.hlushan.game.storage.impl.remote.dto.UpdateLocalNonModifiedResponse
import com.ruslan.hlushan.test.utils.generateFakeDuration
import com.ruslan.hlushan.test.utils.generateFakeInstantTimestamp
import generateAndAddLocalSyncedToLocalRepo
import generateFakeRemoteRecord
import org.junit.Test
import org.threeten.bp.Instant

internal class UpdateLocalSyncedUseCaseLocallyModifiedAndDeletedTest : BaseUpdateLocalSyncedUseCaseTest() {

    @Test
    fun testLocalLocallySyncedNoChangesModifiedAndDeleted() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val localModifiedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = localModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.removeRecordById(localSyncedRecordId))
                .subscribe()

        val recordAfterDeleteLocalActionId = localRepo.getAll()
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

        val expectedFinal = RecordSyncState.forSync(
                remoteInfo = original.syncState.remoteInfo!!.copy(
                        lastRemoteSyncedTimestamp = lastRemoteSyncedTimestamp
                ),
                lastLocalModifiedTimestamp = localModifiedTimestamp,
                modifyingNow = false
        )
                .toLocalDeletedOrThrow(recordAfterDeleteLocalActionId)

        val expectedFinalGameRecord = GameRecord(localSyncedRecordId, updatedGameState, updatedTotalPlayed)

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(expectedFinalGameRecord, expectedFinal))
        )
    }

    @Test
    fun testLocalLocallySyncedChangedModifiedAndDeleted() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id
        val localModifiedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = generateFakeGameState(),
                            totalPlayed = generateFakeDuration(),
                            localModifiedTimestamp = localModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.removeRecordById(localSyncedRecordId))
                .subscribe()

        val remoteInfo = generateFakeRemoteInfo().copy(
                remoteId = original.syncState.remoteInfo!!.remoteId
        )

        val localModifiedTimestampFromRemote = generateFakeInstantTimestamp()

        val remoteRecord = generateFakeRemoteRecord(
                remoteInfo = remoteInfo,
                lastLocalModifiedTimestamp = localModifiedTimestampFromRemote,
        )

        val response = UpdateLocalNonModifiedResponse.Changed(
                remoteId = original.syncState.remoteInfo!!.remoteId,
                remoteRecord = remoteRecord
        )

        updateLocalSyncedUseCase.handleResponse(original = original, response = response)
                .subscribe()

        val expectedFinal = RecordSyncState.forSync(
                remoteInfo = remoteRecord.remoteInfo.copy(
                        lastRemoteSyncedTimestamp = remoteInfo.lastRemoteSyncedTimestamp
                ),
                lastLocalModifiedTimestamp = localModifiedTimestampFromRemote,
                modifyingNow = false
        )

        val localUpdatedRecord = GameRecord(
                id = localSyncedRecordId,
                gameState = remoteRecord.gameState,
                totalPlayed = remoteRecord.totalPlayed
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(localUpdatedRecord, expectedFinal))
        )
    }

    @Test
    fun testLocalLocallySyncedDeletedModifiedAndDeleted() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = generateFakeGameState(),
                            totalPlayed = generateFakeDuration(),
                            localModifiedTimestamp = generateFakeInstantTimestamp()
                    )
                }
                .andThen(playRecordsInteractor.removeRecordById(localSyncedRecordId))
                .subscribe()

        val response = UpdateLocalNonModifiedResponse.Deleted(remoteId = original.syncState.remoteInfo!!.remoteId)

        updateLocalSyncedUseCase.handleResponse(original = original, response = response)
                .subscribe()

        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())
    }

    @Test
    fun testLocalLocallySyncedFailModifiedAndDeleted() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLocalModifiedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.removeRecordById(localSyncedRecordId))
                .subscribe()

        val recordAfterDeleteLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        val response = UpdateLocalNonModifiedResponse.Fail(remoteId = original.syncState.remoteInfo!!.remoteId)

        updateLocalSyncedUseCase.handleResponse(original = original, response = response)
                .subscribe()

        val expectedFinal = RecordSyncState.forSync(
                remoteInfo = original.syncState.remoteInfo!!,
                lastLocalModifiedTimestamp = updatedLocalModifiedTimestamp,
                modifyingNow = false
        )
                .toLocalDeletedOrThrow(recordAfterDeleteLocalActionId)

        val expectedFinalGameRecord = GameRecord(localSyncedRecordId, updatedGameState, updatedTotalPlayed)

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(expectedFinalGameRecord, expectedFinal))
        )
    }
}