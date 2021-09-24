package com.ruslan.hlushan.game.auth.impl.repo.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.internal.InternalTokenResult
import com.ruslan.hlushan.core.api.dto.OperationResult
import com.ruslan.hlushan.core.api.dto.OperationResultVoid
import com.ruslan.hlushan.core.api.dto.ValueHolder
import com.ruslan.hlushan.core.api.dto.VoidOperationResult
import com.ruslan.hlushan.core.api.dto.flatMapCompletableSuccess
import com.ruslan.hlushan.core.api.dto.flatMapNestedSuccess
import com.ruslan.hlushan.core.api.dto.flatMapSuccess
import com.ruslan.hlushan.core.api.dto.mapSuccess
import com.ruslan.hlushan.core.api.dto.toOperationResult
import com.ruslan.hlushan.core.error.NetworkException
import com.ruslan.hlushan.core.error.mapError
import com.ruslan.hlushan.game.api.auth.dto.AuthError
import com.ruslan.hlushan.game.api.auth.dto.User
import com.ruslan.hlushan.game.auth.impl.repo.remote.dto.server.UserNameRequest
import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager
import com.ruslan.hlushan.third_party.rxjava2.extensions.tryEmitOrProvideError
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

private const val USERS_COLLECTION = "users"
private const val USER_NICKNAME = "nickname"

internal class AuthRemoteRepositoryImpl
@Inject
constructor(
        private val authHttpsApi: AuthHttpsApi,
        private val schedulersManager: SchedulersManager
) : AuthRemoteDataSource {

    private val firebaseAuth: FirebaseAuth get() = FirebaseAuth.getInstance()

    private val firebaseFirestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    override val remoteUserInfo: RemoteUserInfo?
        get() = firebaseAuth.currentUser?.toRemoteUserInfo()

    override fun createNewUser(
            nickname: User.Nickname,
            email: User.Email,
            password: User.Password
    ): Single<OperationResult<User, AuthError.UserWithSuchCredentialsExists>> =
            provideErrorIfUserNameExists(nickname)
                    .flatMapNestedSuccess { createNewUserAndReturnUid(email, password) }
                    .mapSuccess { userUid ->
                        User(id = User.Id(userUid), email = email, nickname = nickname)
                    }
                    .flatMapCompletableSuccess { user -> storeUserRemote(user) }

    override fun logIn(
            email: User.Email,
            password: User.Password
    ): Single<OperationResult<User, AuthError.InvalidUserCredentials>> =
            firebaseAuth.signInWithEmailAndPasswordRx(email, password)
                    .toOperationResult<FirebaseUser, AuthError.InvalidUserCredentials>()
                    .onErrorResumeNext { error ->
                        if (error.isFirebaseInvalidUserCredentialsError()) {
                            Single.just(OperationResult.Error(AuthError.InvalidUserCredentials()))
                        } else {
                            Single.error(error)
                        }
                    }
                    .subscribeOn(schedulersManager.io)
                    .flatMapSuccess { firebaseUser -> loadCurrentUser(firebaseUser) }

    override fun updateUserWith(
            newNickname: User.Nickname,
            newPassword: User.Password,
            currentUser: User,
            oldPassword: User.Password
    ): Single<OperationResult<User, AuthError>> =
            logIn(email = currentUser.email, password = oldPassword)
                    .flatMapCompletableSuccess<User, AuthError> {
                        updateUserPassword(oldPassword = oldPassword, newPassword = newPassword)
                    }
                    .flatMapNestedSuccess<User, AuthError, User> {
                        val updatedUser = currentUser.copy(nickname = newNickname)
                        updateUser(currentUser, updatedUser)
                                .mapSuccess { updatedUser }
                    }

    override fun sendPasswordResetEmail(email: User.Email): Completable =
            firebaseAuth.sendPasswordResetEmailRx(email.value)
                    .onErrorComplete { error -> error is FirebaseAuthInvalidUserException }
                    .subscribeOn(schedulersManager.io)

    override fun updateUserTokenAsync(): Single<String> =
            Single.fromCallable {
                @Suppress("UnsafeCallOnNullableType")
                firebaseAuth.currentUser!!
            }
                    .flatMap { currentUser -> currentUser.getIdTokenRx(forceUpdate = true) }

    override fun getCurrentUser(): Single<ValueHolder<User?>> =
            Single.just(true)
                    .flatMap {
                        val currentFirebaseUser = firebaseAuth.currentUser
                        if (currentFirebaseUser != null) {
                            loadCurrentUser(currentFirebaseUser)
                                    .map { user -> ValueHolder<User?>(user) }
                        } else {
                            Single.just(ValueHolder<User?>(null))
                        }
                    }

    override fun addIdTokenListener(listener: (String?) -> Unit) {
        firebaseAuth.addIdTokenListener { internalTokenResult: InternalTokenResult ->
            //TODO: validate if this callback is called when login/logout/login
            listener(internalTokenResult.token)
        }
    }

    override fun logOut(): Completable =
            Completable.fromAction { firebaseAuth.signOut() }

    private fun provideErrorIfUserNameExists(
            nickname: User.Nickname
    ): Single<OperationResult<Unit, AuthError.UserWithSuchCredentialsExists>> =
            Single.fromCallable { UserNameRequest(userName = nickname.value) }
                    .flatMap { request -> authHttpsApi.checkUniqueUserName(request) }
                    .subscribeOn(schedulersManager.io)
                    .map { response ->
                        if (response.unique) {
                            OperationResultVoid()
                        } else {
                            OperationResult.Error(AuthError.UserWithSuchCredentialsExists())
                        }
                    }

    private fun createNewUserAndReturnUid(
            email: User.Email,
            password: User.Password
    ): Single<OperationResult<String, AuthError.UserWithSuchCredentialsExists>> =
            firebaseAuth.createUserWithEmailAndPasswordRx(email.value, password.value)
                    .subscribeOn(schedulersManager.io)
                    .map<OperationResult<String, AuthError.UserWithSuchCredentialsExists>> { firebaseUser ->
                        OperationResult.Success(firebaseUser.uid)
                    }
                    .onErrorResumeNext { error ->
                        if (error is FirebaseAuthUserCollisionException) {
                            Single.just(OperationResult.Error(AuthError.UserWithSuchCredentialsExists()))
                        } else {
                            Single.error(error)
                        }
                    }

    private fun updateUserPassword(oldPassword: User.Password, newPassword: User.Password): Completable =
            Single.fromCallable { oldPassword != newPassword }
                    .flatMapCompletable { wasChanged ->
                        if (wasChanged) {
                            @Suppress("UnsafeCallOnNullableType")
                            firebaseAuth.currentUser!!.updatePasswordRx(newPassword)
                        } else {
                            Completable.complete()
                        }
                    }

    private fun storeUserRemote(user: User): Completable =
            firebaseFirestore.collection(USERS_COLLECTION)
                    .document(user.id.value)
                    .setRx(mapOf(USER_NICKNAME to user.nickname))
                    .subscribeOn(schedulersManager.io)

    private fun updateUser(
            currentUser: User,
            updatedUser: User
    ): Single<VoidOperationResult<AuthError.UserWithSuchCredentialsExists>> =
            Single.fromCallable { currentUser != updatedUser }
                    .flatMap { wasChanged ->
                        if (wasChanged) {
                            provideErrorIfUserNameExists(updatedUser.nickname)
                                    .flatMapCompletableSuccess { storeUserRemote(updatedUser) }
                        } else {
                            Single.just(OperationResultVoid())
                        }
                    }

    private fun loadCurrentUser(currentUser: FirebaseUser): Single<User> =
            firebaseFirestore.collection(USERS_COLLECTION)
                    .document(currentUser.uid)
                    .getRx()
                    .map { documentSnapshot ->
                        @Suppress("UnsafeCallOnNullableType")
                        User(
                                id = User.Id(documentSnapshot.id),
                                email = User.Email.createIfValid(currentUser.email)!!,
                                nickname = User.Nickname.createIfValid(documentSnapshot[USER_NICKNAME].toString())!!
                        )
                    }
                    .subscribeOn(schedulersManager.io)
}

