package com.ruslan.hlushan.core.ui.api.presentation.command.strategy

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