package com.ruslan.hlushan.game.api.auth.dto

sealed interface AuthError {

    sealed interface Register : AuthError

    sealed interface Login : AuthError

    object UserWithSuchCredentialsExists : AuthError.Register

    object InvalidUserCredentials : AuthError.Login

    object Unknown : AuthError.Register, AuthError.Login
}