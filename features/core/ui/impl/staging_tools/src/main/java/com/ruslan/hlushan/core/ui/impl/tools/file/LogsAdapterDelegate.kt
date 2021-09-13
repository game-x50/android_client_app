package com.ruslan.hlushan.core.ui.impl.tools.file

import android.view.View
import androidx.annotation.LayoutRes
import com.ruslan.hlushan.core.recycler.item.RecyclerItem
import com.ruslan.hlushan.core.ui.impl.tools.R
import com.ruslan.hlushan.core.ui.impl.tools.databinding.CoreUiImplStagingToolsFileLogItemBinding
import com.ruslan.hlushan.core.ui.recycler.adapter.AdapterDelegate
import com.ruslan.hlushan.core.ui.recycler.adapter.BaseItemViewHolder

internal class LogsAdapterDelegate : AdapterDelegate<Long, LogRecyclerItem, LogRecyclerItem> {

    @get:LayoutRes
    override val layoutResId: Int
        get() = R.layout.core_ui_impl_staging_tools_file_log_item

    override fun createViewHolder(itemView: View): BaseItemViewHolder<Long, LogRecyclerItem> =
            LogGameViewHolder(itemView)
}

private class LogGameViewHolder(itemView: View) : BaseItemViewHolder<Long, LogRecyclerItem>(itemView) {

    private val binding = CoreUiImplStagingToolsFileLogItemBinding.bind(itemView)

    override fun onBindView(item: LogRecyclerItem) {
        super.onBindView(item)

        binding.coreUiImplStagingToolsFileLogItemText.text = item.log
    }
}

internal data class LogRecyclerItem(
        override val id: Long,
        val log: String
) : RecyclerItem<Long>