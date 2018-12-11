package com.ruslan.hlushan.game.storage.impl.local

import android.content.Context
import android.content.SharedPreferences
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.storage.SharedPrefsProvider
import com.ruslan.hlushan.storage.delegate.ReferencePreferencesDelegate
import io.reactivex.Completable
import org.threeten.bp.Instant
import javax.inject.Inject

internal interface LocalRecordsRepositoryStorage {

    val lastCreatedTimestamp: Instant

    fun storeLastCreatedTimestamp(newLastCreatedTimestamp: Instant): Completable
}

private const val KEY_LAST_CREATED_TIMESTAMP = "KEY_LAST_CREATED_TIMESTAMP"

internal class LocalRecordsRepositoryStorageImpl
@Inject
constructor(
        appContext: Context,
        private val schedulersManager: SchedulersManager
) : LocalRecordsRepositoryStorage {

    override var lastCreatedTimestamp: Instant by ReferencePreferencesDelegate(
            preferences = SharedPrefsProvider.providePrefs(appContext, "app_game_prefs"),
            writer = createLastCreatedTimestampWriter(),
            reader = createLastCreatedTimestampReader()
    )
        private set

    override fun storeLastCreatedTimestamp(newLastCreatedTimestamp: Instant): Completable =
            Completable.fromAction { lastCreatedTimestamp = newLastCreatedTimestamp }
                    .subscribeOn(schedulersManager.ui)
}

private fun createLastCreatedTimestampWriter(): (SharedPreferences.Editor, Instant) -> SharedPreferences.Editor =
        { editor, newValue -> editor.putLong(KEY_LAST_CREATED_TIMESTAMP, newValue.toEpochMilli()) }

private fun createLastCreatedTimestampReader(): (SharedPreferences) -> Instant =
        { prefs -> Instant.ofEpochMilli(prefs.getLong(KEY_LAST_CREATED_TIMESTAMP, 0L)) }