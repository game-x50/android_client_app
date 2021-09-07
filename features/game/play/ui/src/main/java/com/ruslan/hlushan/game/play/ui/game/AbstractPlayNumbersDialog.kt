package com.ruslan.hlushan.game.play.ui.game

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.BaseDialogFragment
import com.ruslan.hlushan.core.ui.dialog.DialogBackgroundColorLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.dialog.DialogSizeRatioLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.api.recycler.DelegatesRecyclerAdapter
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerViewLifecyclePluginObserver
import com.ruslan.hlushan.extensions.ifNotNull
import com.ruslan.hlushan.game.play.ui.R
import com.ruslan.hlushan.game.play.ui.databinding.GamePlayUiPlayNumbersDialogBinding
import com.ruslan.hlushan.third_party.androidx.recyclerview.extensions.setUpDefaults

private const val DIALOG_WIDTH_RATIO = 0.6
private const val DIALOG_HEIGHT_RATIO = 0.4

internal abstract class AbstractPlayNumbersDialog : com.ruslan.hlushan.core.ui.dialog.BaseDialogFragment() {

    @get:LayoutRes
    override val layoutResId: Int
        get() = R.layout.game_play_ui_play_numbers_dialog

    private val binding by bindViewBinding(GamePlayUiPlayNumbersDialogBinding::bind)

    protected abstract val numbers: IntArray

    @UiMainThread
    override fun initLifecyclePluginObservers() {
        super.initLifecyclePluginObservers()
        addLifecyclePluginObserver(com.ruslan.hlushan.core.ui.dialog.DialogBackgroundColorLifecyclePluginObserver(
                owner = this,
                color = Color.TRANSPARENT
        ))
        addLifecyclePluginObserver(com.ruslan.hlushan.core.ui.dialog.DialogSizeRatioLifecyclePluginObserver(
                owner = this,
                widthRatio = DIALOG_WIDTH_RATIO,
                heightRatio = DIALOG_HEIGHT_RATIO
        ))
        addLifecyclePluginObserver(RecyclerViewLifecyclePluginObserver { binding?.playNumbersDialogRecycler })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ifNotNull(binding?.playNumbersDialogRecycler) { notNullRecycler ->
            setUpRecycler(notNullRecycler)
        }
    }

    @UiMainThread
    private fun setUpRecycler(recycler: RecyclerView) {
        recycler.setUpDefaults(
                DelegatesRecyclerAdapter(
                        PlayNumbersAdapterDelegate(),
                        initItems = PlayNumbersAdapterDelegate.initItems(numbers = numbers)
                )
        )
    }
}