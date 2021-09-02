@file:Suppress("PackageNaming")

package com.ruslan.hlushan.game.play.ui.game.continue_game

import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.extensions.ifNotNull
import com.ruslan.hlushan.game.core.api.play.PlayRecordsInteractor
import com.ruslan.hlushan.game.core.api.play.dto.GameRecord
import com.ruslan.hlushan.game.core.api.play.dto.GameState
import com.ruslan.hlushan.game.play.ui.GameScopeMarkerRepository
import com.ruslan.hlushan.game.play.ui.game.PlayGameViewModel
import com.ruslan.hlushan.threeten.extensions.orZero
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.threeten.bp.Duration
import org.threeten.bp.Instant

internal class ContinueGameViewModel
@SuppressWarnings("LongParameterList")
@AssistedInject
constructor(
        @Assisted router: Router,
        @Assisted val continuedGameRecord: GameRecord?,
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        schedulersManager: SchedulersManager,
        gameScopeMarkerRepository: GameScopeMarkerRepository,
        playRecordsInteractor: PlayRecordsInteractor
) : PlayGameViewModel(
        appLogger = appLogger,
        threadChecker = threadChecker,
        schedulersManager = schedulersManager,
        gameScopeMarkerRepository = gameScopeMarkerRepository,
        router = router,
        playRecordsInteractor = playRecordsInteractor
) {

    override fun shouldBeSaveResultQuestionDialogShown(result: GameState): Boolean =
            ((result != continuedGameRecord?.gameState)
             || (getTotalPlayedDuration() != continuedGameRecord.totalPlayed))

    override fun saveRecord(result: GameState) {
        ifNotNull(continuedGameRecord) { oldRecord ->
            playRecordsInteractor.updateRecordAfterPlaying(
                    id = oldRecord.id,
                    gameState = result,
                    totalPlayed = (oldRecord.totalPlayed + playedDuration),
                    localModifiedTimestamp = Instant.now()
            )
                    .handleSavingRecord(result)
        }
    }

    override fun markAsNonPlayingAndExit() {
        ifNotNull(continuedGameRecord) { oldRecord ->
            playRecordsInteractor.markAsNonPlaying(oldRecord.id)
                    .handleSavingRecord(null)
        }
    }

    @UiMainThread
    override fun getTotalPlayedDuration(): Duration =
            (playedDuration + continuedGameRecord?.totalPlayed.orZero())

    @AssistedFactory
    interface Factory {
        fun create(router: Router, continuedGameRecord: GameRecord?): ContinueGameViewModel
    }
}