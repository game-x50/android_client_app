package com.ruslan.hlushan.core.ui.dialog.command

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread

abstract class DialogCommand {

    abstract val tag: String

    @UiMainThread
    abstract fun execute(fragmentManager: FragmentManager)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        @SuppressWarnings("UnsafeCast")
        val otherHandleDialogCommand = (other as DialogCommand)

        return (this.tag == otherHandleDialogCommand.tag)
    }

    override fun hashCode(): Int = tag.hashCode()
}

@UiMainThread
fun Fragment.executeOnParentFragment(command: DialogCommand) =
        executeOnParent(
                command = command,
                parentGet = { this.parentFragment },
                parentFragmentManagerGet = Fragment::getChildFragmentManager
        )

@UiMainThread
fun Fragment.executeOnParentActivity(command: DialogCommand) =
        executeOnParent(
                command = command,
                parentGet = { this.activity },
                parentFragmentManagerGet = FragmentActivity::getSupportFragmentManager
        )

@UiMainThread
private inline fun <P> executeOnParent(
        command: DialogCommand,
        parentGet: () -> P?,
        parentFragmentManagerGet: P.() -> FragmentManager
) {
    val parent: P? = parentGet()
    val parentFragmentManager: FragmentManager? = parent?.parentFragmentManagerGet()

    when {
        (parent is DialogCommandsHandler.Owner) -> {
            parent.dialogCommandsHandler.executeShowOrAddToQueue(command)
        }
        (parentFragmentManager != null)         -> {
            command.execute(parentFragmentManager)
        }
    }
}