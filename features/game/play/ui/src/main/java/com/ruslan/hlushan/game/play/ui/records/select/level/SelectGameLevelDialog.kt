package com.ruslan.hlushan.game.play.ui.records.select.level

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.BaseDialogFragment
import com.ruslan.hlushan.core.ui.dialog.DialogBackgroundColorLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.dialog.DialogSizeRatioLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.dialog.command.DialogCommandsHandler
import com.ruslan.hlushan.core.ui.dialog.command.ShowDialogCommand
import com.ruslan.hlushan.core.ui.api.recycler.DelegatesRecyclerAdapter
import com.ruslan.hlushan.core.ui.api.recycler.OnItemClickListener
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerViewLifecyclePluginObserver
import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.play.ui.R
import com.ruslan.hlushan.game.play.ui.databinding.GamePlayUiSelectGameLevelDialogBinding

private const val DIALOG_WIDTH_RATIO = 0.4

internal class SelectGameLevelDialog : com.ruslan.hlushan.core.ui.dialog.BaseDialogFragment() {

    @get:LayoutRes
    override val layoutResId: Int
        get() = R.layout.game_play_ui_select_game_level_dialog

    private val parentGameLevelSelectedListener: OnGameLevelSelectedListener?
        get() = ((parentFragment as? OnGameLevelSelectedListener)
                 ?: (activity as? OnGameLevelSelectedListener))

    private val binding by bindViewBinding(GamePlayUiSelectGameLevelDialogBinding::bind)

    private val recyclerItemClickListener: OnItemClickListener<GameSize> = { gameSize ->
        parentGameLevelSelectedListener?.onGameLevelSelected(gameSize)
        dismissNowSafety()
    }

    @UiMainThread
    override fun initLifecyclePluginObservers() {
        super.initLifecyclePluginObservers()
        addLifecyclePluginObserver(com.ruslan.hlushan.core.ui.dialog.DialogBackgroundColorLifecyclePluginObserver(
                owner = this,
                color = Color.TRANSPARENT
        ))
        addLifecyclePluginObserver(com.ruslan.hlushan.core.ui.dialog.DialogSizeRatioLifecyclePluginObserver(
                owner = this,
                widthRatio = DIALOG_WIDTH_RATIO
        ))
        addLifecyclePluginObserver(RecyclerViewLifecyclePluginObserver { binding?.selectGameLevelDialogRecycler })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    @UiMainThread
    private fun setUpViews() {
        binding?.selectGameLevelDialogRecycler?.setUpDefaults(
                DelegatesRecyclerAdapter(
                        GameLevelAdapterDelegate(recyclerItemClickListener),
                        initItems = GameLevelAdapterDelegate.initItems()
                )
        )
    }

    interface OnGameLevelSelectedListener {
        @UiMainThread
        fun onGameLevelSelected(gameSize: GameSize)
    }
}

@UiMainThread
internal fun <Parent> Parent.showSelectGameLevelDialog()
        where Parent : com.ruslan.hlushan.core.ui.dialog.command.DialogCommandsHandler.Owner, Parent : SelectGameLevelDialog.OnGameLevelSelectedListener =
        this.dialogCommandsHandler.executeShowOrAddToQueue(ShowSelectGameLevelDialogCommand())

private class ShowSelectGameLevelDialogCommand : com.ruslan.hlushan.core.ui.dialog.command.ShowDialogCommand() {

    override val tag: String get() = "TAG_SELECT_GAME_LEVEL_DIALOG"

    @UiMainThread
    override fun getOrCreate(fragmentManager: FragmentManager): DialogFragment =
            ((fragmentManager.findFragmentByTag(tag) as? SelectGameLevelDialog)
             ?: SelectGameLevelDialog())
}