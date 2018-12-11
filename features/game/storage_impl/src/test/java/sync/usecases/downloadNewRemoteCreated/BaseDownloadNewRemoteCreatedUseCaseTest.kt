package sync.usecases.downloadNewRemoteCreated

import com.ruslan.hlushan.core.api.test.utils.log.EmptyAppLoggerImpl
import com.ruslan.hlushan.core.api.test.utils.managers.CurrentThreadSchedulersManager
import com.ruslan.hlushan.game.storage.impl.DownloadNewRemoteCreatedUseCase
import org.junit.After
import org.junit.Before
import sync.stub.LocalRecordsRepoTestImpl
import sync.stub.LocalRecordsRepositoryStorageMockImpl
import sync.stub.SyncRemoteRepositoryMockImpl

internal abstract class BaseDownloadNewRemoteCreatedUseCaseTest {

    protected lateinit var storage: LocalRecordsRepositoryStorageMockImpl
    protected lateinit var localRepo: LocalRecordsRepoTestImpl
    protected lateinit var remoteRepo: SyncRemoteRepositoryMockImpl

    protected lateinit var downloadNewRemoteCreatedUseCase: DownloadNewRemoteCreatedUseCase

    @Before
    fun before() {
        val scheduler = CurrentThreadSchedulersManager()
        val logger = EmptyAppLoggerImpl()

        storage = LocalRecordsRepositoryStorageMockImpl()
        localRepo = LocalRecordsRepoTestImpl(storage, scheduler)
        remoteRepo = SyncRemoteRepositoryMockImpl()
        downloadNewRemoteCreatedUseCase = DownloadNewRemoteCreatedUseCase(
                remoteSyncRepository = remoteRepo,
                localRepository = localRepo,
                appLogger = logger
        )
    }

    @After
    fun after() {
        remoteRepo.cleanUp()
        localRepo.deleteAllGames()
    }
}