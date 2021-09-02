package com.ruslan.hlushan.game.core.api.top.dto

import com.ruslan.hlushan.game.core.api.auth.dto.User

data class GamePreviewWithUserDetails(
        val userNickname: User.Nickname,
        val gamePreview: GameRecordPreview
)