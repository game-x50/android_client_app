package sync.usecases.localSynced

import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.toLocalDeletedOrThrow
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfo
import com.ruslan.hlushan.game.storage.impl.remote.dto.UpdateLocalNonModifiedResponse
import com.ruslan.hlushan.test.utils.generateFakeInstantTimestamp
import org.junit.Test
import org.threeten.bp.Instant
import utils.assertRecordsWithSyncStateInLocalRepo
import utils.generateAndAddLocalSyncedToLocalRepo
import utils.generateFakeRemoteRecord

internal class UpdateLocalSyncedUseCaseLocallyDeletedTest : BaseUpdateLocalSyncedUseCaseTest() {

    @Test
    fun testLocalLocallySyncedNoChangesDeleted() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        playRecordsInteractor.removeRecordById(localSyncedRecordId)
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
                lastLocalModifiedTimestamp = original.syncState.lastLocalModifiedTimestamp,
                modifyingNow = false
        )
                .toLocalDeletedOrThrow(recordAfterDeleteLocalActionId)

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(original.record, expectedFinal))
        )
    }

    @Test
    fun testLocalLocallySyncedChangedDeleted() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        playRecordsInteractor.removeRecordById(localSyncedRecordId)
                .subscribe()

        val remoteInfo = generateFakeRemoteInfo().copy(
                remoteId = original.syncState.remoteInfo!!.remoteId
        )

        val lastLocalModifiedTimestamp = generateFakeInstantTimestamp()

        val remoteRecord = generateFakeRemoteRecord(
                remoteInfo = remoteInfo,
                lastLocalModifiedTimestamp = lastLocalModifiedTimestamp,
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
                lastLocalModifiedTimestamp = lastLocalModifiedTimestamp,
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
    fun testLocalLocallySyncedDeletedDeleted() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        playRecordsInteractor.removeRecordById(localSyncedRecordId)
                .subscribe()

        val response = UpdateLocalNonModifiedResponse.Deleted(remoteId = original.syncState.remoteInfo!!.remoteId)

        updateLocalSyncedUseCase.handleResponse(original = original, response = response)
                .subscribe()

        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())
    }

    @Test
    fun testLocalLocallySyncedFailDeleted() {
        val original = localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = true, modifyingNow = false)
        val localSyncedRecordId = original.record.id

        playRecordsInteractor.removeRecordById(localSyncedRecordId)
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
                lastLocalModifiedTimestamp = original.syncState.lastLocalModifiedTimestamp,
                modifyingNow = false
        )
                .toLocalDeletedOrThrow(recordAfterDeleteLocalActionId)

        localRepo.assertRecordsWithSyncStateInLocalRepo(
                listOf(GameRecordWithSyncState(original.record, expectedFinal))
        )
    }
}