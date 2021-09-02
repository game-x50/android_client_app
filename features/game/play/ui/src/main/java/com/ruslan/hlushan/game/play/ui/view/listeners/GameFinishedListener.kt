package com.ruslan.hlushan.game.play.ui.view.listeners

import com.ruslan.hlushan.game.core.api.play.dto.GameState

/**
 * @author Ruslan Hlushan on 10/15/18.
 */
fun interface GameFinishedListener {

    fun onGameFinished(gameState: GameState)
}