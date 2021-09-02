package sync.usecases.downloadNewRemoteCreated

import assertRecordsWithSyncStateInLocalRepo
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.storage.impl.local.LastCreatedTimestampWithExcludedRemoteIds
import com.ruslan.hlushan.game.storage.impl.remote.dto.RemoteRecord
import generateAndAddLocalSyncedToLocalRepo
import generateFakeRemoteRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class DownloadNewRemoteCreatedUseCaseTest : BaseDownloadNewRemoteCreatedUseCaseTest() {

    @Test
    fun testDownloadNewEmptyExcludedIdsSuccessNotEmptyReturnOneStep() {
        val stepLimit = 10
        val disposable = downloadNewRemoteCreatedUseCase.downloadNew(stepLimit = stepLimit)
                .subscribe()

        assertFalse(disposable.isDisposed)

        val newRecords = (1 until stepLimit)
                .map { generateFakeRemoteRecord() }

        remoteRepo.returnListRemoteRecords = newRecords
        remoteRepo.advanceTimeToEndDelay()

        assertTrue(disposable.isDisposed)
        assertEquals(
                newRecords.map { rec -> rec.remoteInfo.remoteCreatedTimestamp }.maxOrNull()!!,
                storage.lastCreatedTimestamp
        )

        assertEquals(
                newRecords.map { rec -> rec.remoteInfo.remoteId to SyncStatus.SYNCED },
                localRepo.getAll().map { rec -> rec.syncState.remoteInfo!!.remoteId to rec.syncState.syncStatus }
        )
    }

    @Test
    fun testDownloadNewEmptyExcludedIdsSuccessNotEmptyReturnFewStep() {
        val stepLimit = 10
        val disposable = downloadNewRemoteCreatedUseCase.downloadNew(stepLimit = stepLimit)
                .subscribe()

        val allNewRecords = mutableListOf<RemoteRecord>()

        for (@Suppress("UnusedPrivateMember") step in 1..10) {
            assertFalse(disposable.isDisposed)

            val newRecords = (1..stepLimit)
                    .map { generateFakeRemoteRecord() }

            allNewRecords.addAll(newRecords)

            remoteRepo.returnListRemoteRecords = newRecords
            remoteRepo.advanceTimeToEndDelay()

            assertFalse(disposable.isDisposed)
            assertEquals(
                    newRecords.map { rec -> rec.remoteInfo.remoteCreatedTimestamp }.maxOrNull()!!,
                    storage.lastCreatedTimestamp
            )

            assertEquals(
                    allNewRecords.map { rec -> rec.remoteInfo.remoteId to SyncStatus.SYNCED },
                    localRepo.getAll().map { rec -> rec.syncState.remoteInfo!!.remoteId to rec.syncState.syncStatus }
            )
        }

        val newRecords = (1 until stepLimit)
                .map { generateFakeRemoteRecord() }

        allNewRecords.addAll(newRecords)

        remoteRepo.returnListRemoteRecords = newRecords
        remoteRepo.advanceTimeToEndDelay()

        assertTrue(disposable.isDisposed)
        assertEquals(
                newRecords.map { rec -> rec.remoteInfo.remoteCreatedTimestamp }.maxOrNull()!!,
                storage.lastCreatedTimestamp
        )

        assertEquals(
                allNewRecords.map { rec -> rec.remoteInfo.remoteId to SyncStatus.SYNCED },
                localRepo.getAll().map { rec -> rec.syncState.remoteInfo!!.remoteId to rec.syncState.syncStatus }
        )
    }

    @Test
    fun testDownloadNewWithNotEmptyExcludedIdsSuccess() {
        val countInDb = 100
        repeat(times = countInDb) {
            localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = false, modifyingNow = false)
        }

        val startDbRecords = localRepo.getAll()

        val lastCreatedTimestamp = startDbRecords
                .map { rec -> rec.syncState.remoteInfo!!.remoteCreatedTimestamp }
                .sorted()
                .get(countInDb / 2)

        storage.lastCreatedTimestamp = lastCreatedTimestamp

        val expectedIds = localRepo.getAll()
                .filter { rec -> rec.syncState.remoteInfo!!.remoteCreatedTimestamp >= lastCreatedTimestamp }
                .map { rec -> rec.syncState.remoteInfo!!.remoteId }

        localRepo.getLastCreatedTimestampWithExcludedRemoteIds()
                .test()
                .assertNoErrors()
                .assertComplete()
                .assertValue(LastCreatedTimestampWithExcludedRemoteIds(lastCreatedTimestamp, expectedIds))

        val stepLimit = 10

        val disposable = downloadNewRemoteCreatedUseCase.downloadNew(stepLimit = stepLimit)
                .subscribe()

        assertFalse(disposable.isDisposed)

        val newRecords = (1 until stepLimit)
                .map { generateFakeRemoteRecord() }

        remoteRepo.returnListRemoteRecords = newRecords
        remoteRepo.advanceTimeToEndDelay()

        assertTrue(disposable.isDisposed)
        assertEquals(
                newRecords.map { rec -> rec.remoteInfo.remoteCreatedTimestamp }.maxOrNull()!!,
                storage.lastCreatedTimestamp
        )

        assertEquals(
                startDbRecords.map { rec -> rec.syncState.remoteInfo!!.remoteId }
                        .plus(newRecords.map { rec -> rec.remoteInfo.remoteId })
                        .map { rId -> rId to SyncStatus.SYNCED },
                localRepo.getAll().map { rec -> rec.syncState.remoteInfo!!.remoteId to rec.syncState.syncStatus }
        )
    }

    @Test
    fun testDownloadNewEmptyExcludedIdsSuccessEmptyReturn() {
        val previouslyLastCreatedTimestamp = storage.lastCreatedTimestamp
        val disposable = downloadNewRemoteCreatedUseCase.downloadNew(stepLimit = 10)
                .subscribe()

        assertFalse(disposable.isDisposed)

        remoteRepo.returnListRemoteRecords = emptyList()
        remoteRepo.advanceTimeToEndDelay()

        assertTrue(disposable.isDisposed)
        assertEquals(previouslyLastCreatedTimestamp, storage.lastCreatedTimestamp)

        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())
    }

    @Test
    fun testDownloadNewEmptyExcludedIdsSuccessError() {
        val previouslyLastCreatedTimestamp = storage.lastCreatedTimestamp
        val disposable = downloadNewRemoteCreatedUseCase.downloadNew(stepLimit = 10)
                .subscribe()

        assertFalse(disposable.isDisposed)

        remoteRepo.advanceTimeToEndDelay()

        assertTrue(disposable.isDisposed)
        assertEquals(previouslyLastCreatedTimestamp, storage.lastCreatedTimestamp)

        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())
    }
}