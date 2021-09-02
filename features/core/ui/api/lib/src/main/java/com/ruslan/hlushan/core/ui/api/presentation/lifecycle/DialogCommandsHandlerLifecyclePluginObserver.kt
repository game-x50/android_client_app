package com.ruslan.hlushan.core.ui.api.presentation.lifecycle

import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.dialog.command.DialogCommandsHandler

@UiMainThread
internal class DialogCommandsHandlerLifecyclePluginObserver(
        private val dialogCommandsHandler: DialogCommandsHandler
) : LifecyclePluginObserver {

    override fun onAfterSuperResume() =
            dialogCommandsHandler.handlePendingCommands()
}