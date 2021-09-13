package com.ruslan.hlushan.game.play.api.listeners

import com.ruslan.hlushan.game.api.play.dto.GameState

fun interface GameFinishedListener {

    fun onGameFinished(gameState: GameState)
}