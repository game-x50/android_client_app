package sync.usecases.localModified

import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.LocalAction
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.storage.impl.remote.dto.LocalModifiedResponse
import com.ruslan.hlushan.test.utils.generateFakeStringId
import generateAndAddLocalCreatedToLocalRepo
import generateAndAddLocalDeletedToLocalRepo
import generateAndAddLocalUpdatedToLocalRepo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.Instant
import kotlin.math.min

internal class UploadLocalModifiedUseCaseListTest : BaseUploadLocalModifiedUseCaseTest() {

    @Test
    fun testUploadAllEmptyList() {
        val disposable = uploadLocalModifiedUseCase.uploadAll(100)
                .subscribe()

        assertEquals(null, remoteRepo.receivedUploadLocalModifiedRequests)

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun testUploadAllExactlyOneStep() = testFewStepsSync(step = 10, count = 10)

    @Test
    fun testUploadAllMoreThenOneStepButSmallerThenTwoSteps() = testFewStepsSync(step = 10, count = 15)

    @Test
    fun testUploadAllExactlyTwoStep() = testFewStepsSync(step = 10, count = 20)

    @Test
    fun testUploadAllMoreThenTwoStepsButSmallerThenThreeSteps() = testFewStepsSync(step = 10, count = 25)

    @Test
    fun testUploadAllExactlyThreeSteps() = testFewStepsSync(step = 10, count = 30)

    @Test
    fun testUploadAllPartiallyFail() {
        val count = 10

        val partiallyFailToResponses = generateAndAddToLocalRepoFakesAndCreateResponses(
                count = count,
                failType = FailType.PARTIALLY
        )

        assertEquals((1..count).map { SyncStatus.WAITING },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })

        val disposable = uploadLocalModifiedUseCase.uploadAll(stepCount = count)
                .subscribe()

        assertFalse(disposable.isDisposed)

        assertEquals((1..count).map { SyncStatus.SYNCHRONIZING },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })

        remoteRepo.returnListLocalModifiedResponses = partiallyFailToResponses.map { it.second }
        remoteRepo.advanceTimeToEndDelay()

        assertFalse(disposable.isDisposed)

        assertEquals(
                remoteRepo.returnListLocalModifiedResponses.mapNotNull { response ->
                    when (response) {
                        is LocalModifiedResponse.Fail                                    -> SyncStatus.SYNCHRONIZING
                        is LocalModifiedResponse.Delete                                  -> null
                        is LocalModifiedResponse.Create, is LocalModifiedResponse.Update -> SyncStatus.SYNCED
                    }
                },
                localRepo.getAll().map { item -> item.syncState.syncStatus }
        )

        remoteRepo.returnListLocalModifiedResponses = localRepo.getAll()
                .filter { recordWithSyncState -> recordWithSyncState.syncState.syncStatus != SyncStatus.SYNCED }
                .map(::generateFakeResponseForLocalModified)

        remoteRepo.advanceTimeToEndDelay()

        assertTrue(disposable.isDisposed)

