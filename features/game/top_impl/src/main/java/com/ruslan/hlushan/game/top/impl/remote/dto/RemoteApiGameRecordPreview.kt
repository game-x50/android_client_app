package com.ruslan.hlushan.game.top.impl.remote.dto

import com.ruslan.hlushan.game.core.api.play.dto.GameSize
import com.ruslan.hlushan.game.core.api.top.dto.GameRecordPreview
import com.ruslan.hlushan.parsing.impl.utils.parsing.InstantAsEpochMillisSerializer
import kotlinx.serialization.Serializable
import org.threeten.bp.Duration
import org.threeten.bp.Instant

@Serializable
internal data class RemoteApiGameRecordPreview(
        val id: String,
        val totalPlayedSeconds: Long,
        val totalSum: Int,
        val countRowsAndColumns: Int,
        @Serializable(with = InstantAsEpochMillisSerializer::class)
        val lastLocalModifiedTimestamp: Instant
)

internal fun RemoteApiGameRecordPreview.toEntity(): GameRecordPreview =
        GameRecordPreview(
                id = this.id,
                totalPlayed = Duration.ofSeconds(this.totalPlayedSeconds),
                totalSum = this.totalSum,
                size = GameSize.fromCountRowsAndColumns(this.countRowsAndColumns),
                lastLocalModifiedTimestamp = this.lastLocalModifiedTimestamp
        )