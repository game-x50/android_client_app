package com.ruslan.hlushan.game.play.ui.screens.dto

import android.os.Parcelable
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.play.ui.view.dto.GameStateParcelable
import com.ruslan.hlushan.game.play.ui.view.dto.toParcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.Duration

@Parcelize
internal class GameRecordParcelable(
        val id: Long,
        val gameState: GameStateParcelable,
        val totalPlayed: Duration
) : Parcelable {

    fun toOriginal(): GameRecord =
            GameRecord(
                    id = this.id,
                    gameState = this.gameState.toOriginal(),
                    totalPlayed = this.totalPlayed
            )
}

internal fun GameRecord.toParcelable(): GameRecordParcelable =
        GameRecordParcelable(
                id = this.id,
                gameState = this.gameState.toParcelable(),
                totalPlayed = this.totalPlayed
        )