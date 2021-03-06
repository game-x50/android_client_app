package com.ruslan.hlushan.core.ui.recycler.adapter

import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.ruslan.hlushan.core.recycler.item.RecyclerItem
import com.ruslan.hlushan.core.thread.UiMainThread

@Suppress("MaxLineLength")
abstract class BaseItemViewHolder<out Id : Any, RI : RecyclerItem<Id>>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @UiMainThread
    protected var recyclerItem: RI? = null

    @UiMainThread
    @CallSuper
    open fun onViewAttachedToWindow() = Unit

    @UiMainThread
    @CallSuper
    open fun onViewDetachedFromWindow() = Unit

    @UiMainThread
    @CallSuper
    open fun onViewRecycled() {
        recyclerItem = null
    }

    @UiMainThread
    @CallSuper
    open fun onBindView(item: RI) {
        recyclerItem = item
    }
}