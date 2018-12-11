package sync.usecases.localModified

import assertRecordsWithSyncStateInLocalRepo
import com.ruslan.hlushan.game.core.api.play.dto.LocalAction
import com.ruslan.hlushan.game.storage.impl.remote.dto.UploadLocalModifiedRequest
import generateAndAddLocalCreatedToLocalRepo
import generateAndAddLocalDeletedToLocalRepo
import generateAndAddLocalUpdatedToLocalRepo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class UploadLocalModifiedUseCaseRemoteRepoReceivedParamsTest : BaseUploadLocalModifiedUseCaseTest() {

    @Test
    fun testRemoteRepoReceivedParamsCreatedNotModifying() =
            baseTestRemoteRepoReceivedParamsCreated(partlyModifyingNow = false)

    @Test
    fun testRemoteRepoReceivedParamsCreatedModifying() =
            baseTestRemoteRepoReceivedParamsCreated(partlyModifyingNow = true)

    @Test
    fun testRemoteRepoReceivedParamsUpdatedNotModifying() =
            baseTestRemoteRepoReceivedParamsUpdated(partlyModifyingNow = false)

    @Test
    fun testRemoteRepoReceivedParamsUpdatedModifying() =
            baseTestRemoteRepoReceivedParamsUpdated(partlyModifyingNow = true)

    @Test
    fun baseTestRemoteRepoReceivedParamsDeleted() {
        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())

        val stepCount = 10

        val originalsDeleted = (1..stepCount).map { localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = false) }

        val disposable = uploadLocalModifiedUseCase.uploadAll(stepCount = stepCount)
                .subscribe()

        assertFalse(disposable.isDisposed)

        val requests = originalsDeleted
                .map { rec ->
                    UploadLocalModifiedRequest.Deleted(
                            localRecordId = rec.record.id,
                            remoteId = rec.syncState.remoteInfo!!.remoteId,
                            remoteActionId = rec.syncState.remoteInfo!!.remoteActionId
                    )
                }

        assertEquals(requests, remoteRepo.receivedUploadLocalModifiedRequests)

        disposable.dispose()
        assertTrue(disposable.isDisposed)
    }

    @Test
    fun testRemoteRepoReceivedParamsMixedStepCountGraterThanRecordsCount() =
            baseTestRemoteRepoReceivedParamsMixed(stepCount = 10, countRecords = 8)

    @Test
    fun testRemoteRepoReceivedParamsMixedStepCountEqualRecordsCount() =
            baseTestRemoteRepoReceivedParamsMixed(stepCount = 10, countRecords = 10)

    @Test
    fun testRemoteRepoReceivedParamsMixedStepCountSmallerThanRecordsCount() =
            baseTestRemoteRepoReceivedParamsMixed(stepCount = 10, countRecords = 17)

    private fun baseTestRemoteRepoReceivedParamsCreated(partlyModifyingNow: Boolean) {
        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())

        val stepCount = 10

        val originalsCreated = (1..stepCount).map { index ->
            val modifyingNow: Boolean = (partlyModifyingNow && ((index % 2) == 0))
            localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = false, modifyingNow = modifyingNow)
        }

        val disposable = uploadLocalModifiedUseCase.uploadAll(stepCount = stepCount)
                .subscribe()

        assertFalse(disposable.isDisposed)

        val recordIdToLocalCreateIdPairs = localRepo.getAll()
                .filter { rec -> !rec.syncState.modifyingNow }
                .map { rec -> rec.record.id to rec.syncState.localCreateId!! }

        val requests = originalsCreated
                .filter { rec -> !rec.syncState.modifyingNow }
                .map { rec ->
                    val localCreateId: String = recordIdToLocalCreateIdPairs
                            .first { (recordId, localCreateId) -> recordId == rec.record.id }
                            .second

                    UploadLocalModifiedRequest.Created(record = rec.record,
                                                       localCreateId = localCreateId,
                                                       lastLocalModifiedTimestamp = rec.syncState.lastLocalModifiedTimestamp)
                }

        assertEquals(requests, remoteRepo.receivedUploadLocalModifiedRequests)

        disposable.dispose()
        assertTrue(disposable.isDisposed)
    }

    private fun baseTestRemoteRepoReceivedParamsUpdated(partlyModifyingNow: Boolean) {
        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())

        val stepCount = 10

        val originalsUpdated = (1..stepCount).map { index ->
            val modifyingNow: Boolean = (partlyModifyingNow && ((index % 2) == 0))
            localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = false, modifyingNow = modifyingNow)
        }

        val disposable = uploadLocalModifiedUseCase.uploadAll(stepCount = stepCount)
                .subscribe()

        assertFalse(disposable.isDisposed)

        val requests = originalsUpdated
                .filter { rec -> !rec.syncState.modifyingNow }
                .map { rec ->
                    UploadLocalModifiedRequest.Updated(
                            record = rec.record,
                            remoteId = rec.syncState.remoteInfo!!.remoteId,
                            remoteActionId = rec.syncState.remoteInfo!!.remoteActionId,
                            lastLocalModifiedTimestamp = rec.syncState.lastLocalModifiedTimestamp
                    )
                }

        assertEquals(requests, remoteRepo.receivedUploadLocalModifiedRequests)

        disposable.dispose()
        assertTrue(disposable.isDisposed)
    }

    private fun baseTestRemoteRepoReceivedParamsMixed(stepCount: Int, countRecords: Int) {
        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())

        val originalsMixed = (1..countRecords).map { index ->
            when (index % 3) {
                0    -> localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = false, modifyingNow = false)
                1    -> localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = false, modifyingNow = false)
                else -> localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = false)
            }
        }

        val disposable = uploadLocalModifiedUseCase.uploadAll(stepCount = stepCount)
                .subscribe()

        assertFalse(disposable.isDisposed)

        val recordIdToLocalCreateIdPairs = localRepo.getAll()
                .take(stepCount)
                .filter { rec -> rec.syncState.localAction is LocalAction.Create }
                .map { rec -> rec.record.id to rec.syncState.localCreateId!! }

        val requests = originalsMixed
                .take(stepCount)
                .map { rec ->
                    when (rec.syncState.localAction!!) {
                        is LocalAction.Create -> {
                            val localCreateId: String = recordIdToLocalCreateIdPairs
                                    .first { (recordId, localCreateId) -> recordId == rec.record.id }
                                    .second

                            UploadLocalModifiedRequest.Created(
                                    record = rec.record,
                                    localCreateId = localCreateId,
                                    lastLocalModifiedTimestamp = rec.syncState.lastLocalModifiedTimestamp
                            )
                        }
                        is LocalAction.Update -> {
                            UploadLocalModifiedRequest.Updated(
                                    record = rec.record,
                                    remoteId = rec.syncState.remoteInfo!!.remoteId,
                                    remoteActionId = rec.syncState.remoteInfo!!.remoteActionId,
                                    lastLocalModifiedTimestamp = rec.syncState.lastLocalModifiedTimestamp
                            )
                        }
                        is LocalAction.Delete -> {
                            UploadLocalModifiedRequest.Deleted(
                                    localRecordId = rec.record.id,
                                    remoteId = rec.syncState.remoteInfo!!.remoteId,
                                    remoteActionId = rec.syncState.remoteInfo!!.remoteActionId
                            )
                        }
                    }
                }

        assertEquals(requests, remoteRepo.receivedUploadLocalModifiedRequests)

        disposable.dispose()
        assertTrue(disposable.isDisposed)
    }
}