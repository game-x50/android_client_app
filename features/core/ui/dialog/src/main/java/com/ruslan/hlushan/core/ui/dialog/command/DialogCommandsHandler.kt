package com.ruslan.hlushan.core.ui.dialog.command

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.api.utils.thread.checkThread
import com.ruslan.hlushan.core.extensions.removeFirst
import com.ruslan.hlushan.core.ui.api.utils.UiMainThreadChecker
import com.ruslan.hlushan.third_party.androidx.fragment.extensions.dismissNowSafety

@UiMainThread
abstract class DialogCommandsHandler {

    private val threadChecker: ThreadChecker = UiMainThreadChecker

    private val commandsQueue: MutableList<DialogCommand> = mutableListOf()

    protected abstract fun canExecuteCommands(): Boolean

    protected abstract fun executeCommand(command: DialogCommand)

    fun executeShowOrAddToQueue(command: DialogCommand) {
        threadChecker.checkThread()

        if (canExecuteCommands()) {
            executeCommand(command)
        } else {
            commandsQueue.remove(command)
            commandsQueue.add(command)
        }
    }

    fun removeFromQueueOrExecuteClose(tag: String) {
        threadChecker.checkThread()

        val removedCommand: DialogCommand? = commandsQueue.removeFirst { command -> command.tag == tag }
        if (removedCommand == null) {
            val closeDialogCommand = CloseDialogCommand(tag)

            if (canExecuteCommands()) {
                executeCommand(closeDialogCommand)
            } else {
                commandsQueue.add(closeDialogCommand)
            }
        }
    }

    fun handlePendingCommands() {
        threadChecker.checkThread()

        if (canExecuteCommands()) {
            val commandsList = commandsQueue.toList()

            commandsQueue.clear()

            commandsList.forEach { pendingCommand ->
                executeCommand(pendingCommand)
            }
        }
    }

    interface Owner {
        val dialogCommandsHandler: DialogCommandsHandler
    }
}

private class CloseDialogCommand(override val tag: String) : DialogCommand() {

    @UiMainThread
    override fun execute(fragmentManager: FragmentManager) {
        (fragmentManager.findFragmentByTag(tag) as? DialogFragment)?.dismissNowSafety()
    }
}