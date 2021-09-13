package com.ruslan.hlushan.core.command

import com.ruslan.hlushan.core.command.strategy.StrategyCommand
import io.reactivex.Observable

/**
 * https://github.com/moxy-community/Moxy
 * */

interface CommandQueue<Command : StrategyCommand> {

    val commands: List<Command>

    fun observeNewCommand(): Observable<Command>

    fun notifyAfterCommandExecute(incomingCommand: Command)
}