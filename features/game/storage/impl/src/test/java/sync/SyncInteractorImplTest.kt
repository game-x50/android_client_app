package sync

import assertRecordsWithSyncStateInLocalRepo
import com.ruslan.hlushan.core.api.dto.OperationResult
import com.ruslan.hlushan.core.api.test.utils.log.EmptyAppLoggerImpl
import com.ruslan.hlushan.core.api.test.utils.managers.CurrentThreadSchedulersManager
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfo
import com.ruslan.hlushan.game.storage.impl.DownloadNewRemoteCreatedUseCase
import com.ruslan.hlushan.game.storage.impl.SyncInteractorImpl
import com.ruslan.hlushan.game.storage.impl.UpdateLocalSyncedUseCase
import com.ruslan.hlushan.game.storage.impl.UploadLocalModifiedUseCase
import com.ruslan.hlushan.game.storage.impl.local.LastCreatedTimestampWithExcludedRemoteIds
import com.ruslan.hlushan.game.storage.impl.remote.dto.LocalModifiedResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.UpdateLocalNonModifiedResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.UploadLocalModifiedRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.GetNewRemoteCreatedRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.toUpdateLocalSyncedRequest
import com.ruslan.hlushan.rxjava2.test.utils.assertNotCompleteNoErrorsNoValues
import com.ruslan.hlushan.test.utils.generateFakeInstantTimestamp
import generateAndAddLocalCreatedToLocalRepo
import generateAndAddLocalSyncedToLocalRepo
import generateFakeRemoteRecord
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import sync.rule.TestLocalRecordsRepoCleanUpRule
import sync.rule.TestSyncRemoteRepositoryMockCleanUpRule
import sync.stub.LocalRecordsRepoTestImpl
import sync.stub.LocalRecordsRepositoryStorageMockImpl
import sync.stub.SyncRemoteRepositoryMockImpl

internal class SyncInteractorImplTest {

    private lateinit var localRepo: LocalRecordsRepoTestImpl
    private lateinit var localRecordsRepositoryStorage: LocalRecordsRepositoryStorageMockImpl
    private lateinit var remoteRepo: SyncRemoteRepositoryMockImpl

    private lateinit var syncInteractor: SyncInteractorImpl

    @Rule
    @JvmField
    val testLocalRecordsRepoCleanUpRule = TestLocalRecordsRepoCleanUpRule { localRepo }

    @Rule
    @JvmField
    val testSyncRemoteRepositoryMockCleanUpRule = TestSyncRemoteRepositoryMockCleanUpRule { remoteRepo }

    @Before
    fun before() {
        val scheduler = CurrentThreadSchedulersManager()
        val logger = EmptyAppLoggerImpl()

        localRecordsRepositoryStorage = LocalRecordsRepositoryStorageMockImpl()
        localRepo = LocalRecordsRepoTestImpl(localRecordsRepositoryStorage, scheduler)
        remoteRepo = SyncRemoteRepositoryMockImpl()

        val uploadLocalModifiedUseCase = UploadLocalModifiedUseCase(
                remoteSyncRepository = remoteRepo,
                localRepository = localRepo,
                appLogger = logger
        )

        val updateLocalSyncedUseCase = UpdateLocalSyncedUseCase(
                remoteSyncRepository = remoteRepo,
                localRepository = localRepo,
                appLogger = logger
        )

        val downloadNewRemoteCreatedUseCase = DownloadNewRemoteCreatedUseCase(
                remoteSyncRepository = remoteRepo,
                localRepository = localRepo,
                appLogger = logger
        )

        syncInteractor = SyncInteractorImpl(
                uploadLocalModifiedUseCase,
                updateLocalSyncedUseCase,
                downloadNewRemoteCreatedUseCase,
                localRepo,
                remoteRepo,
                logger
        )
    }

    @SuppressWarnings("MaxLineLength", "LongMethod")
    @Test
    fun checkChainAndRequestsParams() {
        val lastCreatedTimestamp = generateFakeInstantTimestamp()
        val timestampOfStart = lastCreatedTimestamp.plusMillis(1)
        val timestampAfterStart = timestampOfStart.plusMillis(1)

        localRecordsRepositoryStorage.lastCreatedTimestamp = lastCreatedTimestamp

        val originalCreated = localRepo.generateAndAddLocalCreatedToLocalRepo(
                syncingNow = false,
                modifyingNow = false
        )
        val originalSynced = localRepo.generateAndAddLocalSyncedToLocalRepo(
                syncingNow = false,
                modifyingNow = false,
                remoteCreatedTimestamp = lastCreatedTimestamp,
                lastRemoteSyncedTimestamp = lastCreatedTimestamp
        )

        val isSynchronizingObserver = localRepo.observeIsSynchronizing()
                .test()

        isSynchronizingObserver
                .assertNotComplete()
                .assertNoErrors()
                .assertValue(false)

        val testCompleteObserver = syncInteractor.sync()
                .test()

        isSynchronizingObserver
                .assertNotComplete()
                .assertNoErrors()
                .assertValues(false, true)

        testCompleteObserver.assertNotCompleteNoErrorsNoValues()
        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(originalCreated, originalSynced))

        remoteRepo.returnTimestampResult = OperationResult.Success(timestampOfStart)
        remoteRepo.advanceTimeToEndDelay()

        val originalCreatedLocalCreateId: String = localRepo.getAll().first().syncState.localCreateId!!

