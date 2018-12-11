package com.ruslan.hlushan.game.core.api.auth.dto

sealed class AuthError {

    class UserWithSuchCredentialsExists : AuthError()

    class InvalidUserCredentials : AuthError()
}