package com.ruslan.hlushan.game.top.ui.games

import android.view.View
import androidx.annotation.LayoutRes
import com.ruslan.hlushan.core.ui.recycler.adapter.AdapterDelegate
import com.ruslan.hlushan.core.ui.recycler.adapter.BaseItemViewHolder
import com.ruslan.hlushan.core.ui.recycler.item.RecyclerItem
import com.ruslan.hlushan.game.api.top.dto.GamePreviewWithUserDetails
import com.ruslan.hlushan.game.top.ui.R
import com.ruslan.hlushan.game.top.ui.databinding.GameTopUiTopGameItemBinding
import org.threeten.bp.ZoneId

internal class TopGamesAdapterDelegate : AdapterDelegate<String, TopGameRecyclerItem, TopGameRecyclerItem> {

    @get:LayoutRes
    override val layoutResId: Int
        get() = R.layout.game_top_ui_top_game_item

    override fun createViewHolder(itemView: View): BaseItemViewHolder<String, TopGameRecyclerItem> =
            TopGameViewHolder(itemView)
}

private class TopGameViewHolder(itemView: View) : BaseItemViewHolder<String, TopGameRecyclerItem>(itemView) {

    private val binding = GameTopUiTopGameItemBinding.bind(itemView)

    override fun onBindView(item: TopGameRecyclerItem) {
        super.onBindView(item)

        @Suppress("SetTextI18n", "MaxLineLength")
        binding.gameTopUiTopGameItemDescription.text =
                "user: ${item.previewWithUserDetails.userNickname}" +
                "\n" + "${item.previewWithUserDetails.gamePreview.totalPlayed.seconds} seconds" +
                "\n" + item.previewWithUserDetails.gamePreview.totalSum.toString() +
                "\n" + item.previewWithUserDetails.gamePreview.size.toString() +
                "\n" + item.previewWithUserDetails.gamePreview.lastLocalModifiedTimestamp.atZone(ZoneId.systemDefault()).toString() +
                "\n" +
                if (item.isMyOwn) {
                    "MY!!!!!"
                } else {
                    "NOT MY"
                }
    }
}

internal data class TopGameRecyclerItem(
        val isMyOwn: Boolean,
        val previewWithUserDetails: GamePreviewWithUserDetails
) : RecyclerItem<String> {

    override val id: String get() = previewWithUserDetails.gamePreview.id

    override fun hasTheSameContent(other: RecyclerItem<*>): Boolean =
            (other is TopGameRecyclerItem
             && (this.previewWithUserDetails == other.previewWithUserDetails)
             && (this.isMyOwn == other.isMyOwn))
}