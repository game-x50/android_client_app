package com.ruslan.hlushan.game.auth.impl.repo.local

import android.content.Context
import android.content.SharedPreferences
import com.ruslan.hlushan.android.storage.SharedPrefsProvider
import com.ruslan.hlushan.core.extensions.ifNotNull
import com.ruslan.hlushan.core.value.holder.ValueHolder
import com.ruslan.hlushan.game.api.auth.dto.User
import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

private const val KEY_USER_NICKNAME = "KEY_USER_NICKNAME"

internal class AuthLocalDataSourceImpl
@Inject
constructor(
        context: Context,
        private val schedulersManager: SchedulersManager
) : AuthLocalDataSource {

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

    private val userSubject: BehaviorSubject<ValueHolder<User?>> = BehaviorSubject.create()
    private val atomicToken: AtomicReference<String?> = AtomicReference(null)

    override fun initWith(userId: User.Id?, userEmail: User.Email?) {
        val userNickname: User.Nickname? = authPrefs.getString(KEY_USER_NICKNAME, null)
                ?.let(User.Nickname::createIfValid)

        val user: User? = ifNotNull(
                userId,
                userEmail,
                userNickname
        ) { nonNullUserId, nonNullUserEmail, nonNullUserNickname ->
            User(nonNullUserId, nonNullUserEmail, nonNullUserNickname)
        }

        userSubject.onNext(ValueHolder((user)))
    }

    override fun getUser(): User? = userSubject.value?.value

    override fun observeCurrentUser(): Observable<ValueHolder<User?>> = userSubject

    override fun getUserToken(): String? = atomicToken.get()

    override fun storeUser(user: User?): Completable =
            Single.just(true)
                    .observeOn(schedulersManager.ui)
                    .map {
                        userSubject.onNext(ValueHolder((user)))

                        authPrefs.edit()
                                .putString(KEY_USER_NICKNAME, user?.nickname?.value)
                                .apply()
                    }
                    .ignoreElement()

    override fun storeUserToken(token: String?): Completable =
            Completable.fromAction { atomicToken.set(token) }
}