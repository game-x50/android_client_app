package com.ruslan.hlushan.game.play.ui.game

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.android.extensions.getIntOrNull
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.dialog.BaseTwoOptionsAlert
import com.ruslan.hlushan.core.ui.api.dialog.TwoOptionsAlertData
import com.ruslan.hlushan.core.ui.api.dialog.command.DialogCommandsHandler
import com.ruslan.hlushan.core.ui.api.dialog.command.ShowDialogCommand
import com.ruslan.hlushan.game.play.ui.R

private const val KEY_DEFAULT_TOTAL_SCORE = "KEY_DEFAULT_TOTAL_SCORE"

internal class SaveResultQuestionDialog : BaseTwoOptionsAlert() {

    private val parentAnswerSaveResultQuestionListener: AnswerSaveResultQuestionListener?
        get() = ((parentFragment as? AnswerSaveResultQuestionListener)
                 ?: (activity as? AnswerSaveResultQuestionListener))

    override val positiveOnClickListener: () -> Unit = {
        parentAnswerSaveResultQuestionListener?.onAnswerSaveResultQuestion(save = true)
    }

    override val negativeOnClickListener: () -> Unit = {
        parentAnswerSaveResultQuestionListener?.onAnswerSaveResultQuestion(save = false)
    }

    override fun extractData(): TwoOptionsAlertData? =
            arguments?.getIntOrNull(KEY_DEFAULT_TOTAL_SCORE)?.let { totalScore ->
                TwoOptionsAlertData(
                        title = getString(R.string.game_play_ui_save_result_question_template, totalScore),
                        message = getString(R.string.game_play_ui_result_can_be_lose),
                        positiveButtonText = getString(android.R.string.yes), //todo
                        negativeButtonText = getString(android.R.string.no)//todo
                )
            }

    interface AnswerSaveResultQuestionListener {
        @UiMainThread
        fun onAnswerSaveResultQuestion(save: Boolean)
    }
}

@UiMainThread
internal fun <Parent> Parent.showSaveResultQuestionDialog(
        totalScore: Int
) where Parent : DialogCommandsHandler.Owner, Parent : SaveResultQuestionDialog.AnswerSaveResultQuestionListener =
        this.dialogCommandsHandler.executeShowOrAddToQueue(ShowSaveResultQuestionDialogCommand(totalScore))

private class ShowSaveResultQuestionDialogCommand(
        private val totalScore: Int
) : ShowDialogCommand() {

    override val tag: String get() = "TAG_SAVE_RESULT_QUESTION_DIALOG"

    @UiMainThread
    override fun getOrCreate(fragmentManager: FragmentManager): DialogFragment =
            ((fragmentManager.findFragmentByTag(tag) as? SaveResultQuestionDialog)
             ?: createSaveResultQuestionDialog(totalScore))
}

private fun createSaveResultQuestionDialog(totalScore: Int): SaveResultQuestionDialog =
        SaveResultQuestionDialog().apply {
            val args = Bundle(1)
            args.putInt(KEY_DEFAULT_TOTAL_SCORE, totalScore)
            arguments = args
        }