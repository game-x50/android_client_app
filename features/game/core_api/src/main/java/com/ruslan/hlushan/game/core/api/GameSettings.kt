package com.ruslan.hlushan.game.core.api

import com.ruslan.hlushan.game.core.api.play.dto.GameRecordWithSyncState

interface GameSettings {

    var orderParams: GameRecordWithSyncState.Order.Params
}