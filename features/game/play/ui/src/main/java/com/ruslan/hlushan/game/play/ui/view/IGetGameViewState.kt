package com.ruslan.hlushan.game.play.ui.view

import com.ruslan.hlushan.game.api.play.dto.GameState

/**
 * @author Ruslan Hlushan on 10/16/18.
 */
interface IGetGameViewState {

    fun copyGameState(): GameState
}