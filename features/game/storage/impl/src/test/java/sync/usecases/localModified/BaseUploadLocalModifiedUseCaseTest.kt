package sync.usecases.localModified

import com.ruslan.hlushan.game.storage.impl.PlayRecordsInteractorImpl
import com.ruslan.hlushan.game.storage.impl.UploadLocalModifiedUseCase
import com.ruslan.hlushan.third_party.rxjava2.test.utils.CurrentThreadSchedulersManager
import org.junit.Before
import org.junit.Rule
import sync.rule.TestLocalRecordsRepoCleanUpRule
import sync.rule.TestSyncRemoteRepositoryMockCleanUpRule
import sync.stub.LocalRecordsRepoTestImpl
import sync.stub.LocalRecordsRepositoryStorageMockImpl
import sync.stub.SyncRemoteRepositoryMockImpl

internal abstract class BaseUploadLocalModifiedUseCaseTest {

    protected lateinit var localRepo: LocalRecordsRepoTestImpl
    protected lateinit var remoteRepo: SyncRemoteRepositoryMockImpl

    protected lateinit var uploadLocalModifiedUseCase: UploadLocalModifiedUseCase

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
        val logger = com.ruslan.hlushan.core.logger.api.test.utils.EmptyAppLoggerImpl

        localRepo = LocalRecordsRepoTestImpl(LocalRecordsRepositoryStorageMockImpl(), scheduler)
        remoteRepo = SyncRemoteRepositoryMockImpl()
        uploadLocalModifiedUseCase = UploadLocalModifiedUseCase(
                remoteSyncRepository = remoteRepo,
                localRepository = localRepo,
                appLogger = logger
        )
        playRecordsInteractor = PlayRecordsInteractorImpl(localRepo, logger)
    }
}