        isSynchronizingObserver
                .assertNotComplete()
                .assertNoErrors()
                .assertValues(false, true)
        testCompleteObserver.assertNotCompleteNoErrorsNoValues()
        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(
                originalCreated.copy(syncState = originalCreated.syncState.copy(
                        syncStatus = SyncStatus.SYNCHRONIZING,
                        localCreateId = originalCreatedLocalCreateId
                )),
                originalSynced
        ))
        assertEquals(
                listOf(UploadLocalModifiedRequest.Created(
                        record = originalCreated.record,
                        localCreateId = originalCreatedLocalCreateId,
                        lastLocalModifiedTimestamp = originalCreated.syncState.lastLocalModifiedTimestamp
                )),
                remoteRepo.receivedUploadLocalModifiedRequests
        )

        val createdSuccessResponse = LocalModifiedResponse.Create.Success(
                id = originalCreated.record.id,
                remoteInfo = generateFakeRemoteInfo().copy(
                        remoteCreatedTimestamp = timestampAfterStart,
                        lastRemoteSyncedTimestamp = timestampAfterStart
                )
        )

        remoteRepo.returnListLocalModifiedResponses = listOf(createdSuccessResponse)
        remoteRepo.advanceTimeToEndDelay()

        val createdAfterSync = originalCreated.copy(
                syncState = RecordSyncState.forSync(
                        remoteInfo = createdSuccessResponse.remoteInfo,
                        lastLocalModifiedTimestamp = originalCreated.syncState.lastLocalModifiedTimestamp,
                        modifyingNow = false
                )
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(
                createdAfterSync,
                originalSynced.copy(
                        syncState = originalSynced.syncState.copy(syncStatus = SyncStatus.SYNCHRONIZING)
                )
        ))

        isSynchronizingObserver
                .assertNotComplete()
                .assertNoErrors()
                .assertValues(false, true)
        testCompleteObserver.assertNotCompleteNoErrorsNoValues()
        assertEquals(timestampOfStart, localRepo.maxLastRemoteSyncedTimestampRequest)
        assertEquals(
                listOf(originalSynced.syncState.remoteInfo!!.toUpdateLocalSyncedRequest()),
                remoteRepo.receivedUpdateLocalSyncedRequest
        )

        val updateLocalNonModifiedResponse = UpdateLocalNonModifiedResponse.NoChanges(
                remoteId = originalSynced.syncState.remoteInfo!!.remoteId,
                lastRemoteSyncedTimestamp = timestampAfterStart
        )
        remoteRepo.returnListUpdateLocalNonModifiedResponses = listOf(updateLocalNonModifiedResponse)
        remoteRepo.advanceTimeToEndDelay()

        val syncedAfterSync = originalSynced.copy(
                syncState = originalSynced.syncState.copy(
                        remoteInfo = originalSynced.syncState.remoteInfo!!.copy(
                                lastRemoteSyncedTimestamp = updateLocalNonModifiedResponse.lastRemoteSyncedTimestamp
                        )
                )
        )

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(createdAfterSync, syncedAfterSync))
        isSynchronizingObserver.assertNotComplete()
                .assertNoErrors()
                .assertValues(false, true)
        testCompleteObserver.assertNotCompleteNoErrorsNoValues()
        val limit = 10
        assertEquals(
                GetNewRemoteCreatedRequest(
                        lastCreatedTimestamp = lastCreatedTimestamp,
                        excludedRemoteIds = listOf(createdSuccessResponse.remoteInfo.remoteId, originalSynced.syncState.remoteInfo!!.remoteId),
                        limit = limit
                ),
                remoteRepo.receivedGetNewRemoteCreatedRequest
        )

        val maxTimestampAfter = timestampAfterStart.plusMillis(1)
        val remoteRecord = generateFakeRemoteRecord(
                remoteInfo = generateFakeRemoteInfo().copy(remoteCreatedTimestamp = maxTimestampAfter, lastRemoteSyncedTimestamp = maxTimestampAfter)
        )

        remoteRepo.returnListRemoteRecords = listOf(remoteRecord)
        remoteRepo.advanceTimeToEndDelay()

        localRepo.getLastCreatedTimestampWithExcludedRemoteIds()
                .test()
                .assertValue(LastCreatedTimestampWithExcludedRemoteIds(maxTimestampAfter, listOf(remoteRecord.remoteInfo.remoteId)))
        val additional = GameRecordWithSyncState(
                record = GameRecord(
                        id = originalSynced.record.id.inc(),
                        gameState = remoteRecord.gameState,
                        totalPlayed = remoteRecord.totalPlayed
                ),
                syncState = RecordSyncState.forSync(
                        remoteInfo = remoteRecord.remoteInfo,
                        lastLocalModifiedTimestamp = remoteRecord.lastLocalModifiedTimestamp,
                        modifyingNow = false
                )
        )
        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(createdAfterSync, syncedAfterSync, additional))

        testCompleteObserver
                .assertComplete()
                .assertNoErrors()
                .assertNoValues()

        isSynchronizingObserver
                .assertNotComplete()
                .assertNoErrors()
                .assertValues(false, true, false)
    }

    @Test
    fun assertGetTimestampError() {
        val isSynchronizingObserver = localRepo.observeIsSynchronizing()
                .test()

        isSynchronizingObserver
                .assertNotComplete()
                .assertNoErrors()
                .assertValue(false)

        val testCompleteObserver = syncInteractor.sync()
                .test()

        isSynchronizingObserver
                .assertNotComplete()
                .assertNoErrors()
                .assertValues(false, true)

        val error = IllegalStateException("error")

        remoteRepo.returnTimestampResult = OperationResult.Error(error)
        testCompleteObserver.assertNotCompleteNoErrorsNoValues()

        remoteRepo.advanceTimeToEndDelay()

        testCompleteObserver
                .assertNotComplete()
                .assertNoValues()
                .assertError(error)

        isSynchronizingObserver
                .assertNotComplete()
                .assertNoErrors()
                .assertValues(false, true, false)
    }
}