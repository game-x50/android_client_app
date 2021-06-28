package com.ruslan.hlushan.core.ui.api.recycler

import android.view.View
import androidx.annotation.LayoutRes

@Suppress("MaxLineLength")
interface AdapterDelegate<out Id : Any, RecyclerItemGeneral : RecyclerItem<Id>, RecyclerItemDirect : RecyclerItemGeneral> {

    @get:LayoutRes
    val layoutResId: Int

    fun isForViewType(item: RecyclerItemGeneral): Boolean = true

    fun createViewHolder(itemView: View): BaseItemViewHolder<Id, RecyclerItemDirect>
}