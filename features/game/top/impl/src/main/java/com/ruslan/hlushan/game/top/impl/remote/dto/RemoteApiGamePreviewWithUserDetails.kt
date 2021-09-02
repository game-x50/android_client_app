package com.ruslan.hlushan.game.top.impl.remote.dto

import com.ruslan.hlushan.game.api.auth.dto.User
import com.ruslan.hlushan.game.api.top.dto.GamePreviewWithUserDetails
import kotlinx.serialization.Serializable

@Serializable
internal data class RemoteApiGamePreviewWithUserDetails(
        val userNickname: String,
        val gamePreview: RemoteApiGameRecordPreview
)

internal fun RemoteApiGamePreviewWithUserDetails.toEntity(): GamePreviewWithUserDetails =
        GamePreviewWithUserDetails(
                userNickname = User.Nickname.createIfValid(this.userNickname)!!,
                gamePreview = this.gamePreview.toEntity()
        )