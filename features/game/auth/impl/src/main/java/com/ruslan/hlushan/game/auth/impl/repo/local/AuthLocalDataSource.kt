package com.ruslan.hlushan.game.auth.impl.repo.local

import com.ruslan.hlushan.core.value.holder.ValueHolder
import com.ruslan.hlushan.game.api.auth.dto.User
import io.reactivex.Completable
import io.reactivex.Observable

internal interface AuthLocalDataSource {

    fun initWith(userId: User.Id?, userEmail: User.Email?)

    fun getUser(): User?

    fun getUserToken(): String?

    fun observeCurrentUser(): Observable<ValueHolder<User?>>

    fun storeUser(user: User?): Completable

    fun storeUserToken(token: String?): Completable
}

internal fun AuthLocalDataSource.logOut(): Completable =
        this.storeUser(null)
                .andThen(this.storeUserToken(null))