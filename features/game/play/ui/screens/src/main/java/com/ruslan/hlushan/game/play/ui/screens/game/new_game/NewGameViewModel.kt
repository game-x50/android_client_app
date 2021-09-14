@file:Suppress("PackageNaming")

package com.ruslan.hlushan.game.play.ui.screens.game.new_game

import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.thread.ThreadChecker
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.game.api.play.PlayRecordsInteractor
import com.ruslan.hlushan.game.api.play.dto.GameState
import com.ruslan.hlushan.game.api.play.dto.wasPlayed
import com.ruslan.hlushan.game.play.ui.screens.GameScopeMarkerRepository
import com.ruslan.hlushan.game.play.ui.screens.game.PlayGameViewModel
import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.threeten.bp.Duration
import org.threeten.bp.Instant

internal class NewGameViewModel
@AssistedInject
constructor(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        @Assisted router: Router,
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

    @UiMainThread
    override fun shouldBeSaveResultQuestionDialogShown(result: GameState): Boolean =
            result.wasPlayed

    @UiMainThread
    override fun saveRecord(result: GameState) {
        playRecordsInteractor.addNewRecordAfterPlaying(
                gameState = result,
                totalPlayed = playedDuration,
                localCreatedTimestamp = Instant.now()
        )
                .handleSavingRecord(result)
    }

    @UiMainThread
    override fun markAsNonPlayingAndExit() = router.exit()

    @UiMainThread
    override fun getTotalPlayedDuration(): Duration = playedDuration

    @AssistedFactory
    interface Factory {
        fun create(router: Router): NewGameViewModel
    }
}