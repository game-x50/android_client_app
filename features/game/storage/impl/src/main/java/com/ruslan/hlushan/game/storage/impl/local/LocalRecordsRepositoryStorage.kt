package com.ruslan.hlushan.game.storage.impl.local

import android.content.Context
import android.content.SharedPreferences
import com.ruslan.hlushan.android.storage.ReferencePreferencesDelegate
import com.ruslan.hlushan.android.storage.SharedPrefsProvider
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager
import io.reactivex.Completable
import org.threeten.bp.Instant
import javax.inject.Inject

internal interface LocalRecordsRepositoryStorage {

    val lastCreatedTimestamp: RemoteInfo.CreatedTimestamp

    fun storeLastCreatedTimestamp(newLastCreatedTimestamp: RemoteInfo.CreatedTimestamp): Completable
}

private const val KEY_LAST_CREATED_TIMESTAMP = "KEY_LAST_CREATED_TIMESTAMP"

internal class LocalRecordsRepositoryStorageImpl
@Inject
constructor(
        appContext: Context,
        private val schedulersManager: SchedulersManager
) : LocalRecordsRepositoryStorage {

    override var lastCreatedTimestamp: RemoteInfo.CreatedTimestamp by ReferencePreferencesDelegate(
            preferences = SharedPrefsProvider.providePrefs(appContext, "app_game_prefs"),
            writer = createLastCreatedTimestampWriter(),
            reader = createLastCreatedTimestampReader()
    )
        private set

    override fun storeLastCreatedTimestamp(newLastCreatedTimestamp: RemoteInfo.CreatedTimestamp): Completable =
            Completable.fromAction { lastCreatedTimestamp = newLastCreatedTimestamp }
                    .subscribeOn(schedulersManager.ui)
}

private fun createLastCreatedTimestampWriter():
        (SharedPreferences.Editor, RemoteInfo.CreatedTimestamp) -> SharedPreferences.Editor =
        { editor, newValue ->
            editor.putLong(KEY_LAST_CREATED_TIMESTAMP, newValue.value.toEpochMilli())
        }

private fun createLastCreatedTimestampReader(): (SharedPreferences) -> RemoteInfo.CreatedTimestamp =
        { prefs ->
            val instant = Instant.ofEpochMilli(prefs.getLong(KEY_LAST_CREATED_TIMESTAMP, 0L))
            RemoteInfo.CreatedTimestamp(instant)
        }