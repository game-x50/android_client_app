package com.ruslan.hlushan.android.extensions

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

fun RecyclerView.setUpDefaults(
        adapter: RecyclerView.Adapter<*>,
        layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context),
        hasFixedSize: Boolean = true,
        @Suppress("MaxLineLength")
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

val RecyclerView.firstVisibleItemPosition: Int
    get() = when (val manager = layoutManager) {
        is LinearLayoutManager        -> manager.findFirstVisibleItemPosition()
        is StaggeredGridLayoutManager -> (manager.findFirstVisibleItemPositions(null).minOrNull() ?: 0)
        else                          -> 0
    }

val RecyclerView.lastVisibleItemPosition: Int
    get() = when (val manager = layoutManager) {
        is LinearLayoutManager        -> manager.findLastVisibleItemPosition()
        is StaggeredGridLayoutManager -> (manager.findLastVisibleItemPositions(null).maxOrNull() ?: 0)
        else                          -> 0
    }

private class PaginationScrollListener(
        private val onPaginationScrollListener: OnPaginationScrollListener
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        when {
            ((dy > 0) && (recyclerView.lastVisibleItemPosition >= 0)) -> {
                recyclerView.notifyOnScrolledBottom(onPaginationScrollListener)
            }
            ((dy < 0) && (recyclerView.firstVisibleItemPosition >= 0)) -> {
                recyclerView.notifyOnScrolledTop(onPaginationScrollListener)
            }
        }
    }
}

typealias OnPaginationScrollListener = (boundaryVisibleItem: Int, scrolledBottom: Boolean) -> Unit

fun RecyclerView.addPaginationScrollListener(onPaginationScrollListener: OnPaginationScrollListener) {
    this.addOnScrollListener(PaginationScrollListener(onPaginationScrollListener))
}

fun RecyclerView.notifyOnScrolledBottom(onPaginationScrollListener: OnPaginationScrollListener) {
    onPaginationScrollListener(this.lastVisibleItemPosition, true)
}

fun RecyclerView.notifyOnScrolledTop(onPaginationScrollListener: OnPaginationScrollListener) {
    onPaginationScrollListener(this.firstVisibleItemPosition, false)
}