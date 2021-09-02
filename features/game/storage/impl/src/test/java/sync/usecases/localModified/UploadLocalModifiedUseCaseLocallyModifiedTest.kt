package sync.usecases.localModified

import assertRecordsWithSyncStateInLocalRepo
import com.ruslan.hlushan.game.core.api.play.dto.GameRecord
import com.ruslan.hlushan.game.core.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.core.api.play.dto.LocalAction
import com.ruslan.hlushan.game.core.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.core.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.core.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.core.api.test.utils.createLocalUpdatedState
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
import com.ruslan.hlushan.game.core.api.test.utils.generateFakeGameState as generateFakeGameState1

/**
 * @author Ruslan Hlushan on 2019-05-31
 */
internal class UploadLocalModifiedUseCaseLocallyModifiedTest : BaseUploadLocalModifiedUseCaseTest() {

    @Test
    fun testLocalCreatedSuccessLocallyModified() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState1()
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

        val expectedFinalSyncState = createLocalUpdatedState(
                remoteInfo = response.remoteInfo,
                newLastLocalModifiedTimestamp = updatedLastLocalModifiedTimestamp,
                newLocalActionId = recordAfterPlayingLocalActionId
        )

        val expectedFinalCreatedRecord = GameRecord(
                id = localCreatedRecordId,
                gameState = updatedGameState,
                totalPlayed = updatedTotalPlayed
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(expectedFinalCreatedRecord, expectedFinalSyncState))
        )
    }

    @Test
    fun testLocalCreatedWasChangedLocallyModified() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState1()
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
                modifyingNow = false,
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
    fun testLocalCreatedFailLocallyModified() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState1()
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
                modifyingNow = false,
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
    fun testLocalUpdatedSuccessLocallyLocallyModified() {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localUpdatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState1()
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
                        remoteActionId = generateFakeStringId(),
                        remoteCreatedTimestamp = Instant.now().plusSeconds(10_000),
                        lastRemoteSyncedTimestamp = Instant.now().plusSeconds(10_000)
                )
        )

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState(
                remoteInfo = response.remoteInfo.copy(remoteId = original.syncState.remoteInfo!!.remoteId),
                localAction = LocalAction.Update(actionId = recordAfterUpdateLocalActionId),
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
    fun testLocalUpdatedFailLocallyLocallyModified() {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localUpdatedRecordId = original.record.id

        val updatedGameState = generateFakeGameState1()
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