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
            nickname: User.Nickname,
            email: User.Email,
            password: User.Password
    ): Single<VoidOperationResult<AuthError.UserWithSuchCredentialsExists>>

    fun logIn(
            email: User.Email,
            password: User.Password
    ): Single<VoidOperationResult<AuthError.InvalidUserCredentials>>

    fun sendPasswordResetEmail(email: User.Email): Completable

    fun logOut(): Completable

    fun getUser(): User?

    fun observeCurrentUser(): Observable<ValueHolder<User?>>

    fun updateUserWith(
            newNickname: User.Nickname,
            newPassword: User.Password,
            oldPassword: User.Password
    ): Single<VoidOperationResult<AuthError>>
}

fun AuthInteractor.observeUserIsAuthenticated(): Observable<Boolean> =
        this.observeCurrentUser()
                .map { holder -> (holder.value != null) }