package com.ruslan.hlushan.game.play.api

import com.ruslan.hlushan.game.api.play.dto.GameState

interface IGetGameViewState {

    fun copyGameState(): GameState
}