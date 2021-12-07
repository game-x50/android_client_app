package sync.usecases.localModified

import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.LocalAction
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.play.dto.toLocalDeletedOrThrow
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfo
import com.ruslan.hlushan.game.storage.impl.remote.dto.LocalModifiedResponse
import com.ruslan.hlushan.test.utils.generateFakeStringId
import org.junit.Test
import org.threeten.bp.Instant
import utils.assertRecordsWithSyncStateInLocalRepo
import utils.generateAndAddLocalCreatedToLocalRepo
import utils.generateAndAddLocalDeletedToLocalRepo
import utils.generateAndAddLocalUpdatedToLocalRepo
import utils.generateFakeRemoteRecord

internal class UploadLocalModifiedUseCaseLocallyDeletedTest : BaseUploadLocalModifiedUseCaseTest() {

    @Test
    fun testLocalCreatedSuccessLocallyDeleted() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        playRecordsInteractor.removeRecordById(localCreatedRecordId)
                .subscribe()

        val recordAfterDeleteLocalActionId = localRepo.getAll()
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

        val expectedFinal = RecordSyncState.forSync(
                remoteInfo = response.remoteInfo,
                lastLocalModifiedTimestamp = original.syncState.lastLocalModifiedTimestamp,
                modifyingNow = false
        )
                .toLocalDeletedOrThrow(recordAfterDeleteLocalActionId)

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(original.record, expectedFinal))
        )
    }

    @Test
    fun testLocalCreatedWasChangedLocallyDeleted() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        playRecordsInteractor.removeRecordById(localCreatedRecordId)
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
    fun testLocalCreatedFailLocallyDeleted() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        playRecordsInteractor.removeRecordById(localCreatedRecordId)
                .subscribe()

        val response = LocalModifiedResponse.Fail(localCreatedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())
    }

    @Test
    fun testLocalUpdatedSuccessLocallyDeleted() {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localUpdatedRecordId = original.record.id

        playRecordsInteractor.removeRecordById(localUpdatedRecordId)
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
                lastLocalModifiedTimestamp = original.syncState.lastLocalModifiedTimestamp,
                localAction = LocalAction.Delete(actionId = recordAfterDeleteLocalActionId),
                localCreateId = null,
                modifyingNow = false,
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
    fun testLocalUpdatedFailLocallyDeleted() {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localUpdatedRecordId = original.record.id

        playRecordsInteractor.removeRecordById(localUpdatedRecordId)
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
                lastLocalModifiedTimestamp = original.syncState.lastLocalModifiedTimestamp,
                localCreateId = null,
                modifyingNow = false,
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
    fun testLocalDeletedSuccessLocallyDeleted() {
        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = true)
        val localDeletedRecordId = original.record.id

        playRecordsInteractor.removeRecordById(localDeletedRecordId)
                .test()
                .assertNotComplete()
                .assertError(IllegalStateException::class.java)

        val response = LocalModifiedResponse.Delete.Success(id = localDeletedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())
    }

    @Test
    fun testLocalDeletedWasChangedLocallyDeleted() {
        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = true)
        val localDeletedRecordId = original.record.id

        playRecordsInteractor.removeRecordById(localDeletedRecordId)
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
    fun testLocalDeletedFailLocallyDeleted() {
        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = true)
        val localDeletedRecordId = original.record.id

        playRecordsInteractor.removeRecordById(localDeletedRecordId)
                .test()
                .assertNotComplete()
                .assertError(IllegalStateException::class.java)

        val response = LocalModifiedResponse.Fail(id = localDeletedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val expectedFinal = original.copy(syncState = original.syncState.copy(syncStatus = SyncStatus.WAITING))

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expectedFinal))
    }
}