package sync.stub

import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepositoryStorage
import io.reactivex.Completable

class LocalRecordsRepositoryStorageMockImpl : LocalRecordsRepositoryStorage {

    override var lastCreatedTimestamp: RemoteInfo.CreatedTimestamp =
            RemoteInfo.CreatedTimestamp.min()

    override fun storeLastCreatedTimestamp(newLastCreatedTimestamp: RemoteInfo.CreatedTimestamp): Completable =
            Completable.fromAction { lastCreatedTimestamp = newLastCreatedTimestamp }
}