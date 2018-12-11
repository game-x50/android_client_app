package com.ruslan.hlushan.game.play.ui.game

import android.view.View
import androidx.annotation.LayoutRes
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.recycler.AdapterDelegate
import com.ruslan.hlushan.core.ui.api.recycler.BaseItemViewHolder
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem
import com.ruslan.hlushan.game.play.ui.R
import com.ruslan.hlushan.game.play.ui.databinding.GamePlayUiPlayNumbersItemBinding

internal class PlayNumbersAdapterDelegate : AdapterDelegate<String, PlayNumbersRecyclerItem, PlayNumbersRecyclerItem> {

    @SuppressWarnings("ClassOrdering")
    companion object {
        fun initItems(numbers: IntArray) = numbers
                .groupBy { n ->
                    @Suppress("MagicNumber")
                    (n / 10)
                }
                .map { (key, groupedNumbers) ->
                    PlayNumbersRecyclerItem(
                            numbersString = groupedNumbers.joinToString()
                    )
                }
    }

    @get:LayoutRes
    override val layoutResId: Int
        get() = R.layout.game_play_ui_play_numbers_item

    override fun createViewHolder(itemView: View): BaseItemViewHolder<String, PlayNumbersRecyclerItem> =
            PlayNumbersViewHolder(itemView)
}

private class PlayNumbersViewHolder(
        itemView: View
) : BaseItemViewHolder<String, PlayNumbersRecyclerItem>(itemView) {

    private val binding = GamePlayUiPlayNumbersItemBinding.bind(itemView)

    @UiMainThread
    override fun onBindView(item: PlayNumbersRecyclerItem) {
        super.onBindView(item)
        binding.playNumbersItemText.text = item.numbersString
    }
}

internal class PlayNumbersRecyclerItem(val numbersString: String) : RecyclerItem<String> {

    override val id: String get() = numbersString
}