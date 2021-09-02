package sync.stub

import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepositoryStorage
import io.reactivex.Completable
import org.threeten.bp.Instant

class LocalRecordsRepositoryStorageMockImpl : LocalRecordsRepositoryStorage {

    override var lastCreatedTimestamp: Instant = Instant.ofEpochMilli(0)

    override fun storeLastCreatedTimestamp(newLastCreatedTimestamp: Instant): Completable =
            Completable.fromAction { lastCreatedTimestamp = newLastCreatedTimestamp }
}