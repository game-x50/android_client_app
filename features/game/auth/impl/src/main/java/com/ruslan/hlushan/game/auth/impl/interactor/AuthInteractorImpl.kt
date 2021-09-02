package com.ruslan.hlushan.game.auth.impl.interactor

import com.ruslan.hlushan.core.api.dto.ValueHolder
import com.ruslan.hlushan.core.api.dto.VoidOperationResult
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.game.auth.impl.repo.AuthRepository
import com.ruslan.hlushan.game.api.auth.AuthInteractor
import com.ruslan.hlushan.game.api.auth.dto.AuthError
import com.ruslan.hlushan.game.api.auth.dto.User
import com.ruslan.hlushan.game.api.play.ClearAllLocalGamesInfoUseCase
import com.ruslan.hlushan.game.api.sync.StartSyncUseCase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val WAIT_UNTIL_TOKEN_CHANGE_DELAY_SECONDS = 10L
private const val WAIT_UNTIL_TOKEN_CHANGE_RETRY_COUNT = 6L

internal class AuthInteractorImpl
@Inject
constructor(
        private val schedulersManager: SchedulersManager,
        private val authRepository: AuthRepository,
        private val startSyncUseCase: StartSyncUseCase,
        private val clearAllLocalGamesInfoUseCase: ClearAllLocalGamesInfoUseCase
) : AuthInteractor {

    override fun createNewUser(
            nickname: User.Nickname,
            email: User.Email,
            password: User.Password
    ): Single<VoidOperationResult<AuthError.UserWithSuchCredentialsExists>> =
            waitUntilTokenChangeAndStartSyncUseCase(authRepository.createNewUser(nickname, email, password))

    override fun logIn(
            email: User.Email,
            password: User.Password
    ): Single<VoidOperationResult<AuthError.InvalidUserCredentials>> =
            waitUntilTokenChangeAndStartSyncUseCase(authRepository.logIn(email, password))

    override fun sendPasswordResetEmail(email: User.Email): Completable =
            authRepository.sendPasswordResetEmail(email)

    override fun logOut(): Completable =
            authRepository.logOut()
                    .andThen(clearAllLocalGamesInfoUseCase.clearAllLocalGamesInfo())

    override fun getUser(): User? = authRepository.getUser()

    override fun observeCurrentUser(): Observable<ValueHolder<User?>> =
            authRepository.observeCurrentUser()

    override fun updateUserWith(
            newNickname: User.Nickname,
            newPassword: User.Password,
            oldPassword: User.Password
    ): Single<VoidOperationResult<AuthError>> =
            authRepository.updateUserWith(newNickname, newPassword, oldPassword)

    private fun <T> waitUntilTokenChangeAndStartSyncUseCase(source: Single<T>): Single<T> =
            Single.zip<T, Unit, T>(
                    source,
                    waitUntilTokenChange(),
                    { result, unit -> result }
            )
                    .doOnSuccess { startSyncUseCase.start() }

    private fun waitUntilTokenChange(): Single<Unit> =
            Single.fromCallable { authRepository.getUserToken().orEmpty() }
                    .flatMapPublisher { oldToken ->
                        Single.timer(WAIT_UNTIL_TOKEN_CHANGE_DELAY_SECONDS, TimeUnit.SECONDS, schedulersManager.io)
                                .repeatUntil { oldToken != authRepository.getUserToken().orEmpty() }
                                .take(WAIT_UNTIL_TOKEN_CHANGE_RETRY_COUNT)
                    }
                    .ignoreElements()
                    .toSingleDefault(Unit)
}