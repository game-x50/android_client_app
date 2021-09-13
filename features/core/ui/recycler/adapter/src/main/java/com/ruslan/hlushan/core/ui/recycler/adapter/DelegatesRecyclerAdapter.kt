package com.ruslan.hlushan.core.ui.recycler.adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.extensions.ifNotNull
import com.ruslan.hlushan.core.recycler.item.RecyclerItem

typealias OnItemClickListener<T> = (item: T) -> Unit

typealias OnItemWithPositionClickListener<T> = (item: T, position: Int) -> Unit

class DelegatesRecyclerAdapter<
        out Id : Any,
        RecyclerItemGeneral : RecyclerItem<Id>
        >(
        vararg delegates: AdapterDelegate<Id, RecyclerItemGeneral, out RecyclerItemGeneral>,
        initItems: List<RecyclerItemGeneral> = emptyList()
) : ListAdapter<RecyclerItemGeneral, RecyclerView.ViewHolder>(DelegatesDiffHelperItemCallback()) {

    private val delegatesMap: SparseArray<AdapterDelegate<Id, RecyclerItemGeneral, out RecyclerItemGeneral>> =
            SparseArray(delegates.size)

    private var inflater: LayoutInflater? = null
    private val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int = getItem(position).gridSpan
    }

    init {
        delegates.forEachIndexed { index, adapterDelegate ->
            delegatesMap.put(index, adapterDelegate)
        }

        this.submitList(initItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        val delegate = delegatesMap[type]
        return delegate.createViewHolder(inflater!!.inflate(delegate.layoutResId, parent, false))
    }

    @SuppressWarnings("UnsafeCast")
    @UiMainThread
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) =
            (viewHolder as BaseItemViewHolder<Id, RecyclerItemGeneral>).onBindView(getItem(position))

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        delegatesMap.forEach { key, delegate ->
            if (delegate.isForViewType(item)) {
                return key
            }
        }

        throw IllegalStateException("No delegate for this type")
    }

    @UiMainThread
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        ifNotNull(recyclerView.layoutManager) { layoutManager ->
            setManager(layoutManager)
        }

        if (inflater == null) {
            this.inflater = LayoutInflater.from(recyclerView.context)
        }
    }

    @SuppressWarnings("ExpressionBodySyntax")
    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        //TODO: log this or maybe crash in debug
        return super.onFailedToRecycleView(holder)
    }

    @UiMainThread
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        @Suppress("UnsafeCast")
        (holder as BaseItemViewHolder<*, *>).onViewAttachedToWindow()
    }

    @UiMainThread
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        @Suppress("UnsafeCast")
        (holder as BaseItemViewHolder<*, *>).onViewDetachedFromWindow()
    }

    @UiMainThread
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        @Suppress("UnsafeCast")
        (holder as BaseItemViewHolder<*, *>).onViewRecycled()
    }

    @UiMainThread
    private fun setManager(manager: RecyclerView.LayoutManager) {
        when (manager) {
            is GridLayoutManager          -> {
                manager.spanSizeLookup = spanSizeLookup
            }
            is StaggeredGridLayoutManager -> {
                manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            }
        }
    }
}

@Suppress("MaxLineLength")
private class DelegatesDiffHelperItemCallback<out Id : Any, RI : RecyclerItem<Id>> : DiffUtil.ItemCallback<RI>() {

    private val payload = Any()

    //to avoid blinking
    override fun getChangePayload(oldItem: RI, newItem: RI): Any = payload

    override fun areItemsTheSame(oldItem: RI, newItem: RI): Boolean =
            oldItem.isTheSameItem(newItem)

    override fun areContentsTheSame(oldItem: RI, newItem: RI): Boolean =
            oldItem.hasTheSameContent(newItem)
}