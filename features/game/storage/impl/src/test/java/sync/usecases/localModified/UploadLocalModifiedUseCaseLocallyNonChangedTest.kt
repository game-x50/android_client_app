package sync.usecases.localModified

import assertRecordsWithSyncStateInLocalRepo
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.LocalAction
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfo
import com.ruslan.hlushan.game.storage.impl.remote.dto.LocalModifiedResponse
import com.ruslan.hlushan.test.utils.generateFakeStringId
import generateAndAddLocalCreatedToLocalRepo
import generateAndAddLocalDeletedToLocalRepo
import generateAndAddLocalUpdatedToLocalRepo
import generateFakeRemoteRecord
import org.junit.Test
import org.threeten.bp.Instant

internal class UploadLocalModifiedUseCaseLocallyNonChangedTest : BaseUploadLocalModifiedUseCaseTest() {

    @Test
    fun testLocalCreatedSuccessLocallyNonChanged() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        val response = LocalModifiedResponse.Create.Success(
                id = localCreatedRecordId,
                remoteInfo = generateFakeRemoteInfo()
        )

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val newState = RecordSyncState.forSync(
                remoteInfo = response.remoteInfo,
                lastLocalModifiedTimestamp = original.syncState.lastLocalModifiedTimestamp,
                modifyingNow = false
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(GameRecordWithSyncState(original.record, newState)))
    }

    @Test
    fun testLocalCreatedWasChangedLocallyNonChanged() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        val remoteRecord = generateFakeRemoteRecord()

        val response = LocalModifiedResponse.Create.WasChanged(id = localCreatedRecordId, remoteRecord = remoteRecord)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val localCreatedUpdatedSyncState = RecordSyncState.forSync(
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
                listOf(GameRecordWithSyncState(localUpdatedRecord, localCreatedUpdatedSyncState))
        )
    }

    @Test
    fun testLocalCreatedFailLocallyNonChanged() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localCreatedRecordId = original.record.id

        val response = LocalModifiedResponse.Fail(id = localCreatedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val localCreatedUpdatedSyncState = RecordSyncState.forLocalCreated(
                localActionId = original.syncState.localAction!!.actionId,
                modifyingNow = false,
                localCreatedTimestamp = original.syncState.lastLocalModifiedTimestamp
        )
                .copy(localCreateId = original.syncState.localCreateId!!)

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(original.record, localCreatedUpdatedSyncState))
        )
    }

    @Test
    fun testLocalUpdatedSuccessLocallyNonChanged() {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localUpdatedRecordId = original.record.id

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

        val localCreatedUpdatedSyncState = RecordSyncState.forSync(
                remoteInfo = response.remoteInfo.copy(remoteId = original.syncState.remoteInfo!!.remoteId),
                lastLocalModifiedTimestamp = original.syncState.lastLocalModifiedTimestamp,
                modifyingNow = false
        )

        val localUpdatedRecord = GameRecord(
                id = localUpdatedRecordId,
                gameState = original.record.gameState,
                totalPlayed = original.record.totalPlayed
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(localUpdatedRecord, localCreatedUpdatedSyncState))
        )
    }

    @Test
    fun testLocalUpdatedFailLocallyNonChanged() {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localUpdatedRecordId = original.record.id

        val response = LocalModifiedResponse.Fail(id = localUpdatedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val localCreatedUpdatedSyncState = RecordSyncState(
                remoteInfo = original.syncState.remoteInfo,
                localAction = LocalAction.Update(actionId = original.syncState.localAction!!.actionId),
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
                listOf(GameRecordWithSyncState(localUpdatedRecord, localCreatedUpdatedSyncState))
        )
    }

    @Test
    fun testLocalDeletedSuccessLocallyNonChanged() {
        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = true)
        val localDeletedRecordId = original.record.id

        val response = LocalModifiedResponse.Delete.Success(id = localDeletedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())
    }

    @Test
    fun testLocalDeletedWasChangedLocallyNonChanged() {
        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = true)
        val localDeletedRecordId = original.record.id

        val remoteRecord = generateFakeRemoteRecord()

        val response = LocalModifiedResponse.Delete.WasChanged(id = localDeletedRecordId, remoteRecord = remoteRecord)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val localCreatedUpdatedSyncState = RecordSyncState.forSync(
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
                listOf(GameRecordWithSyncState(localUpdatedRecord, localCreatedUpdatedSyncState))
        )
    }

    @Test
    fun testLocalDeletedFailLocallyNonChanged() {
        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = true)
        val localDeletedRecordId = original.record.id

        val response = LocalModifiedResponse.Fail(id = localDeletedRecordId)

        uploadLocalModifiedUseCase.handleLocalModifiedResponse(original = original, response = response)
                .subscribe()

        val localCreatedUpdatedSyncState = RecordSyncState(
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
                listOf(GameRecordWithSyncState(localUpdatedRecord, localCreatedUpdatedSyncState))
        )
    }
}