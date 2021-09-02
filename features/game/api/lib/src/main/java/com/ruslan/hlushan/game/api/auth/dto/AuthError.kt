package com.ruslan.hlushan.game.api.auth.dto

sealed class AuthError : Throwable() {

    class UserWithSuchCredentialsExists : AuthError()

    class InvalidUserCredentials : AuthError()
}