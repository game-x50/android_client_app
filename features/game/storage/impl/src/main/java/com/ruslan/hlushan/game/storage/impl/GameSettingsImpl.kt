package com.ruslan.hlushan.game.storage.impl

import android.content.Context
import android.content.SharedPreferences
import com.ruslan.hlushan.android.extensions.ReferencePreferencesDelegate
import com.ruslan.hlushan.android.storage.SharedPrefsProvider
import com.ruslan.hlushan.game.api.GameSettings
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.OrderType
import javax.inject.Inject

private const val KEY_GAME_RECORDS_ORDER_VARIANT = "KEY_GAME_RECORDS_ORDER_VARIANT"
private const val KEY_GAME_RECORDS_ORDER_TYPE = "KEY_GAME_RECORDS_ORDER_TYPE"
private const val INVALID_ORDINAL = -1

internal class GameSettingsImpl
@Inject
constructor(appContext: Context) : GameSettings {

    override var orderParams: GameRecordWithSyncState.Order.Params by ReferencePreferencesDelegate(
            preferences = SharedPrefsProvider.providePrefs(appContext, "game_setting_impl"),
            writer = createOrderParamsWriter(),
            reader = createOrderParamsReader()
    )
}

@SuppressWarnings("MaxLineLength")
private fun createOrderParamsWriter(): ((SharedPreferences.Editor, GameRecordWithSyncState.Order.Params) -> SharedPreferences.Editor) =
        { editor, newValue ->
            editor.putInt(KEY_GAME_RECORDS_ORDER_VARIANT, newValue.variant.ordinal)
                    .putInt(KEY_GAME_RECORDS_ORDER_TYPE, newValue.type.ordinal)
        }

private fun createOrderParamsReader(): (SharedPreferences) -> GameRecordWithSyncState.Order.Params =
        { prefs ->
            @SuppressWarnings("MaxLineLength")
            val variant = (GameRecordWithSyncState.Order.Variant.fromOrdinal(prefs.getInt(KEY_GAME_RECORDS_ORDER_VARIANT, INVALID_ORDINAL))
                           ?: GameRecordWithSyncState.Order.Variant.TOTAL_SUM)
            val type = (OrderType.fromOrdinal(prefs.getInt(KEY_GAME_RECORDS_ORDER_TYPE, INVALID_ORDINAL))
                        ?: OrderType.DESC)

            GameRecordWithSyncState.Order.Params(variant, type)
        }