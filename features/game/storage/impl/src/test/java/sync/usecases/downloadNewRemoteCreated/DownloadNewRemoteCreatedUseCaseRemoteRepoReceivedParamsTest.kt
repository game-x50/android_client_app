package sync.usecases.downloadNewRemoteCreated

import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.GetNewRemoteCreatedRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.Instant
import utils.assertRecordsWithSyncStateInLocalRepo
import utils.generateAndAddLocalSyncedToLocalRepo

internal class DownloadNewRemoteCreatedUseCaseRemoteRepoReceivedParamsTest : BaseDownloadNewRemoteCreatedUseCaseTest() {

    @Test
    fun testRemoteRepoReceivedParamsEmptyExcludedRemoteIds() =
            baseTestRemoteRepoReceivedParamsNotEmptyExcludedRemoteIds(stepLimit = 14, count = 0)

    @Test
    fun testRemoteRepoReceivedParamsNotEmptyExcludedRemoteIdsSmalerThanStepLimit() =
            baseTestRemoteRepoReceivedParamsNotEmptyExcludedRemoteIds(stepLimit = 14, count = 8)

    @Test
    fun testRemoteRepoReceivedParamsNotEmptyExcludedRemoteIdsMoreThanStepLimit() =
            baseTestRemoteRepoReceivedParamsNotEmptyExcludedRemoteIds(stepLimit = 14, count = 19)

    private fun baseTestRemoteRepoReceivedParamsNotEmptyExcludedRemoteIds(stepLimit: Int, count: Int) {
        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())

        val lastCreatedTimestamp = Instant.ofEpochMilli(1_231_123)
        val localCreatedTimestamp = lastCreatedTimestamp.plusMillis(1)

        storage.lastCreatedTimestamp = RemoteInfo.CreatedTimestamp(lastCreatedTimestamp)

        val originals = (1..count).map {
            localRepo.generateAndAddLocalSyncedToLocalRepo(
                    syncingNow = false,
                    modifyingNow = false,
                    remoteCreatedTimestamp = localCreatedTimestamp,
                    lastRemoteSyncedTimestamp = localCreatedTimestamp
            )
        }

        val disposable = downloadNewRemoteCreatedUseCase.downloadNew(stepLimit = stepLimit)
                .subscribe()

        assertFalse(disposable.isDisposed)

        assertEquals(
                GetNewRemoteCreatedRequest(
                        lastCreatedTimestamp = lastCreatedTimestamp,
                        excludedRemoteIds = originals.map { recordWithSyncState ->
                            recordWithSyncState.syncState.remoteInfo!!.remoteId.value
                        },
                        limit = stepLimit
                ),
                remoteRepo.receivedGetNewRemoteCreatedRequest
        )

        disposable.dispose()
        assertTrue(disposable.isDisposed)
    }
}