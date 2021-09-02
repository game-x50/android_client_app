package com.ruslan.hlushan.game.play.ui.records.select.level

import android.view.View
import androidx.annotation.LayoutRes
import com.ruslan.hlushan.android.extensions.setThrottledOnClickListener
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.recycler.AdapterDelegate
import com.ruslan.hlushan.core.ui.api.recycler.BaseItemViewHolder
import com.ruslan.hlushan.core.ui.api.recycler.OnItemClickListener
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem
import com.ruslan.hlushan.extensions.ifNotNull
import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.play.ui.R
import com.ruslan.hlushan.game.play.ui.databinding.GamePlayUiGameLevelItemBinding

internal class GameLevelAdapterDelegate(
        private val onItemClick: OnItemClickListener<GameSize>
) : AdapterDelegate<GameSize, GameLevelRecyclerItem, GameLevelRecyclerItem> {

    @get:LayoutRes
    override val layoutResId: Int
        get() = R.layout.game_play_ui_game_level_item

    override fun createViewHolder(itemView: View): BaseItemViewHolder<GameSize, GameLevelRecyclerItem> =
            GameLevelViewHolder(itemView, onItemClick)

    companion object {
        fun initItems(): List<GameLevelRecyclerItem> = GameSize.values()
                .map(::GameLevelRecyclerItem)
    }
}

private class GameLevelViewHolder(
        itemView: View,
        private val onItemClick: OnItemClickListener<GameSize>
) : BaseItemViewHolder<GameSize, GameLevelRecyclerItem>(itemView) {

    private val binding = GamePlayUiGameLevelItemBinding.bind(itemView)

    @UiMainThread
    override fun onViewAttachedToWindow() {
        super.onViewAttachedToWindow()
        itemView.setThrottledOnClickListener {
            ifNotNull(recyclerItem) { item ->
                onItemClick(item.gameSize)
            }
        }
    }

    @UiMainThread
    override fun onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow()
        itemView.setOnClickListener(null)
    }

    @UiMainThread
    override fun onBindView(item: GameLevelRecyclerItem) {
        super.onBindView(item)
        binding.gameLevelItemTitle.text = item.gameSize.toString()
    }
}

internal class GameLevelRecyclerItem(val gameSize: GameSize) : RecyclerItem<GameSize> {

    override val id: GameSize get() = gameSize
}