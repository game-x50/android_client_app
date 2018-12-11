package com.ruslan.hlushan.game.core.api.auth

import com.ruslan.hlushan.core.api.dto.ValueHolder
import com.ruslan.hlushan.core.api.dto.VoidOperationResult
import com.ruslan.hlushan.game.core.api.auth.dto.AuthError
import com.ruslan.hlushan.game.core.api.auth.dto.User
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface AuthInteractor {

    fun createNewUser(
            nickname: String,
            email: String,
            password: String
    ): Single<VoidOperationResult<AuthError.UserWithSuchCredentialsExists>>

    fun logIn(
            email: String,
            password: String
    ): Single<VoidOperationResult<AuthError.InvalidUserCredentials>>

    fun sendPasswordResetEmail(email: String): Completable

    fun logOut(): Completable

    fun getUser(): User?

    fun observeCurrentUser(): Observable<ValueHolder<User?>>

    fun updateUserWith(
            newNickname: String,
            newPassword: String,
            oldPassword: String
    ): Single<VoidOperationResult<AuthError>>
}

fun AuthInteractor.observeUserIsAuthenticated(): Observable<Boolean> =
        this.observeCurrentUser()
                .map { holder -> (holder.value != null) }