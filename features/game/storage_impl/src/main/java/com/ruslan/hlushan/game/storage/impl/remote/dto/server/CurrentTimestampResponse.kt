package com.ruslan.hlushan.game.storage.impl.remote.dto.server

import com.ruslan.hlushan.parsing.impl.utils.parsing.InstantAsEpochMillisSerializer
import kotlinx.serialization.Serializable
import org.threeten.bp.Instant

@Serializable
internal class CurrentTimestampResponse(
        @Serializable(with = InstantAsEpochMillisSerializer::class)
        val nowTimestamp: Instant
)