package com.ruslan.hlushan.core.ui.dialog

import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.command.DialogCommandsHandler
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver

@UiMainThread
class DialogCommandsHandlerLifecyclePluginObserver(
        private val dialogCommandsHandler: DialogCommandsHandler
) : LifecyclePluginObserver {

    override fun onAfterSuperResume() =
            dialogCommandsHandler.handlePendingCommands()
}