package com.ruslan.hlushan.game.auth.ui.forgot.password

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.BaseDialogFragment
import com.ruslan.hlushan.core.ui.dialog.command.DialogCommandsHandler
import com.ruslan.hlushan.core.ui.dialog.command.ShowDialogCommand
import com.ruslan.hlushan.game.api.auth.dto.User
import com.ruslan.hlushan.game.auth.ui.R

private const val KEY_RESET_PASSWORD_EMAIL = "KEY_RESET_PASSWORD_EMAIL"

internal class ResetPasswordEmailSentDialog : BaseDialogFragment() {

    @get:LayoutRes
    override val layoutResId: Int?
        get() = null

    private val email: String get() = arguments?.getString(KEY_RESET_PASSWORD_EMAIL).orEmpty()

    private val parentCancelDialogListener: CancelDialogListener?
        get() = ((parentFragment as? CancelDialogListener)
                 ?: (activity as? CancelDialogListener))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            @Suppress("UnsafeCallOnNullableType")
            AlertDialog.Builder(requireContext())
                    .setTitle(R.string.game_auth_ui_reset_password_email_sent_dialog_title)
                    .setMessage(getString(R.string.game_auth_ui_reset_password_email_sent_dialog_message, email))
                    .setPositiveButton(R.string.game_auth_ui_reset_password_email_sent_dialog_button) { dialog, which ->
                        parentCancelDialogListener?.onCancel()
                    }
                    .setCancelable(false)
                    .create()

    interface CancelDialogListener {
        @UiMainThread
        fun onCancel()
    }
}

@UiMainThread
internal fun <Parent> Parent.showResetPasswordEmailSentDialog(
        email: User.Email
) where Parent : DialogCommandsHandler.Owner, Parent : ResetPasswordEmailSentDialog.CancelDialogListener =
        this.dialogCommandsHandler.executeShowOrAddToQueue(ShowResetPasswordEmailSentDialogCommand(email))

private class ShowResetPasswordEmailSentDialogCommand(
        private val email: User.Email
) : ShowDialogCommand() {

    override val tag: String get() = "TAG_RESET_PASSWORD_EMAIL_SENT_DIALOG"

    @UiMainThread
    override fun getOrCreate(fragmentManager: FragmentManager): DialogFragment =
            ((fragmentManager.findFragmentByTag(tag) as? ResetPasswordEmailSentDialog)
             ?: (createResetPasswordEmailSentDialog(email)))
}

private fun createResetPasswordEmailSentDialog(email: User.Email): ResetPasswordEmailSentDialog =
        ResetPasswordEmailSentDialog().apply {
            val args = Bundle(1)
            args.putString(KEY_RESET_PASSWORD_EMAIL, email.value)
            arguments = args
        }