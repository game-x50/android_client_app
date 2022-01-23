package sync.usecases.localModified

import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.LocalAction
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.test.utils.generateFakeGameState
import com.ruslan.hlushan.game.api.test.utils.generateFakeRecordSyncStateLastLocalModifiedTimestamp
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfo
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfoActionId
import com.ruslan.hlushan.game.storage.impl.remote.dto.LocalModifiedResponse
import com.ruslan.hlushan.test.utils.generateFakeDuration
import org.junit.Test
import org.threeten.bp.Instant
import utils.assertRecordsWithSyncStateInLocalRepo
import utils.generateAndAddLocalCreatedToLocalRepo
import utils.generateAndAddLocalUpdatedToLocalRepo
import utils.generateFakeRemoteRecord

@Suppress("MaxLineLength")
internal class UploadLocalModifiedUseCaseLocallyModifiedAndStartedModifyingAgainTest : BaseUploadLocalModifiedUseCaseTest() {

    @Test
    fun testLocalCreatedSuccessLocallyModifiedAndStartedModifyingAgain() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeRecordSyncStateLastLocalModifiedTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localCreatedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.updateAndGetRecordForPlaying(localCreatedRecordId))
                .subscribe()

        val recordAfterPlayingLocalActionId = localRepo.getAll()
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

        val expectedFinal = RecordSyncState(
                remoteInfo = response.remoteInfo,
                localAction = LocalAction.Update(actionId = recordAfterPlayingLocalActionId),
                lastLocalModifiedTimestamp = updatedLastLocalModifiedTimestamp,
                localCreateId = null,
                modifyingNow = true,
                syncStatus = SyncStatus.WAITING
        )

        val expectedFinalCreatedRecord = GameRecord(
                id = localCreatedRecordId,
                gameState = updatedGameState,
                totalPlayed = updatedTotalPlayed
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(expectedFinalCreatedRecord, expectedFinal))
        )
    }

    @Test
    fun testLocalCreatedWasChangedLocallyModifiedAndStartedModifyingAgain() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeRecordSyncStateLastLocalModifiedTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localCreatedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.updateAndGetRecordForPlaying(localCreatedRecordId))
                .subscribe()

        val recordAfterPlayingLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        val remoteRecord = generateFakeRemoteRecord()

        val response = LocalModifiedResponse.Create.WasChanged(id = localCreatedRecordId, remoteRecord = remoteRecord)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState.forLocalCreated(
                localActionId = recordAfterPlayingLocalActionId,
                modifyingNow = true,
                localCreatedTimestamp = updatedLastLocalModifiedTimestamp
        )

        val expectedFinalCreatedRecord = GameRecord(
                id = localCreatedRecordId,
                gameState = updatedGameState,
                totalPlayed = updatedTotalPlayed
        )

        val expectedFinalCreated = GameRecordWithSyncState(expectedFinalCreatedRecord, expectedFinalSyncState)

        val additionalSyncState = RecordSyncState.forSync(
                remoteInfo = remoteRecord.remoteInfo,
                lastLocalModifiedTimestamp = remoteRecord.lastLocalModifiedTimestamp,
                modifyingNow = false
        )

        val additionalRecord = GameRecord(
                id = localCreatedRecordId.inc(),
                gameState = remoteRecord.gameState,
                totalPlayed = remoteRecord.totalPlayed
        )

        val additional = GameRecordWithSyncState(additionalRecord, additionalSyncState)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expectedFinalCreated, additional))
    }

    @Test
    fun testLocalCreatedFailLocallyModifiedAndStartedModifyingAgain() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeRecordSyncStateLastLocalModifiedTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localCreatedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.updateAndGetRecordForPlaying(localCreatedRecordId))
                .subscribe()

        val recordAfterPlayingLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        val response = LocalModifiedResponse.Fail(id = localCreatedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState.forLocalCreated(
                localActionId = recordAfterPlayingLocalActionId,
                modifyingNow = true,
                localCreatedTimestamp = updatedLastLocalModifiedTimestamp
        )
                .copy(localCreateId = original.syncState.localCreateId!!)

        val expectedFinalCreatedRecord = GameRecord(
                id = localCreatedRecordId,
                gameState = updatedGameState,
                totalPlayed = updatedTotalPlayed
        )

        val expectedFinalCreated = GameRecordWithSyncState(expectedFinalCreatedRecord, expectedFinalSyncState)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expectedFinalCreated))
    }

    @Test
    fun testLocalUpdatedSuccessLocallyModifiedAndStartedModifyingAgain() {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localUpdatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeRecordSyncStateLastLocalModifiedTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localUpdatedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.updateAndGetRecordForPlaying(localUpdatedRecordId))
                .subscribe()

        val recordAfterUpdateLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        val response = LocalModifiedResponse.Update(
                id = localUpdatedRecordId,
                remoteInfo = RemoteInfo(
                        remoteId = original.syncState.remoteInfo!!.remoteId,
                        remoteActionId = generateFakeRemoteInfoActionId(),
                        remoteCreatedTimestamp = RemoteInfo.CreatedTimestamp(Instant.now().plusSeconds(10_000)),
                        lastRemoteSyncedTimestamp = RemoteInfo.LastSyncedTimestamp(Instant.now().plusSeconds(10_000))
                )
        )

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState(
                remoteInfo = response.remoteInfo.copy(remoteId = original.syncState.remoteInfo!!.remoteId),
                localAction = LocalAction.Update(actionId = recordAfterUpdateLocalActionId),
                lastLocalModifiedTimestamp = updatedLastLocalModifiedTimestamp,
                localCreateId = null,
                modifyingNow = true,
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
    fun testLocalUpdatedFailLocallyModifiedAndStartedModifyingAgain() {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localUpdatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeRecordSyncStateLastLocalModifiedTimestamp()

        playRecordsInteractor.updateAndGetRecordForPlaying(localUpdatedRecordId)
                .flatMapCompletable { playing ->
                    playRecordsInteractor.updateRecordAfterPlaying(
                            id = playing.id,
                            gameState = updatedGameState,
                            totalPlayed = updatedTotalPlayed,
                            localModifiedTimestamp = updatedLastLocalModifiedTimestamp
                    )
                }
                .andThen(playRecordsInteractor.updateAndGetRecordForPlaying(localUpdatedRecordId))
                .subscribe()

        val recordAfterUpdateLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        val response = LocalModifiedResponse.Fail(id = localUpdatedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState(
                remoteInfo = original.syncState.remoteInfo,
                localAction = LocalAction.Update(actionId = recordAfterUpdateLocalActionId),
                lastLocalModifiedTimestamp = updatedLastLocalModifiedTimestamp,
                localCreateId = null,
                modifyingNow = true,
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