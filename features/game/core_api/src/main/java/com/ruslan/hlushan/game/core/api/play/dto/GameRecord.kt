package com.ruslan.hlushan.game.core.api.play.dto

import org.threeten.bp.Duration

data class GameRecord(
        val id: Long,
        val gameState: GameState,
        val totalPlayed: Duration
)