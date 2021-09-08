package com.ruslan.hlushan.game.play.ui.records

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.BaseTwoOptionsAlert
import com.ruslan.hlushan.core.ui.dialog.TwoOptionsAlertData
import com.ruslan.hlushan.core.ui.dialog.command.DialogCommandsHandler
import com.ruslan.hlushan.core.ui.dialog.command.ShowDialogCommand
import com.ruslan.hlushan.extensions.ifNotNull
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.play.ui.R
import com.ruslan.hlushan.game.play.ui.dto.GameRecordParcelable
import com.ruslan.hlushan.game.play.ui.dto.toParcelable

private const val KEY_GAME_RECORD = "KEY_GAME_RECORD"

internal class ConfirmDeleteGameRecordDialog : BaseTwoOptionsAlert() {

    private val parentOnDeleteGameRecordConfirmedListener: OnDeleteGameRecordConfirmedListener?
        get() = ((parentFragment as? OnDeleteGameRecordConfirmedListener)
                 ?: (activity as? OnDeleteGameRecordConfirmedListener))

    override val positiveOnClickListener: () -> Unit = {
        ifNotNull(extractGameRecord()) { gameRecord ->
            parentOnDeleteGameRecordConfirmedListener?.onDeleteGameRecordConfirmed(gameRecord)
        }
    }

    override val negativeOnClickListener: (() -> Unit)? get() = null

    private fun extractGameRecord(): GameRecord? =
            arguments?.getParcelable<GameRecordParcelable>(KEY_GAME_RECORD)
                    ?.toOriginal()

    override fun extractData(): TwoOptionsAlertData? = extractGameRecord()?.let { gameRecord ->
        @SuppressWarnings("StringFormatMatches")
        val message = getString(
                R.string.game_play_ui_game_with_total_score_will_be_deleted_template,
                gameRecord.gameState.current.immutableNumbersMatrix.totalSum
        )
        TwoOptionsAlertData(
                title = getString(R.string.game_play_ui_delete_game_question),
                message = message,
                positiveButtonText = getString(android.R.string.yes),
                negativeButtonText = getString(android.R.string.no)
        )
    }

    interface OnDeleteGameRecordConfirmedListener {
        @UiMainThread
        fun onDeleteGameRecordConfirmed(gameRecord: GameRecord)
    }
}

@UiMainThread
internal fun <Parent> Parent.showConfirmDeleteGameRecordDialog(
        gameRecord: GameRecord
) where Parent : DialogCommandsHandler.Owner,
        Parent : ConfirmDeleteGameRecordDialog.OnDeleteGameRecordConfirmedListener =
        this.dialogCommandsHandler.executeShowOrAddToQueue(ShowConfirmDeleteGameRecordDialogCommand(gameRecord))

private class ShowConfirmDeleteGameRecordDialogCommand(
        private val gameRecord: GameRecord
) : ShowDialogCommand() {

    override val tag: String get() = "TAG_CONFIRM_DELETE_GAME_RECORD_DIALOG_${gameRecord.id}"

    @UiMainThread
    override fun getOrCreate(fragmentManager: FragmentManager): DialogFragment =
            ((fragmentManager.findFragmentByTag(tag) as? ConfirmDeleteGameRecordDialog)
             ?: createConfirmDeleteGameRecordDialog(gameRecord))
}

private fun createConfirmDeleteGameRecordDialog(gameRecord: GameRecord): ConfirmDeleteGameRecordDialog =
        ConfirmDeleteGameRecordDialog().apply {
            val args = Bundle(1)
            args.putParcelable(KEY_GAME_RECORD, gameRecord.toParcelable())
            arguments = args
        }