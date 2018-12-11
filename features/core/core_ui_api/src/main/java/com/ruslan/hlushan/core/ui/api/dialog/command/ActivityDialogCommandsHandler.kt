package com.ruslan.hlushan.core.ui.api.dialog.command

import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.view.activity.BaseActivity

/**
 * @author Ruslan Hlushan on 2019-09-05
 */

@UiMainThread
class ActivityDialogCommandsHandler(private val activity: BaseActivity) : DialogCommandsHandler() {

    override fun canExecuteCommands(): Boolean =
        (!activity.instanceStateSaved)

    override fun executeCommand(command: DialogCommand) =
        command.execute(activity.supportFragmentManager)
}