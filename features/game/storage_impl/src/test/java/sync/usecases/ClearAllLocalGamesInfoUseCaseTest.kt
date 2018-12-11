package sync.usecases

import com.ruslan.hlushan.core.api.test.utils.managers.CurrentThreadSchedulersManager
import com.ruslan.hlushan.extensions.addAsFirstTo
import com.ruslan.hlushan.game.core.api.play.ClearAllLocalGamesInfoUseCase
import com.ruslan.hlushan.game.storage.impl.ClearAllLocalGamesInfoUseCaseImpl
import com.ruslan.hlushan.test.utils.generateFakeInstantTimestamp
import generateAndAddLocalCreatedToLocalRepo
import generateAndAddLocalDeletedToLocalRepo
import generateAndAddLocalSyncedToLocalRepo
import generateAndAddLocalUpdatedToLocalRepo
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.threeten.bp.Instant
import sync.stub.LocalRecordsRepoTestImpl
import sync.stub.LocalRecordsRepositoryStorageMockImpl
import sync.stub.StartSyncUseCaseStubImpl

internal class ClearAllLocalGamesInfoUseCaseTest {

    private lateinit var localRecordsRepositoryStorage: LocalRecordsRepositoryStorageMockImpl
    private lateinit var localRepo: LocalRecordsRepoTestImpl
    private lateinit var startSyncUseCase: StartSyncUseCaseStubImpl

    private lateinit var clearAllLocalGamesInfoUseCase: ClearAllLocalGamesInfoUseCase

    @Before
    fun before() {
        val scheduler = CurrentThreadSchedulersManager()

        localRecordsRepositoryStorage = LocalRecordsRepositoryStorageMockImpl()
        localRepo = LocalRecordsRepoTestImpl(localRecordsRepositoryStorage, scheduler)
        startSyncUseCase = StartSyncUseCaseStubImpl()
        clearAllLocalGamesInfoUseCase = ClearAllLocalGamesInfoUseCaseImpl(startSyncUseCase = startSyncUseCase, localRecordsRepository = localRepo)
    }

    @After
    fun after() {
        localRepo.deleteAllGames()
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

        assertEquals(Instant.ofEpochMilli(0), localRecordsRepositoryStorage.lastCreatedTimestamp)
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

        localRecordsRepositoryStorage.lastCreatedTimestamp = generateFakeInstantTimestamp()
    }
}