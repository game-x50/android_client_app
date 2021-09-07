package com.ruslan.hlushan.core.ui.api.recycler

import androidx.recyclerview.widget.RecyclerView
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.lifecycle.LifecyclePluginObserver
import com.ruslan.hlushan.third_party.androidx.recyclerview.extensions.clearLeakingDada

@UiMainThread
class RecyclerViewLifecyclePluginObserver(
        private val recyclerGet: () -> RecyclerView?
) : LifecyclePluginObserver {

    override fun onBeforeSuperDestroyView() {
        recyclerGet()?.clearLeakingDada()
    }
}