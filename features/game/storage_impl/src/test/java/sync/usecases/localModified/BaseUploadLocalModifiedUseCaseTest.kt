package sync.usecases.localModified

import com.ruslan.hlushan.core.api.test.utils.log.EmptyAppLoggerImpl
import com.ruslan.hlushan.core.api.test.utils.managers.CurrentThreadSchedulersManager
import com.ruslan.hlushan.game.storage.impl.PlayRecordsInteractorImpl
import com.ruslan.hlushan.game.storage.impl.UploadLocalModifiedUseCase
import org.junit.After
import org.junit.Before
import sync.stub.LocalRecordsRepoTestImpl
import sync.stub.LocalRecordsRepositoryStorageMockImpl
import sync.stub.SyncRemoteRepositoryMockImpl

/**
 * @author Ruslan Hlushan on 2019-06-05
 */
internal abstract class BaseUploadLocalModifiedUseCaseTest {

    protected lateinit var localRepo: LocalRecordsRepoTestImpl
    protected lateinit var remoteRepo: SyncRemoteRepositoryMockImpl

    protected lateinit var uploadLocalModifiedUseCase: UploadLocalModifiedUseCase

    protected lateinit var playRecordsInteractor: PlayRecordsInteractorImpl

    @Before
    fun before() {
        val scheduler = CurrentThreadSchedulersManager()
        val logger = EmptyAppLoggerImpl()

        localRepo = LocalRecordsRepoTestImpl(LocalRecordsRepositoryStorageMockImpl(), scheduler)
        remoteRepo = SyncRemoteRepositoryMockImpl()
        uploadLocalModifiedUseCase = UploadLocalModifiedUseCase(
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