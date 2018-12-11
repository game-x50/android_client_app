package com.ruslan.hlushan.core.ui.api.presentation.command.strategy

import com.ruslan.hlushan.extensions.withoutFirst

class AddToEndSingleStrategy : HandleStrategy {

    override fun <Command : Any> beforeApply(
            currentState: List<Command>,
            incomingCommand: Command
    ): List<Command> =
            currentState
                    .withoutFirst { command -> command.javaClass == incomingCommand.javaClass }
                    .plus(incomingCommand)

    override fun <Command : Any> afterApply(
            currentState: List<Command>,
            incomingCommand: Command
    ): List<Command> =
            currentState
}