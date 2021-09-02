package com.ruslan.hlushan.game.api

import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState

interface GameSettings {

    var orderParams: GameRecordWithSyncState.Order.Params
}