        assertEquals(
                partiallyFailToResponses
                        .filter { it.first.syncState.localAction !is LocalAction.Delete }
                        .map { SyncStatus.SYNCED },
                localRepo.getAll().map { item -> item.syncState.syncStatus }
        )
    }

    @Test
    fun testUploadAllFailAll() {
        val count = 10

        val fullFailedToResponses = generateAndAddToLocalRepoFakesAndCreateResponses(
                count = count,
                failType = FailType.ALL
        )

        assertEquals(
                (1..count).map { SyncStatus.WAITING },
                localRepo.getAll().map { item -> item.syncState.syncStatus }
        )

        val disposable = uploadLocalModifiedUseCase.uploadAll(stepCount = count)
                .subscribe()

        assertFalse(disposable.isDisposed)

        assertEquals(
                (1..count).map { SyncStatus.SYNCHRONIZING },
                localRepo.getAll().map { item -> item.syncState.syncStatus }
        )

        remoteRepo.returnListLocalModifiedResponses = fullFailedToResponses.map { it.second }
        remoteRepo.advanceTimeToEndDelay()

        assertTrue(disposable.isDisposed)

        assertEquals(
                (1..count).map { SyncStatus.WAITING },
                localRepo.getAll().map { item -> item.syncState.syncStatus }
        )
    }

    @Test
    fun testUploadAllFailRemoteRequest() {
        val count = 10

        generateAndAddToLocalRepoFakesAndCreateResponses(count = count, failType = FailType.ALL)

        assertEquals(
                (1..count).map { SyncStatus.WAITING },
                localRepo.getAll().map { item -> item.syncState.syncStatus }
        )

        val disposable = uploadLocalModifiedUseCase.uploadAll(stepCount = count)
                .subscribe()

        assertFalse(disposable.isDisposed)

        assertEquals(
                (1..count).map { SyncStatus.SYNCHRONIZING },
                localRepo.getAll().map { item -> item.syncState.syncStatus }
        )

        remoteRepo.advanceTimeToEndDelay()

        assertTrue(disposable.isDisposed)

        assertEquals(
                (1..count).map { SyncStatus.WAITING },
                localRepo.getAll().map { item -> item.syncState.syncStatus }
        )
    }

    @Test
    fun testUploadAllDispose() {
        val count = 10

        generateAndAddToLocalRepoFakesAndCreateResponses(count = count, failType = FailType.NONE)

        assertEquals(
                (1..count).map { SyncStatus.WAITING },
                localRepo.getAll().map { item -> item.syncState.syncStatus }
        )

        val disposable = uploadLocalModifiedUseCase.uploadAll(stepCount = count)
                .subscribe()

        assertFalse(disposable.isDisposed)

        assertEquals(
                (1..count).map { SyncStatus.SYNCHRONIZING },
                localRepo.getAll().map { item -> item.syncState.syncStatus }
        )

        disposable.dispose()

        assertTrue(disposable.isDisposed)

        assertEquals(
                (1..count).map { SyncStatus.WAITING },
                localRepo.getAll().map { item -> item.syncState.syncStatus }
        )
    }

    private fun testFewStepsSync(step: Int, count: Int) {
        val originalsToResponses = generateAndAddToLocalRepoFakesAndCreateResponses(
                count = count,
                failType = FailType.NONE
        )

        assertEquals(
                (1..count).map { SyncStatus.WAITING },
                localRepo.getAll().map { item -> item.syncState.syncStatus }
        )

        val disposable = uploadLocalModifiedUseCase.uploadAll(step)
                .subscribe()

        (1..count step step).forEach { stepCounter ->

            assertFalse(disposable.isDisposed)

            val allPreviousStepResponses = originalsToResponses
                    .map { (state, response) -> response }
                    .subList(0, stepCounter - 1)

            val lastElementExcludedPosition = min(((stepCounter + step) - 1), count)

            val stepResponses = originalsToResponses
                    .map { (state, response) -> response }
                    .subList(stepCounter - 1, lastElementExcludedPosition)

            @Suppress("MaxLineLength")
            val expectedBeforeRemoteReturn = (
                    allPreviousStepResponses.filter { resp -> resp !is LocalModifiedResponse.Delete }.map { SyncStatus.SYNCED }
                    + (stepCounter..lastElementExcludedPosition).map { SyncStatus.SYNCHRONIZING }
                    + ((step + stepCounter)..count).map { SyncStatus.WAITING }
                                             )

            assertEquals(
                    expectedBeforeRemoteReturn,
                    localRepo.getAll().map { item -> item.syncState.syncStatus }
            )

            remoteRepo.returnListLocalModifiedResponses = stepResponses
            remoteRepo.advanceTimeToEndDelay()
        }

        assertTrue(disposable.isDisposed)

        assertEquals(
                originalsToResponses
                        .filter { it.second !is LocalModifiedResponse.Delete }
                        .map { SyncStatus.SYNCED },
                localRepo.getAll().map { item -> item.syncState.syncStatus }
        )
    }

    private enum class FailType {
        ALL, PARTIALLY, NONE
    }

    @SuppressWarnings("ComplexMethod")
    private fun generateAndAddToLocalRepoFakesAndCreateResponses(
            count: Int,
            failType: FailType
    ): List<Pair<GameRecordWithSyncState, LocalModifiedResponse>> =
            (1..count).map { index ->

                val shouldFail = when (failType) {
                    FailType.ALL       -> true
                    FailType.NONE      -> false
                    FailType.PARTIALLY -> (((index / 3) % 2) == 0)
                }

                when {
                    (index % 3 == 0) -> {
                        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(
                                syncingNow = false,
                                modifyingNow = false
                        )

                        val response = if (shouldFail) {
                            LocalModifiedResponse.Fail(id = original.record.id)
                        } else {
                            generateFakeResponseForLocalModified(original)
                        }

                        original to response
                    }
                    (index % 3 == 1) -> {
                        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(
                                syncingNow = false,
                                modifyingNow = false
                        )

                        val response = if (shouldFail) {
                            LocalModifiedResponse.Fail(id = original.record.id)
                        } else {
                            generateFakeResponseForLocalModified(original)
                        }

                        original to response
                    }
                    else             -> {
                        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = false)
                        val response = if (shouldFail) {
                            LocalModifiedResponse.Fail(id = original.record.id)
                        } else {
                            generateFakeResponseForLocalModified(original)
                        }

                        original to response
                    }
                }
            }

    private fun generateFakeResponseForLocalModified(
            recordWithSyncState: GameRecordWithSyncState
    ): LocalModifiedResponse {
        val remoteCreatedTimestamp = Instant.now()
        val lastRemoteSyncedTimestamp = Instant.now()

        return when (recordWithSyncState.syncState.localAction!!) {
            is LocalAction.Create -> LocalModifiedResponse.Create.Success(
                    id = recordWithSyncState.record.id,
                    remoteInfo = RemoteInfo(
                            remoteId = generateFakeStringId(),
                            remoteActionId = generateFakeStringId(),
                            remoteCreatedTimestamp = remoteCreatedTimestamp,
                            lastRemoteSyncedTimestamp = lastRemoteSyncedTimestamp
                    )
            )
            is LocalAction.Update -> LocalModifiedResponse.Update(
                    id = recordWithSyncState.record.id,
                    remoteInfo = RemoteInfo(
                            remoteId = recordWithSyncState.syncState.remoteInfo!!.remoteId,
                            remoteActionId = generateFakeStringId(),
                            remoteCreatedTimestamp = remoteCreatedTimestamp,
                            lastRemoteSyncedTimestamp = lastRemoteSyncedTimestamp
                    )
            )
            is LocalAction.Delete -> LocalModifiedResponse.Delete.Success(id = recordWithSyncState.record.id)
        }
    }
}