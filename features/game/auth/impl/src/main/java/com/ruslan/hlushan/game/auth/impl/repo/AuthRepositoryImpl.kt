package com.ruslan.hlushan.game.auth.impl.repo

import android.content.Context
import android.content.SharedPreferences
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
import com.ruslan.hlushan.android.storage.SharedPrefsProvider
import com.ruslan.hlushan.core.api.dto.OperationResult
import com.ruslan.hlushan.core.api.dto.OperationResultVoid
import com.ruslan.hlushan.core.api.dto.ValueHolder
import com.ruslan.hlushan.core.api.dto.VoidOperationResult
import com.ruslan.hlushan.core.api.dto.flatMapCompletableSuccess
import com.ruslan.hlushan.core.api.dto.flatMapNestedSuccess
import com.ruslan.hlushan.core.api.dto.mapSuccess
import com.ruslan.hlushan.core.api.dto.toOperationResult
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.error.NetworkException
import com.ruslan.hlushan.core.error.mapError
import com.ruslan.hlushan.core.extensions.ifNotNull
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.game.api.auth.dto.AuthError
import com.ruslan.hlushan.game.api.auth.dto.User
import com.ruslan.hlushan.game.auth.impl.repo.dto.server.UserNameRequest
import com.ruslan.hlushan.third_party.rxjava2.extensions.tryEmitOrProvideError
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

private const val USERS_COLLECTION = "users"
private const val USER_NICKNAME = "nickname"

private const val KEY_USER_NICKNAME = "KEY_USER_NICKNAME"

