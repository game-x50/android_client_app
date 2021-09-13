package com.ruslan.hlushan.game.play.ui.screens.game

import android.os.Bundle
import android.view.View
import com.ruslan.hlushan.android.extensions.getLongOrNull
import com.ruslan.hlushan.android.extensions.setThrottledOnClickListener
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.utils.BottomMenuHolder
import com.ruslan.hlushan.core.ui.dialog.showSimpleProgress
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.viewbinding.extensions.bindViewBinding
import com.ruslan.hlushan.core.ui.viewmodel.extensions.handleCommandQueue
import com.ruslan.hlushan.extensions.formatWithLeadingZerosString
import com.ruslan.hlushan.game.play.api.listeners.GameFinishedListener
import com.ruslan.hlushan.game.play.api.listeners.TotalSumChangedListener
import com.ruslan.hlushan.game.play.ui.screens.R
import com.ruslan.hlushan.game.play.ui.screens.databinding.GamePlayUiPlayScreenBinding
import com.ruslan.hlushan.third_party.androidx.insets.addSystemPadding
import com.ruslan.hlushan.third_party.three_ten.extensions.hoursInNonFullDay
import com.ruslan.hlushan.third_party.three_ten.extensions.minutesInNonFullHour
import com.ruslan.hlushan.third_party.three_ten.extensions.secondsInNonFullMinute
import org.threeten.bp.Duration

private const val KEY_PLAYED_SECONDS = "KEY_PLAYED_SECONDS"

internal abstract class PlayGameFragment<VM : PlayGameViewModel> : BaseFragment(
        layoutResId = R.layout.game_play_ui_play_screen
), SaveResultQuestionDialog.AnswerSaveResultQuestionListener {

    protected abstract val viewModel: VM

    protected val binding by bindViewBinding(GamePlayUiPlayScreenBinding::bind)

    private val totalSumChangedListener = TotalSumChangedListener { totalSum ->
        @SuppressWarnings("StringFormatMatches")
        binding?.playScreenTotalScoreText?.text = getString(R.string.game_play_ui_total_score_template, totalSum)
    }

    private val gameFinishedListener = GameFinishedListener { gameState ->
        viewModel.stopTimer()
        showSaveResultQuestionDialog(gameState.current.immutableNumbersMatrix.totalSum)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.addSystemPadding(top = true, bottom = true)

        binding?.playScreenGameView?.setTotalSumChangedListener(totalSumChangedListener)
        binding?.playScreenGameView?.setGameFinishedListener(gameFinishedListener)

        binding?.playScreenShowAvailableNumbersBtn?.setThrottledOnClickListener {
            showAvailableNumbers()
        }
        binding?.playScreenShowComboSumsBtn?.setThrottledOnClickListener {
            showComboSums()
        }

        this.handleCommandQueue(commandQueue = viewModel.commandsQueue, handler = this::handleCommand)
    }

    override fun onResume() {
        super.onResume()

        if (binding?.playScreenGameView?.isGameFinished == false) {
            viewModel.startTimer()
        }

        (activity as? BottomMenuHolder)?.showBottomMenu(false)
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopTimer()

        (activity as? BottomMenuHolder)?.showBottomMenu(true)
    }

    @UiMainThread
    override fun restoreFromSavedInstanceState(savedInstanceState: Bundle) {
        super.restoreFromSavedInstanceState(savedInstanceState)
        savedInstanceState.getLongOrNull(KEY_PLAYED_SECONDS)?.let { playedSeconds ->
            viewModel.restore(Duration.ofSeconds(playedSeconds))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_PLAYED_SECONDS, viewModel.playedDuration.seconds)
    }

    @UiMainThread
    override fun onBackPressed() {
        val result = binding?.playScreenGameView?.copyGameState()
        if (result != null) {
            viewModel.onPressedExitWithState(result)
        } else {
            super.onBackPressed()
        }
    }

    @UiMainThread
    override fun onAnswerSaveResultQuestion(save: Boolean) {
        val gameState = binding?.playScreenGameView?.copyGameState()
        if (gameState != null) {
            viewModel.exitAndSaveResultIfNeeded(gameState, save)
        }
    }

    @Suppress("MaxLineLength")
    @UiMainThread
    private fun handleCommand(command: PlayGameViewModel.Command) =
            when (command) {
                is PlayGameViewModel.Command.ShowSimpleProgress           -> showSimpleProgress(command.show)
                is PlayGameViewModel.Command.ShowPlayedTime               -> showPlayedTime(command.playDuration)
                is PlayGameViewModel.Command.ShowSaveResultQuestionDialog -> showSaveResultQuestionDialog(command.totalSum)
                is PlayGameViewModel.Command.ShowError                    -> showError(command.error)
            }

    @UiMainThread
    private fun showPlayedTime(playDuration: Duration) {
        binding?.playScreenPlayedTimeText?.text = playDuration.formatPlayedTime()
    }

    @UiMainThread
    private fun showAvailableNumbers() =
            dialogCommandsHandler.executeShowOrAddToQueue(ShowAvailableNumbersDialog.ShowCommand())

    @UiMainThread
    private fun showComboSums() =
            dialogCommandsHandler.executeShowOrAddToQueue(ShowComboSumsDialog.ShowCommand())
}

private fun Duration.formatPlayedTime(): String {
    val stringBuilder = StringBuilder()

    val days = this.toDays()
    if (days > 0) {
        stringBuilder.append(days)
        stringBuilder.append(" : ")
    }

    val hours = this.hoursInNonFullDay()
    if (days > 0 || hours > 0) {
        stringBuilder.append(hours.formatWithLeadingZerosString(minStringSize = 2))
        stringBuilder.append(" : ")
    }

    stringBuilder.append(this.minutesInNonFullHour().formatWithLeadingZerosString(minStringSize = 2))
    stringBuilder.append(" : ")
    stringBuilder.append(this.secondsInNonFullMinute().formatWithLeadingZerosString(minStringSize = 2))

    return stringBuilder.toString()
}