package com.ruslan.hlushan.game.auth.impl.repo.remote

import com.ruslan.hlushan.core.result.OpResult
import com.ruslan.hlushan.core.value.holder.ValueHolder
import com.ruslan.hlushan.game.api.auth.dto.AuthError
import com.ruslan.hlushan.game.api.auth.dto.User
import io.reactivex.Completable
import io.reactivex.Single

internal interface AuthRemoteDataSource {

    val remoteUserInfo: RemoteUserInfo?

    fun createNewUser(
            nickname: User.Nickname,
            email: User.Email,
            password: User.Password
    ): Single<OpResult<User, AuthError.Register>>

    fun logIn(
            email: User.Email,
            password: User.Password
    ): Single<OpResult<User, AuthError.Login>>

    fun sendPasswordResetEmail(email: User.Email): Completable

    fun updateUserWith(
            newNickname: User.Nickname,
            newPassword: User.Password,
            currentUser: User,
            oldPassword: User.Password
    ): Single<OpResult<User, AuthError>>

    fun updateUserTokenAsync(): Single<String>

    fun addIdTokenListener(listener: (String?) -> Unit)

    fun getCurrentUser(): Single<ValueHolder<User?>>

    fun logOut(): Completable
}