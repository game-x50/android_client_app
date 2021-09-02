package com.ruslan.hlushan.game.auth.ui

import androidx.annotation.StringRes
import com.ruslan.hlushan.game.core.api.auth.dto.AuthError

@get:StringRes
internal val AuthError.descriptionStringResId: Int
    get() = when (this) {
        is AuthError.UserWithSuchCredentialsExists -> R.string.game_auth_ui_user_with_such_credentials_already_exists
        is AuthError.InvalidUserCredentials        -> R.string.game_auth_ui_invalid_user_credentials
    }