package com.ruslan.hlushan.core.ui.activity

import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.command.DialogCommand
import com.ruslan.hlushan.core.ui.dialog.command.DialogCommandsHandler

@UiMainThread
class ActivityDialogCommandsHandler(private val activity: BaseActivity) : DialogCommandsHandler() {

    override fun canExecuteCommands(): Boolean =
        (!activity.instanceStateSaved)

    override fun executeCommand(command: DialogCommand) =
        command.execute(activity.supportFragmentManager)
}