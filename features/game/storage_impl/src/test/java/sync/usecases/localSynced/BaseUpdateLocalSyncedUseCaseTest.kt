package sync.usecases.localSynced

import com.ruslan.hlushan.core.api.test.utils.log.EmptyAppLoggerImpl
import com.ruslan.hlushan.core.api.test.utils.managers.CurrentThreadSchedulersManager
import com.ruslan.hlushan.game.storage.impl.PlayRecordsInteractorImpl
import com.ruslan.hlushan.game.storage.impl.UpdateLocalSyncedUseCase
import org.junit.After
import org.junit.Before
import sync.stub.LocalRecordsRepoTestImpl
import sync.stub.LocalRecordsRepositoryStorageMockImpl
import sync.stub.SyncRemoteRepositoryMockImpl

internal abstract class BaseUpdateLocalSyncedUseCaseTest {

    protected lateinit var localRepo: LocalRecordsRepoTestImpl
    protected lateinit var remoteRepo: SyncRemoteRepositoryMockImpl

    protected lateinit var updateLocalSyncedUseCase: UpdateLocalSyncedUseCase

    protected lateinit var playRecordsInteractor: PlayRecordsInteractorImpl

    @Before
    fun before() {
        val scheduler = CurrentThreadSchedulersManager()
        val logger = EmptyAppLoggerImpl()

        localRepo = LocalRecordsRepoTestImpl(LocalRecordsRepositoryStorageMockImpl(), scheduler)
        remoteRepo = SyncRemoteRepositoryMockImpl()
        updateLocalSyncedUseCase = UpdateLocalSyncedUseCase(
                remoteSyncRepository = remoteRepo,
                localRepository = localRepo,
                appLogger = logger
        )
        playRecordsInteractor = PlayRecordsInteractorImpl(localRepo, logger)
    }

    @After
    fun after() {
        localRepo.deleteAllGames()
        remoteRepo.cleanUp()
    }
}