package sync.rule

import org.junit.rules.TestWatcher
import org.junit.runner.Description
import sync.stub.LocalRecordsRepoTestImpl

internal class TestLocalRecordsRepoCleanUpRule(
        private val localRepo: () -> LocalRecordsRepoTestImpl
) : TestWatcher() {

    override fun finished(description: Description?) {
        super.finished(description)
        localRepo().deleteAllGames()
    }
}