package sync.usecases.downloadNewRemoteCreated

import com.ruslan.hlushan.core.api.test.utils.log.EmptyAppLoggerImpl
import com.ruslan.hlushan.core.api.test.utils.managers.CurrentThreadSchedulersManager
import com.ruslan.hlushan.game.storage.impl.DownloadNewRemoteCreatedUseCase
import org.junit.Before
import org.junit.Rule
import sync.rule.TestLocalRecordsRepoCleanUpRule
import sync.rule.TestSyncRemoteRepositoryMockCleanUpRule
import sync.stub.LocalRecordsRepoTestImpl
import sync.stub.LocalRecordsRepositoryStorageMockImpl
import sync.stub.SyncRemoteRepositoryMockImpl

internal abstract class BaseDownloadNewRemoteCreatedUseCaseTest {

    protected lateinit var storage: LocalRecordsRepositoryStorageMockImpl
    protected lateinit var localRepo: LocalRecordsRepoTestImpl
    protected lateinit var remoteRepo: SyncRemoteRepositoryMockImpl

    protected lateinit var downloadNewRemoteCreatedUseCase: DownloadNewRemoteCreatedUseCase

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

        storage = LocalRecordsRepositoryStorageMockImpl()
        localRepo = LocalRecordsRepoTestImpl(storage, scheduler)
        remoteRepo = SyncRemoteRepositoryMockImpl()
        downloadNewRemoteCreatedUseCase = DownloadNewRemoteCreatedUseCase(
                remoteSyncRepository = remoteRepo,
                localRepository = localRepo,
                appLogger = logger
        )
    }
}