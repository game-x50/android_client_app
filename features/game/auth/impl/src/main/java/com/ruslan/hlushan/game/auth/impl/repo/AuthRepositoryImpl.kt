package com.ruslan.hlushan.game.auth.impl.repo

import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.result.VoidOperationResult
import com.ruslan.hlushan.core.result.flatMapCompletableSuccess
import com.ruslan.hlushan.core.result.flatMapSuccess
import com.ruslan.hlushan.core.result.mapSuccess
import com.ruslan.hlushan.core.value.holder.ValueHolder
import com.ruslan.hlushan.game.api.auth.dto.AuthError
import com.ruslan.hlushan.game.api.auth.dto.User
import com.ruslan.hlushan.game.auth.impl.repo.local.AuthLocalDataSource
import com.ruslan.hlushan.game.auth.impl.repo.local.logOut
import com.ruslan.hlushan.game.auth.impl.repo.remote.AuthRemoteDataSource
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

//TODO: #write_unit_tests
internal class AuthRepositoryImpl
@Inject
constructor(
        private val localDataSource: AuthLocalDataSource,
        private val remoteDataSource: AuthRemoteDataSource,
        private val appLogger: AppLogger
) : AuthRepository {

    init {
        val remoteUserInfo = remoteDataSource.remoteUserInfo
        localDataSource.initWith(
                userId = remoteUserInfo?.userId,
                userEmail = remoteUserInfo?.userEmail
        )

        updateUserFromRemote()
        observeUserToken()
    }

    override fun getUser(): User? = localDataSource.getUser()

    override fun observeCurrentUser(): Observable<ValueHolder<User?>> = localDataSource.observeCurrentUser()

    override fun getUserToken(): String? = localDataSource.getUserToken()

    @Synchronized
    override fun updateUserToken() = updateUserTokenAsync().blockingAwait()

    override fun createNewUser(
            nickname: User.Nickname,
            email: User.Email,
            password: User.Password
    ): Single<VoidOperationResult<AuthError.Register>> =
            remoteDataSource.createNewUser(nickname, email, password)
                    .flatMapCompletableSuccess { user -> localDataSource.storeUser(user) }
                    .mapSuccess { Unit }

    override fun logIn(
            email: User.Email,
            password: User.Password
    ): Single<VoidOperationResult<AuthError.Login>> =
            remoteDataSource.logIn(email, password)
                    .flatMapCompletableSuccess { user -> localDataSource.storeUser(user) }
                    .mapSuccess { Unit }

    override fun sendPasswordResetEmail(email: User.Email): Completable =
            remoteDataSource.sendPasswordResetEmail(email)

    override fun updateUserWith(
            newNickname: User.Nickname,
            newPassword: User.Password,
            oldPassword: User.Password
    ): Single<VoidOperationResult<AuthError>> =
            Single.fromCallable {
                getUser() ?: throw IllegalStateException("User is null")
            }.flatMap { currentUser ->
                remoteDataSource.updateUserWith(
                        newNickname = newNickname,
                        newPassword = newPassword,
                        currentUser = currentUser,
                        oldPassword = oldPassword
                )
            }
                    .flatMapSuccess { updatedUser ->
                        localDataSource.storeUser(updatedUser)
                                .toSingleDefault(Unit)
                    }

    override fun logOut(): Completable =
            remoteDataSource.logOut()
                    .andThen(localDataSource.logOut())

    @Suppress("CheckResult")
    private fun updateUserFromRemote() {
        remoteDataSource.getCurrentUser()
                .flatMapCompletable { valueHolder -> localDataSource.storeUser(valueHolder.value) }
                .subscribe({ appLogger.log(this, "updateUserFromRemote : SUCCESS") },
                           { error -> appLogger.log(this, "updateUserFromRemote : ERROR!", error) })
    }

    @Suppress("CheckResult")
    private fun observeUserToken() {
        updateUserTokenAsync()
                .subscribe({ appLogger.log(this, "getIdTokenRx : received") },
                           { error -> appLogger.log(this, "getIdTokenRx : ERROR!", error) })

        remoteDataSource.addIdTokenListener { token ->
            localDataSource.storeUserToken(token)
                    .doOnComplete { updateUserFromRemote() }
                    .subscribe({ appLogger.log(this, "observeUserToken : received  new") },
                               { error -> appLogger.log(this, "observeUserToken : ERROR!", error) })
        }
    }

    private fun updateUserTokenAsync(): Completable =
            remoteDataSource.updateUserTokenAsync()
                    .flatMapCompletable { token -> localDataSource.storeUserToken(token) }
}