package sync.usecases.localSynced

import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.LocalAction
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.test.utils.copyWithNewId
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfo
import com.ruslan.hlushan.game.storage.impl.remote.dto.UpdateLocalNonModifiedResponse
import org.junit.Assert.assertEquals
import org.junit.Test
import utils.assertRecordsWithSyncStateInLocalRepo
import utils.generateAndAddLocalSyncedToLocalRepo
import utils.generateFakeRemoteRecord

internal class UpdateLocalSyncedUseCaseLocallyStartedModifyingTest : BaseUpdateLocalSyncedUseCaseTest() {

    @Test
    fun testLocalLocallySyncedNoChangesStartedModifying() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .subscribe()

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
                localAction = null,
                lastLocalModifiedTimestamp = original.syncState.lastLocalModifiedTimestamp,
                localCreateId = null,
                modifyingNow = true,
                syncStatus = SyncStatus.SYNCED
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(original.record, expectedFinal))
        )
    }

    @Test
    fun testLocalLocallySyncedChangedStartedModifying() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .subscribe()

        val remoteRecord = generateFakeRemoteRecord(
                remoteInfo = generateFakeRemoteInfo().copy(remoteId = original.syncState.remoteInfo!!.remoteId),
        )

        val response = UpdateLocalNonModifiedResponse.Changed(
                remoteId = original.syncState.remoteInfo!!.remoteId,
                remoteRecord = remoteRecord
        )

        updateLocalSyncedUseCase.handleResponse(original = original, response = response)
                .subscribe()

        val expectedLocalActionId = LocalAction.Id("1234567890")

        val expectedFinalSyncState = RecordSyncState.forLocalCreated(
                localActionId = expectedLocalActionId,
                modifyingNow = true,
                localCreatedTimestamp = original.syncState.lastLocalModifiedTimestamp
        )
        val expectedFinalSynced = GameRecordWithSyncState(original.record, expectedFinalSyncState)

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

        @Suppress("MaxLineLength")
        assertEquals(listOf(expectedFinalSynced, additional),
                     localRepo.getAll()
                             .let { (first, second) ->
                                 val firstUpdated = first.copy(
                                         syncState = first.syncState.copy(
                                                 localAction = first.syncState.localAction!!.copyWithNewId(actionId = expectedLocalActionId)
                                         )
                                 )
                                 listOf(firstUpdated, second)
                             })
    }

    @Test
    fun testLocalLocallySyncedDeletedStartedModifying() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .subscribe()

        val response = UpdateLocalNonModifiedResponse.Deleted(remoteId = original.syncState.remoteInfo!!.remoteId)

        updateLocalSyncedUseCase.handleResponse(original = original, response = response)
                .subscribe()

        val expectedLocalActionId = LocalAction.Id("1234567890")

        val expectedFinalSyncState = RecordSyncState.forLocalCreated(
                localActionId = expectedLocalActionId,
                modifyingNow = true,
                localCreatedTimestamp = original.syncState.lastLocalModifiedTimestamp
        )
        val expectedFinalSynced = GameRecordWithSyncState(original.record, expectedFinalSyncState)

        @Suppress("MaxLineLength")
        assertEquals(expectedFinalSynced,
                     localRepo.getAll()
                             .let { (first) ->
                                 val firstUpdated = first.copy(
                                         syncState = first.syncState.copy(
                                                 localAction = first.syncState.localAction!!.copyWithNewId(actionId = expectedLocalActionId)
                                         )
                                 )
                                 firstUpdated
                             })
    }

    @Test
    fun testLocalLocallySyncedFailStartedModifying() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        playRecordsInteractor.updateAndGetRecordForPlaying(localSyncedRecordId)
                .subscribe()

        val response = UpdateLocalNonModifiedResponse.Fail(remoteId = original.syncState.remoteInfo!!.remoteId)

        updateLocalSyncedUseCase.handleResponse(original = original, response = response)
                .subscribe()

        val expectedFinalSyncState = RecordSyncState(
                remoteInfo = original.syncState.remoteInfo,
                localAction = null,
                lastLocalModifiedTimestamp = original.syncState.lastLocalModifiedTimestamp,
                localCreateId = null,
                modifyingNow = true,
                syncStatus = SyncStatus.SYNCED
        )

        val expectedFinalSynced = GameRecordWithSyncState(original.record, expectedFinalSyncState)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expectedFinalSynced))
    }
}