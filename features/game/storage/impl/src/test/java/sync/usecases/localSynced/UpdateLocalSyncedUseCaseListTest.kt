package sync.usecases.localSynced

import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfo
import com.ruslan.hlushan.game.storage.impl.remote.dto.UpdateLocalNonModifiedResponse
import generateAndAddLocalSyncedToLocalRepo
import generateFakeRemoteRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.Instant
import kotlin.math.min

internal class UpdateLocalSyncedUseCaseListTest : BaseUpdateLocalSyncedUseCaseTest() {

    @Test
    fun testUpdateAllEmptyList() {
        val disposable = updateLocalSyncedUseCase.updateAll(Instant.now(), 100)
                .subscribe()

        assertEquals(null, remoteRepo.receivedUpdateLocalSyncedRequest)

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun testUpdateAllExactlyOneStep() = testFewStepsSync(step = 10, count = 10)

    @Test
    fun testUpdateAllMoreThenOneStepButSmallerThenTwoSteps() = testFewStepsSync(step = 10, count = 15)

    @Test
    fun testUpdateAllExactlyTwoStep() = testFewStepsSync(step = 10, count = 20)

    @Test
    fun tesUpdateAllMoreThenTwoStepsButSmallerThenThreeSteps() = testFewStepsSync(step = 10, count = 25)

    @Test
    fun testUpdateAllExactlyThreeSteps() = testFewStepsSync(step = 10, count = 30)

    @Test
    fun testUpdateAllNotEmptyButAllRecordsLastRemoteSyncGraterList() {

        val searchParamLastRemoteSyncedTimestamp: Instant = Instant.now()
        val dbRecordsLastRemoteSyncedTimestamp: Instant = searchParamLastRemoteSyncedTimestamp.plusMillis(1)

        val countRecords = 100

        repeat(times = countRecords) {
            localRepo.generateAndAddLocalSyncedToLocalRepo(
                    syncingNow = false,
                    modifyingNow = false,
                    remoteCreatedTimestamp = dbRecordsLastRemoteSyncedTimestamp,
                    lastRemoteSyncedTimestamp = dbRecordsLastRemoteSyncedTimestamp
            )
        }

        assertEquals(countRecords, localRepo.getAll().size)

        val disposable = updateLocalSyncedUseCase.updateAll(searchParamLastRemoteSyncedTimestamp, countRecords)
                .subscribe()

        assertEquals((1..countRecords).map { SyncStatus.SYNCED },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun testUpdateAllPartiallyFail() {
        val count = 10
        val searchParamLastRemoteSyncedTimestamp: Instant = Instant.now()
        val dbRecordsLastRemoteSyncedTimestamp: Instant = searchParamLastRemoteSyncedTimestamp.minusMillis(1)

        val partiallyFailToResponses = generateAndAddToLocalRepoFakesAndCreateResponses(
                count = count,
                failType = FailType.PARTIALLY,
                dbRecordsLastRemoteSyncedTimestamp = dbRecordsLastRemoteSyncedTimestamp
        )

        assertEquals((1..count).map { SyncStatus.SYNCED },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })

        val disposable = updateLocalSyncedUseCase.updateAll(
                maxLastRemoteSyncedTimestamp = searchParamLastRemoteSyncedTimestamp,
                stepCount = count
        )
                .subscribe()

        assertFalse(disposable.isDisposed)

        assertEquals((1..count).map { SyncStatus.SYNCHRONIZING },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })

        remoteRepo.returnListUpdateLocalNonModifiedResponses = partiallyFailToResponses.map { it.second }
        remoteRepo.advanceTimeToEndDelay()

        assertFalse(disposable.isDisposed)

        assertEquals(remoteRepo.returnListUpdateLocalNonModifiedResponses.mapNotNull { response ->
            when (response) {
                is UpdateLocalNonModifiedResponse.Fail    -> SyncStatus.SYNCHRONIZING
                is UpdateLocalNonModifiedResponse.Deleted -> null
                else                                      -> SyncStatus.SYNCED
            }
        },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })

        remoteRepo.returnListUpdateLocalNonModifiedResponses = localRepo.getAll()
                .filter { recordWithSyncState -> recordWithSyncState.syncState.syncStatus != SyncStatus.SYNCED }
                .map { original ->
                    UpdateLocalNonModifiedResponse.NoChanges(
                            remoteId = original.syncState.remoteInfo!!.remoteId,
                            lastRemoteSyncedTimestamp = Instant.now()
                    )
                }

        remoteRepo.advanceTimeToEndDelay()

        assertTrue(disposable.isDisposed)

        assertEquals(partiallyFailToResponses
                             .filter { it.second !is UpdateLocalNonModifiedResponse.Deleted }
                             .map { SyncStatus.SYNCED },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })
    }

    @Test
    fun testUpdateAllFailAll() {
        val count = 10
        val searchParamLastRemoteSyncedTimestamp: Instant = Instant.now()
        val dbRecordsLastRemoteSyncedTimestamp: Instant = searchParamLastRemoteSyncedTimestamp.minusMillis(1)

        val fullFailedToResponses = generateAndAddToLocalRepoFakesAndCreateResponses(
                count = count,
                failType = FailType.ALL,
                dbRecordsLastRemoteSyncedTimestamp = dbRecordsLastRemoteSyncedTimestamp
        )

        assertEquals((1..count).map { SyncStatus.SYNCED },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })

        val disposable = updateLocalSyncedUseCase.updateAll(
                maxLastRemoteSyncedTimestamp = searchParamLastRemoteSyncedTimestamp,
                stepCount = count
        )
                .subscribe()

        assertFalse(disposable.isDisposed)

        assertEquals((1..count).map { SyncStatus.SYNCHRONIZING },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })

        remoteRepo.returnListUpdateLocalNonModifiedResponses = fullFailedToResponses.map { it.second }
        remoteRepo.advanceTimeToEndDelay()

        assertTrue(disposable.isDisposed)

        assertEquals((1..count).map { SyncStatus.SYNCED },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })
    }

    @Test
    fun testUpdateAllFailRemoteRequest() {
        val count = 10
        val searchParamLastRemoteSyncedTimestamp: Instant = Instant.now()
        val dbRecordsLastRemoteSyncedTimestamp: Instant = searchParamLastRemoteSyncedTimestamp.minusMillis(1)

        generateAndAddToLocalRepoFakesAndCreateResponses(
                count = count,
                failType = FailType.NONE,
                dbRecordsLastRemoteSyncedTimestamp = dbRecordsLastRemoteSyncedTimestamp
        )

        assertEquals((1..count).map { SyncStatus.SYNCED },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })

        val disposable = updateLocalSyncedUseCase.updateAll(
                maxLastRemoteSyncedTimestamp = searchParamLastRemoteSyncedTimestamp,
                stepCount = count
        )
                .subscribe()

        assertFalse(disposable.isDisposed)

        assertEquals((1..count).map { SyncStatus.SYNCHRONIZING },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })

        remoteRepo.advanceTimeToEndDelay()

        assertTrue(disposable.isDisposed)

        assertEquals((1..count).map { SyncStatus.SYNCED },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })
    }

    private enum class FailType {
        ALL, PARTIALLY, NONE
    }

    private fun testFewStepsSync(step: Int, count: Int) {

        val searchParamLastRemoteSyncedTimestamp: Instant = Instant.now()
        val dbRecordsLastRemoteSyncedTimestamp: Instant = searchParamLastRemoteSyncedTimestamp.minusMillis(1)

        val originalsToResponses = generateAndAddToLocalRepoFakesAndCreateResponses(
                count = count,
                failType = FailType.NONE,
                dbRecordsLastRemoteSyncedTimestamp = dbRecordsLastRemoteSyncedTimestamp
        )

        assertEquals((1..count).map { SyncStatus.SYNCED },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })

        val disposable = updateLocalSyncedUseCase.updateAll(searchParamLastRemoteSyncedTimestamp, step)
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
                    allPreviousStepResponses.filter { resp -> resp !is UpdateLocalNonModifiedResponse.Deleted }.map { SyncStatus.SYNCED }
                    + (stepCounter..lastElementExcludedPosition).map { SyncStatus.SYNCHRONIZING }
                    + ((step + stepCounter)..count).map { SyncStatus.SYNCED }
                                             )
            assertEquals(expectedBeforeRemoteReturn,
                         localRepo.getAll().map { item -> item.syncState.syncStatus })

            remoteRepo.returnListUpdateLocalNonModifiedResponses = stepResponses
            remoteRepo.advanceTimeToEndDelay()
        }

        assertTrue(disposable.isDisposed)

        assertEquals(originalsToResponses
                             .filter { it.second !is UpdateLocalNonModifiedResponse.Deleted }
                             .map { SyncStatus.SYNCED },
                     localRepo.getAll().map { item -> item.syncState.syncStatus })
    }

    @SuppressWarnings("ComplexMethod")
    private fun generateAndAddToLocalRepoFakesAndCreateResponses(
            count: Int,
            failType: FailType,
            dbRecordsLastRemoteSyncedTimestamp: Instant
    ): List<Pair<GameRecordWithSyncState, UpdateLocalNonModifiedResponse>> {

        val lastRemoteSyncedTimestamp: Instant = Instant.now().plusMillis(129301)

        return (1..count).map { index ->

            val shouldFail = when (failType) {
                FailType.ALL       -> true
                FailType.NONE      -> false
                FailType.PARTIALLY -> (((index / 3) % 2) == 0)
            }

            val original = localRepo.generateAndAddLocalSyncedToLocalRepo(
                    syncingNow = false,
                    modifyingNow = false,
                    remoteCreatedTimestamp = dbRecordsLastRemoteSyncedTimestamp,
                    lastRemoteSyncedTimestamp = dbRecordsLastRemoteSyncedTimestamp
            )

            when {
                (index % 3 == 0) -> {
                    val response = if (shouldFail) {
                        UpdateLocalNonModifiedResponse.Fail(remoteId = original.syncState.remoteInfo!!.remoteId)
                    } else {
                        UpdateLocalNonModifiedResponse.NoChanges(
                                remoteId = original.syncState.remoteInfo!!.remoteId,
                                lastRemoteSyncedTimestamp = lastRemoteSyncedTimestamp
                        )
                    }

                    original to response
                }
                (index % 3 == 1) -> {
                    val response = if (shouldFail) {
                        UpdateLocalNonModifiedResponse.Fail(remoteId = original.syncState.remoteInfo!!.remoteId)
                    } else {
                        val remoteRecord = generateFakeRemoteRecord(
                                remoteInfo = generateFakeRemoteInfo().copy(
                                        remoteId = original.syncState.remoteInfo!!.remoteId
                                )
                        )

                        UpdateLocalNonModifiedResponse.Changed(
                                remoteId = original.syncState.remoteInfo!!.remoteId,
                                remoteRecord = remoteRecord
                        )
                    }

                    original to response
                }
                else             -> {
                    val response = if (shouldFail) {
                        UpdateLocalNonModifiedResponse.Fail(remoteId = original.syncState.remoteInfo!!.remoteId)
                    } else {
                        UpdateLocalNonModifiedResponse.Deleted(remoteId = original.syncState.remoteInfo!!.remoteId)
                    }

                    original to response
                }
            }
        }
    }
}