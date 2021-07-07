package sync.usecases.localSynced

import com.ruslan.hlushan.core.api.test.utils.log.EmptyAppLoggerImpl
import com.ruslan.hlushan.core.api.test.utils.managers.CurrentThreadSchedulersManager
import com.ruslan.hlushan.game.storage.impl.PlayRecordsInteractorImpl
import com.ruslan.hlushan.game.storage.impl.UpdateLocalSyncedUseCase
import org.junit.Before
import org.junit.Rule
import sync.rule.TestLocalRecordsRepoCleanUpRule
import sync.rule.TestSyncRemoteRepositoryMockCleanUpRule
import sync.stub.LocalRecordsRepoTestImpl
import sync.stub.LocalRecordsRepositoryStorageMockImpl
import sync.stub.SyncRemoteRepositoryMockImpl

internal abstract class BaseUpdateLocalSyncedUseCaseTest {

    protected lateinit var localRepo: LocalRecordsRepoTestImpl
    protected lateinit var remoteRepo: SyncRemoteRepositoryMockImpl

    protected lateinit var updateLocalSyncedUseCase: UpdateLocalSyncedUseCase

    protected lateinit var playRecordsInteractor: PlayRecordsInteractorImpl

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

        localRepo = LocalRecordsRepoTestImpl(LocalRecordsRepositoryStorageMockImpl(), scheduler)
        remoteRepo = SyncRemoteRepositoryMockImpl()
        updateLocalSyncedUseCase = UpdateLocalSyncedUseCase(
                remoteSyncRepository = remoteRepo,
                localRepository = localRepo,
                appLogger = logger
        )
        playRecordsInteractor = PlayRecordsInteractorImpl(localRepo, logger)
    }
}