package sync.usecases.localModified

import assertRecordsWithSyncStateInLocalRepo
import com.ruslan.hlushan.game.core.api.play.dto.GameRecord
import com.ruslan.hlushan.game.core.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.core.api.play.dto.LocalAction
import com.ruslan.hlushan.game.core.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.core.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.core.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.core.api.play.dto.toLocalDeletedOrThrow
import com.ruslan.hlushan.game.core.api.test.utils.generateFakeGameState
import com.ruslan.hlushan.game.core.api.test.utils.generateFakeRemoteInfo
import com.ruslan.hlushan.game.storage.impl.remote.dto.LocalModifiedResponse
import com.ruslan.hlushan.test.utils.generateFakeDuration
import com.ruslan.hlushan.test.utils.generateFakeInstantTimestamp
import com.ruslan.hlushan.test.utils.generateFakeStringId
import generateAndAddLocalCreatedToLocalRepo
import generateAndAddLocalUpdatedToLocalRepo
import generateFakeRemoteRecord
import org.junit.Test
import org.threeten.bp.Instant

internal class UploadLocalModifiedUseCaseLocallyModifiedAndDeletedTest : BaseUploadLocalModifiedUseCaseTest() {

    @Test
    fun testLocalCreatedSuccessLocallyModifiedAndDeleted() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localCreatedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.removeRecordById(localCreatedRecordId))
                .subscribe()

        val recordAfterPlayingAndDeletingLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        val response = LocalModifiedResponse.Create.Success(
                id = localCreatedRecordId,
                remoteInfo = generateFakeRemoteInfo()
        )

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalState = RecordSyncState.forSync(
                remoteInfo = response.remoteInfo,
                lastLocalModifiedTimestamp = updatedLastLocalModifiedTimestamp,
                modifyingNow = false
        )
                .toLocalDeletedOrThrow(recordAfterPlayingAndDeletingLocalActionId)

        val expectedFinalGameRecord = GameRecord(localCreatedRecordId, updatedGameState, updatedTotalPlayed)

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(expectedFinalGameRecord, expectedFinalState))
        )
    }

    @Test
    fun testLocalCreatedWasChangedLocallyModifiedAndDeleted() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localCreatedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.removeRecordById(localCreatedRecordId))
                .subscribe()

        val remoteRecord = generateFakeRemoteRecord()

        val response = LocalModifiedResponse.Create.WasChanged(id = localCreatedRecordId, remoteRecord = remoteRecord)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState.forSync(
                remoteInfo = remoteRecord.remoteInfo,
                lastLocalModifiedTimestamp = remoteRecord.lastLocalModifiedTimestamp,
                modifyingNow = false
        )

        val localUpdatedRecord = GameRecord(
                id = localCreatedRecordId,
                gameState = remoteRecord.gameState,
                totalPlayed = remoteRecord.totalPlayed
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(localUpdatedRecord, expectedFinalSyncState))
        )
    }

    @Test
    fun testLocalCreatedFailLocallyModifiedAndDeleted() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localCreatedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.removeRecordById(localCreatedRecordId))
                .subscribe()

        val response = LocalModifiedResponse.Fail(localCreatedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())
    }

    @Test
    fun testLocalUpdatedSuccessLocallyModifiedAndDeleted() {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localUpdatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localUpdatedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.removeRecordById(localUpdatedRecordId))
                .subscribe()

        val recordAfterDeleteLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        val response = LocalModifiedResponse.Update(
                id = localUpdatedRecordId,
                remoteInfo = RemoteInfo(
                        remoteId = original.syncState.remoteInfo!!.remoteId,
                        remoteActionId = generateFakeStringId(),
                        remoteCreatedTimestamp = Instant.now().plusSeconds(11_203),
                        lastRemoteSyncedTimestamp = Instant.now().plusSeconds(11_203)
                )
        )

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState(
                remoteInfo = response.remoteInfo.copy(remoteId = original.syncState.remoteInfo!!.remoteId),
                localAction = LocalAction.Delete(actionId = recordAfterDeleteLocalActionId),
                lastLocalModifiedTimestamp = updatedLastLocalModifiedTimestamp,
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.WAITING
        )

        val localUpdatedRecord = GameRecord(
                id = localUpdatedRecordId,
                gameState = updatedGameState,
                totalPlayed = updatedTotalPlayed
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(localUpdatedRecord, expectedFinalSyncState))
        )
    }

    @Test
    fun testLocalUpdatedFailLocallyModifiedAndDeleted() {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localUpdatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localUpdatedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.removeRecordById(localUpdatedRecordId))
                .subscribe()

        val recordAfterDeleteLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        val response = LocalModifiedResponse.Fail(localUpdatedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState(
                remoteInfo = original.syncState.remoteInfo,
                localAction = LocalAction.Delete(actionId = recordAfterDeleteLocalActionId),
                lastLocalModifiedTimestamp = updatedLastLocalModifiedTimestamp,
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.WAITING
        )

        val localUpdatedRecord = GameRecord(
                id = localUpdatedRecordId,
                gameState = updatedGameState,
                totalPlayed = updatedTotalPlayed
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(localUpdatedRecord, expectedFinalSyncState))
        )
    }
}