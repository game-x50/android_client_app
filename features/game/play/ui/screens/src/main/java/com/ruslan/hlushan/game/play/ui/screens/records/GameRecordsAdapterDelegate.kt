package com.ruslan.hlushan.game.play.ui.screens.records

import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.ruslan.hlushan.android.extensions.setThrottledOnClickListener
import com.ruslan.hlushan.core.extensions.ifNotNull
import com.ruslan.hlushan.core.recycler.item.RecyclerItem
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.recycler.adapter.AdapterDelegate
import com.ruslan.hlushan.core.ui.recycler.adapter.BaseItemViewHolder
import com.ruslan.hlushan.core.ui.recycler.adapter.OnItemClickListener
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.play.ui.screens.R
import com.ruslan.hlushan.game.play.ui.screens.databinding.GamePlayUiGameRecordItemBinding
import com.ruslan.hlushan.third_party.androidx.recyclerview.extensions.getAllViewHolders
import com.ruslan.hlushan.third_party.rxjava2.extensions.isActive
import com.ruslan.hlushan.third_party.rxjava2.extensions.safetySubscribe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.threeten.bp.ZoneId

internal class GameRecordsAdapterDelegate(
        private val onItemClick: OnItemClickListener<GameRecordWithSyncState>,
        private val onDeleteClick: OnItemClickListener<GameRecordWithSyncState>,
        private val observeUpdates: (GameRecordRecyclerItem) -> Observable<GameRecordRecyclerItem>
) : AdapterDelegate<Long, GameRecordRecyclerItem, GameRecordRecyclerItem> {

    @get:LayoutRes
    override val layoutResId: Int
        get() = R.layout.game_play_ui_game_record_item

    override fun createViewHolder(itemView: View): BaseItemViewHolder<Long, GameRecordRecyclerItem> =
            GameRecordViewHolder(itemView, onItemClick, onDeleteClick, observeUpdates)

    companion object {

        @UiMainThread
        fun unsubscribeAll(recyclerView: RecyclerView) =
                recyclerView.getAllViewHolders()
                        .mapNotNull { viewHolder -> viewHolder as? GameRecordViewHolder }
                        .forEach { viewHolder -> viewHolder.clearUpdatesDisposable() }

        @UiMainThread
        fun subscribeAllActive(recyclerView: RecyclerView) =
                recyclerView.getAllViewHolders()
                        .mapNotNull { viewHolder -> viewHolder as? GameRecordViewHolder }
                        .forEach { viewHolder -> viewHolder.subscribeIfNeeded() }
    }
}

private class GameRecordViewHolder(
        itemView: View,
        private val onItemClick: OnItemClickListener<GameRecordWithSyncState>,
        private val onDeleteClick: OnItemClickListener<GameRecordWithSyncState>,
        private val observeUpdates: (GameRecordRecyclerItem) -> Observable<GameRecordRecyclerItem>
) : BaseItemViewHolder<Long, GameRecordRecyclerItem>(itemView) {

    @UiMainThread
    private var updatesDisposable: Disposable? = null

    private val binding = GamePlayUiGameRecordItemBinding.bind(itemView)

    @UiMainThread
    override fun onViewAttachedToWindow() {
        super.onViewAttachedToWindow()
        itemView.setThrottledOnClickListener {
            ifNotNull(recyclerItem) { item ->
                onItemClick(item.gameRecord)
            }
        }
        binding.gameRecordItemDeleteBtn.setThrottledOnClickListener {
            ifNotNull(recyclerItem) { item ->
                onDeleteClick(item.gameRecord)
            }
        }
    }

    @UiMainThread
    override fun onViewDetachedFromWindow() {
        clearUpdatesDisposable()
        itemView.setOnClickListener(null)
        binding.gameRecordItemDeleteBtn.setOnClickListener(null)
        super.onViewDetachedFromWindow()
    }

    @UiMainThread
    override fun onBindView(item: GameRecordRecyclerItem) {
        super.onBindView(item)
        listenForUpdates(item)
    }

    @UiMainThread
    override fun onViewRecycled() {
        super.onViewRecycled()
        clearUpdatesDisposable()
    }

    @UiMainThread
    private fun redraw(item: GameRecordRecyclerItem) {
        val zonedDateTime = item.gameRecord.syncState.lastLocalModifiedTimestamp.value.atZone(ZoneId.systemDefault())
        @Suppress("MaxLineLength", "SetTextI18n")
        binding.gameRecordItemTitle.text = (item.gameRecord.record.gameState.current.immutableNumbersMatrix.totalSum.toString()
                                            + "\n" + item.gameRecord.syncState.syncStatus
                                            + "\n" + zonedDateTime)
    }

    @UiMainThread
    private fun listenForUpdates(item: GameRecordRecyclerItem) {
        clearUpdatesDisposable()
        updatesDisposable = observeUpdates(item)
                .startWith(item)
                .distinctUntilChanged()
                .doOnNext { updated -> recyclerItem = updated }
                .distinctUntilChanged { first, second -> first.hasTheSameContent(second) }
                .doOnNext { updatedForUi -> redraw(updatedForUi) }
                .safetySubscribe()
    }

    @UiMainThread
    fun clearUpdatesDisposable() {
        updatesDisposable?.dispose()
        updatesDisposable = null
    }

    @UiMainThread
    fun subscribeIfNeeded() {
        val localItem = recyclerItem
        if (localItem != null && !updatesDisposable.isActive) {
            listenForUpdates(localItem)
        }
    }
}

internal data class GameRecordRecyclerItem(
        val gameRecord: GameRecordWithSyncState
) : RecyclerItem<Long> {

    override val id: Long get() = gameRecord.record.id

    override fun hasTheSameContent(other: RecyclerItem<*>): Boolean =
            (other is GameRecordRecyclerItem
             && this.gameRecord == other.gameRecord)
}