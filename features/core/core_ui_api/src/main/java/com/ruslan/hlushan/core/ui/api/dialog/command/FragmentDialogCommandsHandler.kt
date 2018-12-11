package com.ruslan.hlushan.core.ui.api.dialog.command

import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.view.fragment.BaseFragment

/**
 * @author Ruslan Hlushan on 2019-09-05
 */

@UiMainThread
class FragmentDialogCommandsHandler(private val fragment: BaseFragment) : DialogCommandsHandler() {

    override fun canExecuteCommands(): Boolean =
        (fragment.isResumed
                && fragment.isAdded
                && !fragment.instanceStateSaved)

    override fun executeCommand(command: DialogCommand) =
        command.execute(fragment.childFragmentManager)
}