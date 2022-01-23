package sync.stub

import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepositoryImpl
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepositoryStorage
import com.ruslan.hlushan.game.storage.impl.local.db.entities.fromDbRecord
import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager
import io.reactivex.Completable
import io.reactivex.Single

internal class LocalRecordsRepoTestImpl(
        localRecordsRepositoryStorage: LocalRecordsRepositoryStorage,
        schedulersManager: SchedulersManager,
        private val gameRecordsDAOStubImpl: GameRecordsDAOStubImpl = GameRecordsDAOStubImpl()
) : LocalRecordsRepositoryImpl(
        localRecordsRepositoryStorage,
        gameRecordsDAOStubImpl,
        schedulersManager,
        com.ruslan.hlushan.core.logger.api.test.utils.EmptyAppLoggerImpl
) {

    var maxLastRemoteSyncedTimestampRequest: RemoteInfo.LastSyncedTimestamp? = null
        private set

    var deleteAllGamesError: Throwable? = null

    fun getAll(): List<GameRecordWithSyncState> =
            gameRecordsDAOStubImpl.getAll()
                    .map { dbRec -> dbRec.fromDbRecord() }

    override fun markAsSyncingAndGetSyncedWhereLastRemoteSyncSmallerThen(
            maxLastRemoteSyncedTimestamp: RemoteInfo.LastSyncedTimestamp,
            limit: Int
    ): Single<List<GameRecordWithSyncState>> {
        maxLastRemoteSyncedTimestampRequest = maxLastRemoteSyncedTimestamp
        return super.markAsSyncingAndGetSyncedWhereLastRemoteSyncSmallerThen(maxLastRemoteSyncedTimestamp, limit)
    }

    override fun deleteAllGames(): Completable =
            (deleteAllGamesError?.let { error -> Completable.error(error) }
             ?: super.deleteAllGames())
}