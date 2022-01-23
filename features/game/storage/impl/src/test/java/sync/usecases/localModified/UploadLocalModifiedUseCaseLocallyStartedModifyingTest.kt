package sync.usecases.localModified

import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.LocalAction
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.play.dto.toModifyingNowOrThrow
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfo
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfoActionId
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfoId
import com.ruslan.hlushan.game.storage.impl.remote.dto.LocalModifiedResponse
import org.junit.Test
import org.threeten.bp.Instant
import utils.assertRecordsWithSyncStateInLocalRepo
import utils.generateAndAddLocalCreatedToLocalRepo
import utils.generateAndAddLocalDeletedToLocalRepo
import utils.generateAndAddLocalUpdatedToLocalRepo
import utils.generateFakeRemoteRecord

internal class UploadLocalModifiedUseCaseLocallyStartedModifyingTest : BaseUploadLocalModifiedUseCaseTest() {

    @Test
    fun testLocalCreatedSuccessLocallyStartedModifying() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        playRecordsInteractor.updateAndGetRecordForPlaying(localCreatedRecordId)
                .subscribe()

        val response = LocalModifiedResponse.Create.Success(
                id = localCreatedRecordId,
                remoteInfo = generateFakeRemoteInfo()
        )

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinal = RecordSyncState.forSync(
                remoteInfo = response.remoteInfo,
                lastLocalModifiedTimestamp = original.syncState.lastLocalModifiedTimestamp,
                modifyingNow = true
        )
                .toModifyingNowOrThrow()

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(original.record, expectedFinal))
        )
    }

    @Test
    fun testLocalCreatedWasChangedLocallyStartedModifying() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        playRecordsInteractor.updateAndGetRecordForPlaying(localCreatedRecordId)
                .subscribe()

        val remoteRecord = generateFakeRemoteRecord()

        val response = LocalModifiedResponse.Create.WasChanged(id = localCreatedRecordId, remoteRecord = remoteRecord)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState.forLocalCreated(
                localActionId = original.syncState.localAction!!.actionId,
                modifyingNow = true,
                localCreatedTimestamp = original.syncState.lastLocalModifiedTimestamp
        )

        val expectedFinalCreatedRecord = GameRecord(
                id = localCreatedRecordId,
                gameState = original.record.gameState,
                totalPlayed = original.record.totalPlayed
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
    fun testLocalCreatedFailLocallyStartedModifying() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        playRecordsInteractor.updateAndGetRecordForPlaying(localCreatedRecordId)
                .subscribe()

        val response = LocalModifiedResponse.Fail(id = localCreatedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState.forLocalCreated(
                localActionId = original.syncState.localAction!!.actionId,
                modifyingNow = true,
                localCreatedTimestamp = original.syncState.lastLocalModifiedTimestamp
        )
                .copy(localCreateId = original.syncState.localCreateId!!)
        val expectedFinalCreatedRecord = GameRecord(
                id = localCreatedRecordId,
                gameState = original.record.gameState,
                totalPlayed = original.record.totalPlayed
        )
        val expectedFinalCreated = GameRecordWithSyncState(expectedFinalCreatedRecord, expectedFinalSyncState)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expectedFinalCreated))
    }

    @Test
    fun testLocalUpdatedSuccessLocallyStartedModifying() {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localUpdatedRecordId = original.record.id

        playRecordsInteractor.updateAndGetRecordForPlaying(localUpdatedRecordId)
                .subscribe()

        val response = LocalModifiedResponse.Update(
                id = localUpdatedRecordId,
                remoteInfo = RemoteInfo(
                        remoteId = generateFakeRemoteInfoId(),
                        remoteActionId = generateFakeRemoteInfoActionId(),
                        remoteCreatedTimestamp = RemoteInfo.CreatedTimestamp(Instant.now().plusSeconds(10_000)),
                        lastRemoteSyncedTimestamp = RemoteInfo.LastSyncedTimestamp(Instant.now().plusSeconds(10_000))
                )
        )

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState.forSync(
                remoteInfo = response.remoteInfo,
                lastLocalModifiedTimestamp = original.syncState.lastLocalModifiedTimestamp,
                modifyingNow = true
        )

        val localUpdatedRecord = GameRecord(
                id = localUpdatedRecordId,
                gameState = original.record.gameState,
                totalPlayed = original.record.totalPlayed
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(localUpdatedRecord, expectedFinalSyncState))
        )
    }

    @Test
    fun testLocalUpdatedFailLocallyStartedModifying() {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localUpdatedRecordId = original.record.id

        playRecordsInteractor.updateAndGetRecordForPlaying(localUpdatedRecordId)
                .subscribe()

        val response = LocalModifiedResponse.Fail(id = localUpdatedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState(
                remoteInfo = original.syncState.remoteInfo,
                localAction = LocalAction.Update(actionId = original.syncState.localAction!!.actionId),
                lastLocalModifiedTimestamp = original.syncState.lastLocalModifiedTimestamp,
                localCreateId = null,
                modifyingNow = true,
                syncStatus = SyncStatus.WAITING
        )

        val localUpdatedRecord = GameRecord(
                id = localUpdatedRecordId,
                gameState = original.record.gameState,
                totalPlayed = original.record.totalPlayed
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(localUpdatedRecord, expectedFinalSyncState))
        )
    }

    @Test
    fun testLocalDeletedSuccessLocallyStartedModifying() {
        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = true)
        val localDeletedRecordId = original.record.id

        playRecordsInteractor.updateAndGetRecordForPlaying(localDeletedRecordId)
                .test()
                .assertNotComplete()
                .assertError(IllegalStateException::class.java)

        val response = LocalModifiedResponse.Delete.Success(id = localDeletedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())
    }

    @Test
    fun testLocalDeletedWasChangedLocallyStartedModifying() {
        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = true)
        val localDeletedRecordId = original.record.id

        playRecordsInteractor.updateAndGetRecordForPlaying(localDeletedRecordId)
                .test()
                .assertNotComplete()
                .assertError(IllegalStateException::class.java)

        val remoteRecord = generateFakeRemoteRecord()

        val response = LocalModifiedResponse.Delete.WasChanged(id = localDeletedRecordId, remoteRecord = remoteRecord)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState.forSync(
                remoteInfo = remoteRecord.remoteInfo,
                lastLocalModifiedTimestamp = remoteRecord.lastLocalModifiedTimestamp,
                modifyingNow = false
        )

        val localUpdatedRecord = GameRecord(
                id = localDeletedRecordId,
                gameState = remoteRecord.gameState,
                totalPlayed = remoteRecord.totalPlayed
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(localUpdatedRecord, expectedFinalSyncState))
        )
    }

    @Test
    fun testLocalDeletedFailLocallyStartedModifying() {
        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = true)
        val localDeletedRecordId = original.record.id

        playRecordsInteractor.updateAndGetRecordForPlaying(localDeletedRecordId)
                .test()
                .assertNotComplete()
                .assertError(IllegalStateException::class.java)

        val response = LocalModifiedResponse.Fail(id = localDeletedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState(
                remoteInfo = original.syncState.remoteInfo,
                localAction = LocalAction.Delete(actionId = original.syncState.localAction!!.actionId),
                lastLocalModifiedTimestamp = original.syncState.lastLocalModifiedTimestamp,
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.WAITING
        )

        val localUpdatedRecord = GameRecord(
                id = localDeletedRecordId,
                gameState = original.record.gameState,
                totalPlayed = original.record.totalPlayed
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(localUpdatedRecord, expectedFinalSyncState))
        )
    }
}