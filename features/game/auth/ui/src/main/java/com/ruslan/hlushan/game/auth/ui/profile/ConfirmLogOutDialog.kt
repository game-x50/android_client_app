package com.ruslan.hlushan.game.auth.ui.profile

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.android.extensions.getIntOrNull
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.dialog.BaseTwoOptionsAlert
import com.ruslan.hlushan.core.ui.api.dialog.TwoOptionsAlertData
import com.ruslan.hlushan.core.ui.api.dialog.command.DialogCommandsHandler
import com.ruslan.hlushan.core.ui.api.dialog.command.ShowDialogCommand
import com.ruslan.hlushan.game.auth.ui.R

private const val KEY_COUNT_NOT_SYNCHED_RECORDS = "KEY_COUNT_NOT_SYNCHED_RECORDS"

internal class ConfirmLogOutDialog : BaseTwoOptionsAlert() {

    private val parentLogOutConfirmedListener: LogOutConfirmedListener?
        get() = ((parentFragment as? LogOutConfirmedListener)
                 ?: (activity as? LogOutConfirmedListener))

    override val positiveOnClickListener: () -> Unit = {
        parentLogOutConfirmedListener?.oLogOutConfirmed()
    }

    override val negativeOnClickListener: (() -> Unit)? get() = null

    override fun extractData(): TwoOptionsAlertData? =
            arguments?.getIntOrNull(KEY_COUNT_NOT_SYNCHED_RECORDS)?.let { countNotSynchedRecords ->
                val message: String = if (countNotSynchedRecords > 0) {
                    getString(
                            R.string.game_auth_ui_confirm_logout_with_not_synched_records_message_template,
                            countNotSynchedRecords
                    )
                } else {
                    getString(R.string.game_auth_ui_confirm_logout_message_question)
                }

                TwoOptionsAlertData(
                        title = getString(R.string.game_auth_ui_confirm_logout_title),
                        message = message,
                        positiveButtonText = getString(android.R.string.yes),
                        negativeButtonText = getString(android.R.string.no)
                )
            }

    interface LogOutConfirmedListener {
        @UiMainThread
        fun oLogOutConfirmed()
    }
}

@UiMainThread
internal fun <Parent> Parent.showConfirmLogOutDialog(
        countNotSynchedRecords: Int
) where Parent : DialogCommandsHandler.Owner, Parent : ConfirmLogOutDialog.LogOutConfirmedListener =
        this.dialogCommandsHandler.executeShowOrAddToQueue(ShowConfirmLogOutDialogCommand(countNotSynchedRecords))

private class ShowConfirmLogOutDialogCommand(
        private val countNotSynchedRecords: Int
) : ShowDialogCommand() {

    override val tag: String get() = "TAG_CONFIRM_LOG_OUT_DIALOG"

    @UiMainThread
    override fun getOrCreate(fragmentManager: FragmentManager): DialogFragment =
            ((fragmentManager.findFragmentByTag(tag) as? ConfirmLogOutDialog)
             ?: createConfirmLogOutDialog(countNotSynchedRecords))
}

private fun createConfirmLogOutDialog(countNotSynchedRecords: Int): ConfirmLogOutDialog =
        ConfirmLogOutDialog().apply {
            val args = Bundle(1)
            args.putInt(KEY_COUNT_NOT_SYNCHED_RECORDS, countNotSynchedRecords)
            arguments = args
        }