@file:Suppress("PackageNaming")

package com.ruslan.hlushan.game.play.ui.screens.game.continue_game

import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.extensions.ifNotNull
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.thread.ThreadChecker
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.game.api.play.PlayRecordsInteractor
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameState
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.play.ui.screens.GameScopeMarkerRepository
import com.ruslan.hlushan.game.play.ui.screens.game.PlayGameViewModel
import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager
import com.ruslan.hlushan.third_party.three_ten.extensions.orZero
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.threeten.bp.Duration

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
                    localModifiedTimestamp = RecordSyncState.LastLocalModifiedTimestamp.now()
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