@SuppressWarnings("TooManyFunctions")
internal class AuthRepositoryImpl
@Inject
constructor(
        context: Context,
        private val authHttpsApi: AuthHttpsApi,
        private val schedulersManager: SchedulersManager,
        private val appLogger: AppLogger
) : AuthRepository {

    private val authPrefs: SharedPreferences = SharedPrefsProvider.provideSecurePrefs(
            context = context.applicationContext,
            prefsName = "app_auth_prefs",
            keySetName = "auth_master_keyset",
            prefFileName = "auth_master_key_preference",
            masterKeyUri = "auth_master_key",
            dKeySetName = "auth_dmaster_keyset",
            dPrefFileName = "auth_dmaster_key_preference",
            dMasterKeyUri = "auth_dmaster_key"
    )

    private val userSubject: BehaviorSubject<ValueHolder<User?>>
    private val atomicToken: AtomicReference<String?> = AtomicReference(null)

    private val firebaseAuth: FirebaseAuth get() = FirebaseAuth.getInstance()

    private val firebaseFirestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    init {
        val userNickname: User.Nickname? = authPrefs.getString(KEY_USER_NICKNAME, null)
                ?.let(User.Nickname::createIfValid)

        val user: User? = ifNotNull(
                firebaseAuth.currentUser?.uid?.let(User::Id),
                firebaseAuth.currentUser?.email?.let(User.Email::createIfValid),
                userNickname
        ) { nonNullUserId, nonNullUserEmail, nonNullUserNickname ->
            User(nonNullUserId, nonNullUserEmail, nonNullUserNickname)
        }

        userSubject = BehaviorSubject.createDefault(ValueHolder(user))

        updateUserFromRemote()
        observeUserToken()
    }

    override fun getUser(): User? = userSubject.value?.value

    override fun observeCurrentUser(): Observable<ValueHolder<User?>> = userSubject

    override fun getUserToken(): String? = atomicToken.get()

    @Synchronized
    override fun updateUserToken() = updateUserTokenAsync().blockingAwait()

    override fun createNewUser(
            nickname: User.Nickname,
            email: User.Email,
            password: User.Password
    ): Single<VoidOperationResult<AuthError.UserWithSuchCredentialsExists>> =
            provideErrorIfUserNameExists(nickname)
                    .flatMapNestedSuccess { createNewUserAndReturnUid(email, password) }
                    .mapSuccess { userUid ->
                        User(id = User.Id(userUid), email = email, nickname = nickname)
                    }
                    .flatMapCompletableSuccess { user -> storeNewUserRemoteAndLocal(user) }
                    .mapSuccess { Unit }

    override fun logIn(
            email: User.Email,
            password: User.Password
    ): Single<VoidOperationResult<AuthError.InvalidUserCredentials>> =
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
                    .flatMapCompletableSuccess { firebaseUser -> updateCurrentUserFromRemote(firebaseUser) }
                    .mapSuccess { Unit }

    override fun sendPasswordResetEmail(email: User.Email): Completable =
            firebaseAuth.sendPasswordResetEmailRx(email.value)
                    .onErrorComplete { error -> error is FirebaseAuthInvalidUserException }
                    .subscribeOn(schedulersManager.io)

    override fun updateUserWith(
            newNickname: User.Nickname,
            newPassword: User.Password,
            oldPassword: User.Password
    ): Single<VoidOperationResult<AuthError>> =
            Single.fromCallable {
                getUser() ?: throw IllegalStateException("User is null")
            }.flatMap { currentUser ->
                logIn(email = currentUser.email, password = oldPassword)
                        .flatMapCompletableSuccess<Unit, AuthError> {
                            updateUserPassword(oldPassword = oldPassword, newPassword = newPassword)
                        }
                        .flatMapNestedSuccess<Unit, AuthError, Unit> {
                            val updatedUser = currentUser.copy(nickname = newNickname)
                            updateUser(currentUser, updatedUser)
                        }
            }

    override fun logOut(): Completable =
            Completable.fromAction { firebaseAuth.signOut() }
                    .andThen(storeUserLocal(null))
                    .andThen(storeUserTokenLocal(null))

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

    private fun storeNewUserRemoteAndLocal(user: User): Completable =
            firebaseFirestore.collection(USERS_COLLECTION)
                    .document(user.id.value)
                    .setRx(mapOf(USER_NICKNAME to user.nickname))
                    .subscribeOn(schedulersManager.io)
                    .andThen(storeUserLocal(user))

    private fun updateCurrentUserFromRemote(currentUser: FirebaseUser): Completable =
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
                    .flatMapCompletable { user -> storeUserLocal(user) }

    private fun storeUserLocal(user: User?): Completable =
            Single.just(true)
                    .observeOn(schedulersManager.ui)
                    .map {
                        userSubject.onNext(ValueHolder((user)))

                        authPrefs.edit()
                                .putString(KEY_USER_NICKNAME, user?.nickname?.value)
                                .apply()
                    }
                    .ignoreElement()

    private fun storeUserTokenLocal(token: String?): Completable =
            Completable.fromAction { atomicToken.set(token) }

    private fun updateUserFromRemote() {
        ifNotNull(firebaseAuth.currentUser) { currentUser ->
            updateCurrentUserFromRemote(currentUser)
                    .subscribe({ appLogger.log(this, "updateUserFromRemote : SUCCESS") },
                               { error -> appLogger.log(this, "updateUserFromRemote : ERROR!", error) })
        }
    }

    @Suppress("CheckResult")
    private fun observeUserToken() {
        updateUserTokenAsync()
                .subscribe({ appLogger.log(this, "getIdTokenRx : received") },
                           { error -> appLogger.log(this, "getIdTokenRx : ERROR!", error) })

        firebaseAuth.addIdTokenListener { internalTokenResult: InternalTokenResult ->
            //TODO: validate if this callback is called when login/logout/login
            storeUserTokenLocal(internalTokenResult.token)
                    .doOnComplete { updateUserFromRemote() }
                    .subscribe({ appLogger.log(this, "observeUserToken : received  new") },
                               { error -> appLogger.log(this, "observeUserToken : ERROR!", error) })
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

    private fun updateUser(
            currentUser: User,
            updatedUser: User
    ): Single<VoidOperationResult<AuthError.UserWithSuchCredentialsExists>> =
            Single.fromCallable { currentUser != updatedUser }
                    .flatMap { wasChanged ->
                        if (wasChanged) {
                            provideErrorIfUserNameExists(updatedUser.nickname)
                                    .flatMapCompletableSuccess { storeNewUserRemoteAndLocal(updatedUser) }
                        } else {
                            Single.just(OperationResultVoid())
                        }
                    }

    private fun updateUserTokenAsync(): Completable =
            Single.fromCallable {
                @Suppress("UnsafeCallOnNullableType")
                firebaseAuth.currentUser!!
            }
                    .flatMap { currentUser -> currentUser.getIdTokenRx(forceUpdate = true) }
                    .flatMapCompletable { token -> storeUserTokenLocal(token) }
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