package com.ruslan.hlushan.game.storage.impl

import com.ruslan.hlushan.game.api.play.ClearAllLocalGamesInfoUseCase
import com.ruslan.hlushan.game.api.sync.StartSyncUseCase
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepository
import com.ruslan.hlushan.game.storage.impl.local.markSynchronizingFinished
import com.ruslan.hlushan.game.storage.impl.local.markSynchronizingStarted
import io.reactivex.Completable
import org.threeten.bp.Instant
import javax.inject.Inject

internal class ClearAllLocalGamesInfoUseCaseImpl
@Inject
constructor(
        private val startSyncUseCase: StartSyncUseCase,
        private val localRecordsRepository: LocalRecordsRepository
) : ClearAllLocalGamesInfoUseCase {

    override fun clearAllLocalGamesInfo(): Completable =
            Completable.fromAction {
                startSyncUseCase.cancel()

                localRecordsRepository.markSynchronizingStarted()
            }
                    .andThen(localRecordsRepository.deleteAllGames())
                    .andThen(localRecordsRepository.storeLastCreatedTimestamp(Instant.ofEpochMilli(0)))
                    .doFinally { localRecordsRepository.markSynchronizingFinished() }
}