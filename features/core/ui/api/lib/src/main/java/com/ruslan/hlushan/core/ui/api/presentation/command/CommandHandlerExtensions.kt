package com.ruslan.hlushan.core.ui.api.presentation.command

import androidx.lifecycle.Lifecycle
import com.ruslan.hlushan.core.api.utils.thread.SingleThreadSafety
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.StrategyCommand
import com.ruslan.hlushan.core.ui.api.presentation.lifecycle.LifecyclePluginObserver
import com.ruslan.hlushan.third_party.rxjava2.extensions.safetyDispose
import io.reactivex.disposables.Disposable

fun <Command : StrategyCommand> LifecyclePluginObserver.Owner.handleCommandQueue(
        commandQueue: CommandQueue<Command>,
        handler: (Command) -> Unit
) {
    val observer = CommandQueueLifecycleObserver(
            commandQueue,
            handler
    )

    this.addLifecyclePluginObserver(observer)

    val localCurrentState = this.currentState
    if ((localCurrentState != null) && (localCurrentState >= Lifecycle.State.STARTED)) {
        observer.startCommandsHandling()
    }
}

@SingleThreadSafety
private class CommandQueueLifecycleObserver<Command : StrategyCommand>(
        private val commandQueue: CommandQueue<Command>,
        private val handler: (Command) -> Unit
) : LifecyclePluginObserver {

    private var disposable: Disposable? = null

    override fun onAfterSuperStart() = startCommandsHandling()

    override fun onBeforeSuperStop() = disposeCommands()

    fun startCommandsHandling() {
        disposeCommands()
        handleQueuedCommands()
        observeCommands()
    }

    private fun handleQueuedCommands() {
        for (singleCommand: Command in commandQueue.commands) {
            handleSingleCommand(singleCommand)
        }
    }

    private fun observeCommands() {
        disposable = commandQueue.observeNewCommand()
                .subscribe(::handleSingleCommand)
    }

    private fun handleSingleCommand(command: Command) {
        handler(command)
        commandQueue.notifyAfterCommandExecute(command)
    }

    private fun disposeCommands() {
        disposable?.safetyDispose()
        disposable = null
    }
}