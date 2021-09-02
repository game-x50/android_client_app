package com.ruslan.hlushan.game.api.top.dto

import com.ruslan.hlushan.game.api.auth.dto.User

data class GamePreviewWithUserDetails(
        val userNickname: User.Nickname,
        val gamePreview: GameRecordPreview
)