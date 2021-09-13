package com.ruslan.hlushan.core.command.strategy

class SkipStrategy : HandleStrategy {

    override fun <Command : Any> beforeApply(
            currentState: List<Command>,
            incomingCommand: Command
    ): List<Command> =
            currentState

    override fun <Command : Any> afterApply(
            currentState: List<Command>,
            incomingCommand: Command
    ): List<Command> =
            currentState
}