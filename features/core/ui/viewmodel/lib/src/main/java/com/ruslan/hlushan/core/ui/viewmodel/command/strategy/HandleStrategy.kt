package com.ruslan.hlushan.core.ui.viewmodel.command.strategy

interface HandleStrategy {

    fun <Command : Any> beforeApply(
            currentState: List<Command>,
            incomingCommand: Command
    ): List<Command>

    fun <Command : Any> afterApply(
            currentState: List<Command>,
            incomingCommand: Command
    ): List<Command>
}