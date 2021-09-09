package com.ruslan.hlushan.game.play.ui.screens.records.select.order

import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.ruslan.hlushan.android.extensions.colorAttributeValue
import com.ruslan.hlushan.android.extensions.setThrottledOnClickListener
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.recycler.adapter.AdapterDelegate
import com.ruslan.hlushan.core.ui.recycler.adapter.BaseItemViewHolder
import com.ruslan.hlushan.core.ui.recycler.adapter.OnItemClickListener
import com.ruslan.hlushan.core.ui.recycler.item.RecyclerItem
import com.ruslan.hlushan.extensions.ifNotNull
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.play.ui.screens.R
import com.ruslan.hlushan.game.play.ui.screens.databinding.GamePlayUiGameRecordsOrderVariantItemBinding

internal class GameRecordsOrderVariantAdapterDelegate(
        private val onItemClick: OnItemClickListener<GameRecordsOrderVariantRecyclerItem>
) : AdapterDelegate<
        GameRecordWithSyncState.Order.Variant,
        GameRecordsOrderVariantRecyclerItem,
        GameRecordsOrderVariantRecyclerItem
        > {

    @get:LayoutRes
    override val layoutResId: Int
        get() = R.layout.game_play_ui_game_records_order_variant_item

    override fun createViewHolder(
            itemView: View
    ): BaseItemViewHolder<GameRecordWithSyncState.Order.Variant, GameRecordsOrderVariantRecyclerItem> =
            GameRecordsOrderVariantViewHolder(itemView, onItemClick)

    companion object {
        fun createRecyclerItems(
                selectedVariant: GameRecordWithSyncState.Order.Variant
        ): List<GameRecordsOrderVariantRecyclerItem> =
                GameRecordWithSyncState.Order.Variant.values()
                        .map { variant ->
                            GameRecordsOrderVariantRecyclerItem(variant, selected = (variant == selectedVariant))
                        }
    }
}

private class GameRecordsOrderVariantViewHolder(
        itemView: View,
        private val onItemClick: OnItemClickListener<GameRecordsOrderVariantRecyclerItem>
) : BaseItemViewHolder<GameRecordWithSyncState.Order.Variant, GameRecordsOrderVariantRecyclerItem>(itemView) {

    private val binding = GamePlayUiGameRecordsOrderVariantItemBinding.bind(itemView)

    @UiMainThread
    override fun onViewAttachedToWindow() {
        super.onViewAttachedToWindow()
        itemView.setThrottledOnClickListener {
            ifNotNull(recyclerItem) { item ->
                onItemClick(item)
            }
        }
    }

    @UiMainThread
    override fun onViewDetachedFromWindow() {
        super.onViewDetachedFromWindow()
        itemView.setOnClickListener(null)
    }

    @UiMainThread
    override fun onBindView(item: GameRecordsOrderVariantRecyclerItem) {
        super.onBindView(item)
        @StringRes val textResId: Int = item.orderVariant.nameResId
        binding.gameRecordsOrderVariantItemTitle.setText(textResId)
        @AttrRes val backgroundColorAttrResId = if (item.selected) {
            com.google.android.material.R.attr.colorSecondary
        } else {
            com.google.android.material.R.attr.colorPrimary
        }
        binding.gameRecordsOrderVariantItemTitle.setBackgroundColor(
                binding.gameRecordsOrderVariantItemTitle.context.colorAttributeValue(backgroundColorAttrResId)
        )
    }
}

internal class GameRecordsOrderVariantRecyclerItem(
        val orderVariant: GameRecordWithSyncState.Order.Variant,
        val selected: Boolean
) : RecyclerItem<GameRecordWithSyncState.Order.Variant> {

    override val id: GameRecordWithSyncState.Order.Variant get() = orderVariant

    override fun hasTheSameContent(other: RecyclerItem<*>): Boolean =
            (other is GameRecordsOrderVariantRecyclerItem
             && this.orderVariant == other.orderVariant
             && this.selected == other.selected)
}

@Suppress("MaxLineLength")
@get:StringRes
private val GameRecordWithSyncState.Order.Variant.nameResId: Int
    get() = when (this) {
        GameRecordWithSyncState.Order.Variant.TOTAL_SUM               -> R.string.game_play_ui_game_records_order_variant_by_total_sum
        GameRecordWithSyncState.Order.Variant.LAST_MODIFIED_TIMESTAMP -> R.string.game_play_ui_game_records_order_variant_by_last_modified
    }