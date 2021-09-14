package com.ruslan.hlushan.core.ui.fragment

import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.command.DialogCommand
import com.ruslan.hlushan.core.ui.dialog.command.DialogCommandsHandler

@UiMainThread
class FragmentDialogCommandsHandler(private val fragment: BaseFragment) : DialogCommandsHandler() {

    override fun canExecuteCommands(): Boolean =
        (fragment.isResumed
                && fragment.isAdded
                && !fragment.instanceStateSaved)

    override fun executeCommand(command: DialogCommand) =
        command.execute(fragment.childFragmentManager)
}