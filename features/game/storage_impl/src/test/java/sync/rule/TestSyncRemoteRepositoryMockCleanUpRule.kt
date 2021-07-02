package sync.rule

import org.junit.rules.TestWatcher
import org.junit.runner.Description
import sync.stub.SyncRemoteRepositoryMockImpl

internal class TestSyncRemoteRepositoryMockCleanUpRule(
        private val remoteRepo: () -> SyncRemoteRepositoryMockImpl
) : TestWatcher() {

    override fun finished(description: Description?) {
        super.finished(description)
        remoteRepo().cleanUp()
    }
}