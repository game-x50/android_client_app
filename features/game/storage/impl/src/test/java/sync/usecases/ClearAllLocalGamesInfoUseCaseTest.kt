package sync.usecases

import com.ruslan.hlushan.core.extensions.addAsFirstTo
import com.ruslan.hlushan.game.api.play.ClearAllLocalGamesInfoUseCase
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.api.test.utils.generateFakeRemoteInfoCreatedTimestamp
import com.ruslan.hlushan.game.storage.impl.ClearAllLocalGamesInfoUseCaseImpl
import com.ruslan.hlushan.third_party.rxjava2.test.utils.CurrentThreadSchedulersManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import sync.rule.TestLocalRecordsRepoCleanUpRule
import sync.stub.LocalRecordsRepoTestImpl
import sync.stub.LocalRecordsRepositoryStorageMockImpl
import sync.stub.StartSyncUseCaseStubImpl
import utils.generateAndAddLocalCreatedToLocalRepo
import utils.generateAndAddLocalDeletedToLocalRepo
import utils.generateAndAddLocalSyncedToLocalRepo
import utils.generateAndAddLocalUpdatedToLocalRepo

internal class ClearAllLocalGamesInfoUseCaseTest {

    private lateinit var localRecordsRepositoryStorage: LocalRecordsRepositoryStorageMockImpl
    private lateinit var localRepo: LocalRecordsRepoTestImpl
    private lateinit var startSyncUseCase: StartSyncUseCaseStubImpl

    private lateinit var clearAllLocalGamesInfoUseCase: ClearAllLocalGamesInfoUseCase

    @Rule
    @JvmField
    val testLocalRecordsRepoCleanUpRule = TestLocalRecordsRepoCleanUpRule { localRepo }

    @Before
    fun before() {
        val scheduler = CurrentThreadSchedulersManager()

        localRecordsRepositoryStorage = LocalRecordsRepositoryStorageMockImpl()
        localRepo = LocalRecordsRepoTestImpl(localRecordsRepositoryStorage, scheduler)
        startSyncUseCase = StartSyncUseCaseStubImpl()
        clearAllLocalGamesInfoUseCase = ClearAllLocalGamesInfoUseCaseImpl(
                startSyncUseCase = startSyncUseCase,
                localRecordsRepository = localRepo
        )
    }

    @Test
    fun testFinalState() {
        val isSynchronizingObserver = localRepo.observeIsSynchronizing()
                .test()

        generateAndDataToLocalRepo()

        isSynchronizingObserver
                .assertNoErrors()
                .assertNotComplete()
                .assertValue(false)

        assertTrue(localRepo.getAll().isNotEmpty())

        assertClearAndEmptyState()
    }

    @Test
    fun checkMultipleCall() {
        val isSynchronizingObserver = localRepo.observeIsSynchronizing()
                .test()

        for (i in 1..10) {

            val notifiedValues = (false).addAsFirstTo((1..i).flatMap { listOf(true, false) })

            assertClearAndEmptyState(expectedInitCanceledCounter = (i - 1))

            isSynchronizingObserver
                    .assertNoErrors()
                    .assertNotComplete()
                    .assertValues(*notifiedValues.toTypedArray())
        }
    }

    @Test
    fun testAnyError() {
        val isSynchronizingObserver = localRepo.observeIsSynchronizing()
                .test()

        generateAndDataToLocalRepo()

        isSynchronizingObserver
                .assertNoErrors()
                .assertNotComplete()
                .assertValue(false)

        assertEquals(0, startSyncUseCase.canceledCounter)

        val error = Throwable("Delete")

        localRepo.deleteAllGamesError = error

        clearAllLocalGamesInfoUseCase.clearAllLocalGamesInfo()
                .test()
                .assertError(error)
                .assertNotComplete()

        assertEquals(1, startSyncUseCase.canceledCounter)

        isSynchronizingObserver
                .assertNoErrors()
                .assertNotComplete()
                .assertValues(false, true, false)
    }

    private fun assertClearAndEmptyState(expectedInitCanceledCounter: Int = 0) {
        val isSynchronizingObserver = localRepo.observeIsSynchronizing()
                .test()

        isSynchronizingObserver
                .assertNoErrors()
                .assertNotComplete()
                .assertValue(false)

        assertEquals(expectedInitCanceledCounter, startSyncUseCase.canceledCounter)

        clearAllLocalGamesInfoUseCase.clearAllLocalGamesInfo()
                .test()
                .assertNoErrors()
                .assertComplete()

        isSynchronizingObserver
                .assertNoErrors()
                .assertNotComplete()
                .assertValues(false, true, false)

        assertTrue(localRepo.getAll().isEmpty())

        val expectedAfterCanceledCounter = (expectedInitCanceledCounter + 1)
        assertEquals(expectedAfterCanceledCounter, startSyncUseCase.canceledCounter)

        assertEquals(
                RemoteInfo.CreatedTimestamp.min(),
                localRecordsRepositoryStorage.lastCreatedTimestamp
        )
    }

    private fun generateAndDataToLocalRepo() {
        booleanArrayOf(true, false).forEach { syncingNow ->
            booleanArrayOf(true, false).forEach { modifyingNow ->
                localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = syncingNow, modifyingNow = modifyingNow)
                localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = syncingNow, modifyingNow = modifyingNow)
                localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = syncingNow)
                localRepo.generateAndAddLocalSyncedToLocalRepo(syncingNow = syncingNow, modifyingNow = modifyingNow)
            }
        }

        localRecordsRepositoryStorage.lastCreatedTimestamp = generateFakeRemoteInfoCreatedTimestamp()
    }
}