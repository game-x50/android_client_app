package sync

import com.ruslan.hlushan.core.api.test.utils.managers.CurrentThreadSchedulersManager
import com.ruslan.hlushan.core.logger.api.test.utils.EmptyAppLoggerImpl
import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.api.play.dto.GameState
import com.ruslan.hlushan.game.api.play.dto.ImmutableNumbersMatrix
import com.ruslan.hlushan.game.api.play.dto.MatrixAndNewItemsState
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.storage.impl.UploadLocalModifiedUseCase
import com.ruslan.hlushan.game.storage.impl.local.LocalUpdateRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.LocalModifiedResponse
import com.ruslan.hlushan.test.utils.generateFakeDuration
import com.ruslan.hlushan.test.utils.generateFakeInstantTimestamp
import com.ruslan.hlushan.test.utils.generateFakeStringId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import sync.rule.TestLocalRecordsRepoCleanUpRule
import sync.rule.TestSyncRemoteRepositoryMockCleanUpRule
import sync.stub.LocalRecordsRepoTestImpl
import sync.stub.LocalRecordsRepositoryStorageMockImpl
import sync.stub.SyncRemoteRepositoryMockImpl

/**
 * @author Ruslan Hlushan on 2019-05-31
 */
internal class CommonTests {

    private lateinit var localRepo: LocalRecordsRepoTestImpl
    private lateinit var remoteRepo: SyncRemoteRepositoryMockImpl

    private lateinit var uploadLocalModifiedUseCase: UploadLocalModifiedUseCase

    @Rule
    @JvmField
    val testLocalRecordsRepoCleanUpRule = TestLocalRecordsRepoCleanUpRule { localRepo }

    @Rule
    @JvmField
    val testSyncRemoteRepositoryMockCleanUpRule = TestSyncRemoteRepositoryMockCleanUpRule { remoteRepo }

    @Before
    fun before() {
        val scheduler = CurrentThreadSchedulersManager()
        val logger = EmptyAppLoggerImpl

        localRepo = LocalRecordsRepoTestImpl(LocalRecordsRepositoryStorageMockImpl(), scheduler)
        remoteRepo = SyncRemoteRepositoryMockImpl()
        uploadLocalModifiedUseCase = UploadLocalModifiedUseCase(
                remoteSyncRepository = remoteRepo,
                localRepository = localRepo,
                appLogger = logger
        )
    }

    @SuppressWarnings("LongMethod")
    @Test
    fun testLocalCreateId() {
        val localActionId = generateFakeStringId()
        val localCreatedTimestamp = generateFakeInstantTimestamp()

        val syncState = RecordSyncState.forLocalCreated(
                localActionId = localActionId,
                modifyingNow = false,
                localCreatedTimestamp = localCreatedTimestamp
        )

        val gameSize = GameSize.MEDIUM

        val currentNumbers = (1..(gameSize.countRowsAndColumns * gameSize.countRowsAndColumns))
                .map { number ->
                    if (number % 3 == 0) {
                        null
                    } else {
                        number
                    }
                }
        val currentMatrix = ImmutableNumbersMatrix(
                numbers = currentNumbers,
                gameSize = gameSize,
                totalSum = currentNumbers.filterNotNull().sum()
        )
        val currentNewItems = listOf(10, 120, 1239, 1234)

        val lastStackNumbers = currentNumbers.map { num -> num?.inc() }
        val lastStackMatrix = ImmutableNumbersMatrix(
                numbers = lastStackNumbers,
                gameSize = gameSize,
                totalSum = lastStackNumbers.filterNotNull().sum()
        )
        val lastStackNewItems = listOf(101, 10, 1139, 114)

        val stack = listOf(
                MatrixAndNewItemsState(lastStackMatrix, lastStackNewItems),
                MatrixAndNewItemsState(ImmutableNumbersMatrix.emptyForSize(gameSize), emptyList())
        )

        val gameState = GameState(current = MatrixAndNewItemsState(currentMatrix, currentNewItems), stack = stack)

        val request = LocalUpdateRequest(
                gameState = gameState,
                totalPlayed = generateFakeDuration(),
                syncState = syncState
        )

        localRepo.addNewRecord(request)
                .subscribe()

        val id = localRepo.getAll().first().record.id

        assertEquals(
                listOf(null),
                localRepo.getAll().map { recordWithSyncState -> recordWithSyncState.syncState.localCreateId }
        )

        val disposable = uploadLocalModifiedUseCase.uploadAll(10)
                .subscribe()

        val localCreateId: String = localRepo.getAll().first().syncState.localCreateId!!

        val splited = localCreateId.split("_")

        assertEquals(stack.size.toString(), splited[splited.size - 1])
        assertEquals(currentNewItems.sum().toString(), splited[splited.size - 2])
        assertEquals(currentNumbers.count { number -> number != null }.toString(), splited[splited.size - 3])
        assertEquals(gameSize.countRowsAndColumns.toString(), splited[splited.size - 4])
        assertEquals(request.totalPlayed.seconds.toString(), splited[splited.size - 5])
        assertEquals(currentMatrix.totalSum.toString(), splited[splited.size - 6])
        assertNotNull(splited[splited.size - 7])

        remoteRepo.returnListLocalModifiedResponses = listOf(LocalModifiedResponse.Fail(id = id))
        remoteRepo.advanceTimeToEndDelay()

        assertTrue(disposable.isDisposed)

        assertEquals(
                listOf(SyncStatus.WAITING to localCreateId),
                localRepo.getAll()
                        .map { recordWithSyncState ->
                            recordWithSyncState.syncState.syncStatus to recordWithSyncState.syncState.localCreateId
                        }
        )
    }
}