package sync.play.base

import com.ruslan.hlushan.core.api.test.utils.managers.CurrentThreadSchedulersManager
import com.ruslan.hlushan.game.storage.impl.PlayRecordsInteractorImpl
import org.junit.Before
import org.junit.Rule
import sync.rule.TestLocalRecordsRepoCleanUpRule
import sync.stub.LocalRecordsRepoTestImpl
import sync.stub.LocalRecordsRepositoryStorageMockImpl

/**
 * @author Ruslan Hlushan on 2019-06-10
 */
internal abstract class BasePlayRecordsInteractorImplTest {

    protected lateinit var localRepo: LocalRecordsRepoTestImpl
    protected lateinit var playRecordsInteractor: PlayRecordsInteractorImpl

    @Rule
    @JvmField
    val testLocalRecordsRepoCleanUpRule = TestLocalRecordsRepoCleanUpRule { localRepo }

    @Before
    fun before() {
        val scheduler = CurrentThreadSchedulersManager()
        val logger = com.ruslan.hlushan.core.logger.api.test.utils.EmptyAppLoggerImpl

        localRepo = LocalRecordsRepoTestImpl(LocalRecordsRepositoryStorageMockImpl(), scheduler)
        playRecordsInteractor = PlayRecordsInteractorImpl(localRepo, logger)
    }
}