@Suppress("UnsafeCallOnNullableType")
private fun FirebaseAuth.createUserWithEmailAndPasswordRx(email: String, password: String): Single<FirebaseUser> =
        taskToSingle { this.createUserWithEmailAndPassword(email, password) }
                .map { result -> result.user!! }

@Suppress("UnsafeCallOnNullableType")
private fun FirebaseAuth.signInWithEmailAndPasswordRx(
        email: User.Email,
        password: User.Password
): Single<FirebaseUser> =
        taskToSingle { this.signInWithEmailAndPassword(email.value, password.value) }
                .map { result -> result.user!! }

private fun FirebaseAuth.sendPasswordResetEmailRx(email: String): Completable =
        taskToCompletable { this.sendPasswordResetEmail(email) }

@Suppress("UnsafeCallOnNullableType")
private fun FirebaseUser.getIdTokenRx(forceUpdate: Boolean): Single<String> =
        taskToSingle { this.getIdToken(forceUpdate) }
                .map { result -> result.token!! }

private fun FirebaseUser.updatePasswordRx(password: User.Password): Completable =
        taskToCompletable { this.updatePassword(password.value) }

private fun DocumentReference.setRx(any: Any): Completable =
        taskToCompletable { this.set(any) }

private fun DocumentReference.getRx(): Single<DocumentSnapshot> =
        taskToSingle { this.get() }

@Suppress("UnsafeCallOnNullableType")
private inline fun <R : Any> taskToSingle(crossinline createTask: () -> Task<R>): Single<R> =
        Single.create<R> { emitter ->
            createTask()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            emitter.tryEmitOrProvideError(task.result!!)
                        } else {
                            emitter.tryOnError(task.exception!!)
                        }
                    }
        }
                .mapError { error -> mapFirebaseNetworkError(error) }

@Suppress("UnsafeCallOnNullableType")
private inline fun taskToCompletable(crossinline createTask: () -> Task<Void>): Completable =
        Completable.create { emitter ->
            createTask()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            emitter.tryEmitOrProvideError()
                        } else {
                            emitter.tryOnError(task.exception!!)
                        }
                    }
        }
                .mapError { error -> mapFirebaseNetworkError(error) }

private fun Throwable.isFirebaseInvalidUserCredentialsError(): Boolean =
        when (this) {
            is FirebaseAuthInvalidCredentialsException,
            is FirebaseAuthInvalidUserException -> true

            else                                -> false
        }

private inline fun mapFirebaseNetworkError(error: Throwable): Throwable =
        if (error is FirebaseNetworkException) {
            NetworkException(error)
        } else {
            error
        }

private fun FirebaseUser.toRemoteUserInfo(): RemoteUserInfo? {
    val userEmail = this.email?.let(User.Email::createIfValid)

    return if (userEmail != null) {
        return RemoteUserInfo(
                userId = this.uid.let(User::Id),
                userEmail = userEmail,
        )
    } else {
        null
    }
}