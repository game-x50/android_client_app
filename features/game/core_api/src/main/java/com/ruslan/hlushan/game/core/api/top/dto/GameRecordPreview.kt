package com.ruslan.hlushan.game.core.api.top.dto

import com.ruslan.hlushan.game.core.api.play.dto.GameSize
import org.threeten.bp.Duration
import org.threeten.bp.Instant

data class GameRecordPreview(
        val id: String,
        val totalPlayed: Duration,
        val totalSum: Int,
        val size: GameSize,
        val lastLocalModifiedTimestamp: Instant
)