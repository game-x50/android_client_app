package com.ruslan.hlushan.core.ui.api.presentation.command.strategy

class OneExecutionStateStrategy : HandleStrategy {

    override fun <Command : Any> beforeApply(
            currentState: List<Command>,
            incomingCommand: Command
    ): List<Command> =
            (currentState + incomingCommand)

    override fun <Command : Any> afterApply(
            currentState: List<Command>,
            incomingCommand: Command
    ): List<Command> =
            (currentState - incomingCommand)
}