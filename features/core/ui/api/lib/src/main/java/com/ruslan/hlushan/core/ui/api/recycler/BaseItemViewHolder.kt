package com.ruslan.hlushan.core.ui.api.recycler

import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread

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