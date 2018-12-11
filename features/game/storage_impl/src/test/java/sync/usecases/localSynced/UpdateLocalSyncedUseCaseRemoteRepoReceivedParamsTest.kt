package sync.usecases.localSynced

import assertRecordsWithSyncStateInLocalRepo
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.toUpdateLocalSyncedRequest
import generateAndAddLocalSyncedToLocalRepo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.Instant

internal class UpdateLocalSyncedUseCaseRemoteRepoReceivedParamsTest : BaseUpdateLocalSyncedUseCaseTest() {

    @Test
    fun testRemoteRepoReceivedParamsNotModifyingStepCountEqualCountRecords() =
            baseTestRemoteRepoReceivedParams(partlyModifyingNow = false, stepCount = 10, countRecords = 10)

    @Test
    fun testRemoteRepoReceivedParamsNotModifyingStepCountGraterThanCountRecords() =
            baseTestRemoteRepoReceivedParams(partlyModifyingNow = false, stepCount = 20, countRecords = 10)

    @Test
    fun testRemoteRepoReceivedParamsNotModifyingStepCountSmallerThanCountRecords() =
            baseTestRemoteRepoReceivedParams(partlyModifyingNow = false, stepCount = 10, countRecords = 20)

    @Test
    fun testRemoteRepoReceivedParamsModifyingStepCountEqualCountRecords() =
            baseTestRemoteRepoReceivedParams(partlyModifyingNow = true, stepCount = 10, countRecords = 10)

    @Test
    fun testRemoteRepoReceivedParamsModifyingStepCountGraterThanCountRecords() =
            baseTestRemoteRepoReceivedParams(partlyModifyingNow = true, stepCount = 20, countRecords = 10)

    @Test
    fun testRemoteRepoReceivedParamsModifyingStepCountSmallerThanCountRecords() =
            baseTestRemoteRepoReceivedParams(partlyModifyingNow = true, stepCount = 10, countRecords = 20)

    private fun baseTestRemoteRepoReceivedParams(partlyModifyingNow: Boolean, stepCount: Int, countRecords: Int) {
        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())

        val searchParamLastRemoteSyncedTimestamp: Instant = Instant.now()
        val dbRecordsLastRemoteSyncedTimestamp: Instant = searchParamLastRemoteSyncedTimestamp.minusMillis(1)

        val originals = (1..countRecords).map { index ->
            val modifyingNow: Boolean = (partlyModifyingNow && ((index % 2) == 0))
            localRepo.generateAndAddLocalSyncedToLocalRepo(
                    syncingNow = false,
                    modifyingNow = modifyingNow,
                    remoteCreatedTimestamp = dbRecordsLastRemoteSyncedTimestamp,
                    lastRemoteSyncedTimestamp = dbRecordsLastRemoteSyncedTimestamp
            )
        }

        val requests = originals
                .filter { rec -> !rec.syncState.modifyingNow }
                .take(stepCount)
                .map { rec -> rec.syncState.remoteInfo!!.toUpdateLocalSyncedRequest() }

        val disposable = updateLocalSyncedUseCase.updateAll(maxLastRemoteSyncedTimestamp = searchParamLastRemoteSyncedTimestamp,
                                                            stepCount = stepCount)
                .subscribe()

        assertFalse(disposable.isDisposed)

        assertEquals(requests, remoteRepo.receivedUpdateLocalSyncedRequest)

        disposable.dispose()
        assertTrue(disposable.isDisposed)
    }
}