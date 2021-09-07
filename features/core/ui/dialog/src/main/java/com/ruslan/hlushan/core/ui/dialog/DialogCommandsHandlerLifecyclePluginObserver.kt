package com.ruslan.hlushan.core.ui.dialog

import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.lifecycle.LifecyclePluginObserver
import com.ruslan.hlushan.core.ui.dialog.command.DialogCommandsHandler

@UiMainThread
class DialogCommandsHandlerLifecyclePluginObserver(
        private val dialogCommandsHandler: DialogCommandsHandler
) : LifecyclePluginObserver {

    override fun onAfterSuperResume() =
            dialogCommandsHandler.handlePendingCommands()
}