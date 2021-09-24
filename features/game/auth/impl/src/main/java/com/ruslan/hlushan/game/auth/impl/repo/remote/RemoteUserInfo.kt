package com.ruslan.hlushan.game.auth.impl.repo.remote

import com.ruslan.hlushan.game.api.auth.dto.User

internal data class RemoteUserInfo(
        val userId: User.Id,
        val userEmail: User.Email
)