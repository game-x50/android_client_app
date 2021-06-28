package com.ruslan.hlushan.core.ui.api.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.android.extensions.showNowSafety
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.R
import com.ruslan.hlushan.core.ui.api.dialog.command.DialogCommand
import com.ruslan.hlushan.core.ui.api.dialog.command.DialogCommandsHandler

private const val ARG_STYLE_RES_ID = "ARG_STYLE_RES_ID"
private const val ARG_TITLE = "ARG_TITLE"
private const val ARG_MESSAGE = "ARG_MESSAGE"
private const val ARG_BUTTON_TEXT = "ARG_BUTTON_TEXT"
private const val TAG_MESSAGE_DIALOG = "TAG_MESSAGE_DIALOG"

internal class MessageDialog : BaseDialogFragment() {

    @get:LayoutRes
    override val layoutResId: Int?
        get() = null

    @StyleRes private var styleResId: Int = R.style.AlertDialogStyle
    private var title: String = ""
    private var message: String = ""
    private var buttonText = ""

    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        styleResId = arguments?.getInt(ARG_STYLE_RES_ID) ?: R.style.AlertDialogStyle
        title = arguments?.getString(ARG_TITLE) ?: ""
        message = arguments?.getString(ARG_MESSAGE) ?: ""
        buttonText = arguments?.getString(ARG_BUTTON_TEXT) ?: ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        @SuppressWarnings("UnsafeCallOnNullableType")
        val alert = AlertDialog.Builder(requireContext(), styleResId)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(buttonText, null)
                .create()

        alertDialog = alert
        return alert
    }

    private fun updateViews(title: String, message: String, buttonText: String) {
        arguments?.putString(ARG_TITLE, title)
        alertDialog?.setTitle(title)

        arguments?.putString(ARG_MESSAGE, message)
        alertDialog?.setMessage(message)

        arguments?.putString(ARG_BUTTON_TEXT, buttonText)
        alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.text = buttonText
    }

    class ShowCommand(
            @StyleRes private val styleResId: Int,
            private val title: String,
            private val message: String,
            private val buttonText: String
    ) : DialogCommand() {

        override val tag: String get() = TAG_MESSAGE_DIALOG

        @UiMainThread
        override fun execute(fragmentManager: FragmentManager) {
            val messageDialog = find(fragmentManager)

            return if (messageDialog != null && messageDialog.styleResId == styleResId) {
                messageDialog.updateViews(title, message, buttonText)
            } else {
                createMessageDialog(
                        styleResId = styleResId,
                        title = title,
                        message = message,
                        buttonText = buttonText
                ).showNowSafety(fragmentManager, tag)
            }
        }

        private fun find(fragmentManager: FragmentManager): MessageDialog? =
                (fragmentManager.findFragmentByTag(tag) as? MessageDialog)
    }
}

private fun createMessageDialog(
        @StyleRes styleResId: Int,
        title: String,
        message: String,
        buttonText: String
): MessageDialog =
        MessageDialog().apply {
            @SuppressWarnings("MagicNumber")
            val args = Bundle(4)

            args.putInt(ARG_STYLE_RES_ID, styleResId)
            args.putString(ARG_TITLE, title)
            args.putString(ARG_MESSAGE, message)
            args.putString(ARG_BUTTON_TEXT, buttonText)

            arguments = args
        }

@UiMainThread
fun DialogCommandsHandler.Owner.showDialogMessage(
        @StyleRes styleResId: Int = R.style.AlertDialogStyle,
        title: String,
        message: String,
        buttonText: String
) = this.dialogCommandsHandler.executeShowOrAddToQueue(
        MessageDialog.ShowCommand(styleResId = styleResId, title = title, message = message, buttonText = buttonText)
)

@UiMainThread
fun DialogCommandsHandler.Owner.hideDialogMessage() =
        this.dialogCommandsHandler.removeFromQueueOrExecuteClose(TAG_MESSAGE_DIALOG)