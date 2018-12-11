package com.ruslan.hlushan.android.extensions

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

fun RecyclerView.setUpDefaults(
        adapter: RecyclerView.Adapter<*>,
        layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context),
        hasFixedSize: Boolean = true,
        stateRestorationPolicy: RecyclerView.Adapter.StateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
) {
    this.layoutManager = layoutManager
    this.setHasFixedSize(hasFixedSize)
    adapter.stateRestorationPolicy = stateRestorationPolicy
    this.adapter = adapter
}

fun RecyclerView.clearLeakingDada() {
    this.adapter = null
}

fun RecyclerView.getAllViewHolders(): List<RecyclerView.ViewHolder> =
        (0 until this.childCount)
                .map { index -> this.getChildViewHolder(this.getChildAt(index)) }

val RecyclerView.lastVisibleItemPosition: Int
    get() = when (val manager = layoutManager) {
        is LinearLayoutManager        -> manager.findLastVisibleItemPosition()
        is StaggeredGridLayoutManager -> (manager.findLastVisibleItemPositions(null).maxOrNull() ?: 0)
        else                          -> 0
    }

private class PaginationScrollListener(private val onPaginationScrollListener: OnPaginationScrollListener) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) {
            recyclerView.provideScrollDataToCallback(onPaginationScrollListener)
        }
    }
}

typealias OnPaginationScrollListener = (lastVisibleItem: Int) -> Unit

fun RecyclerView.addPaginationScrollListener(onPaginationScrollListener: OnPaginationScrollListener) {
    this.addOnScrollListener(PaginationScrollListener(onPaginationScrollListener))
}

fun RecyclerView.provideScrollDataToCallback(onPaginationScrollListener: OnPaginationScrollListener) {
    onPaginationScrollListener(this.lastVisibleItemPosition)
}