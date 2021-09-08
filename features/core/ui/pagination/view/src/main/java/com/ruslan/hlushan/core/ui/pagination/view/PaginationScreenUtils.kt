package com.ruslan.hlushan.core.ui.pagination.view

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ruslan.hlushan.android.extensions.colorAttributeValue
import com.ruslan.hlushan.core.ui.pagination.viewmodel.PaginationViewModel
import com.ruslan.hlushan.core.ui.recycler.adapter.DelegatesRecyclerAdapter
import com.ruslan.hlushan.core.ui.recycler.item.RecyclerItem
import com.ruslan.hlushan.third_party.androidx.recyclerview.extensions.addPaginationScrollListener
import com.ruslan.hlushan.third_party.androidx.recyclerview.extensions.setUpDefaults

fun <Id : Any, RI : RecyclerItem<Id>> Fragment.setUpPagination(
        recyclerAdapter: DelegatesRecyclerAdapter<Id, RI>,
        paginationViewModel: PaginationViewModel<*, Id, RI, *>,
        recyclerView: RecyclerView?,
        swipeRefreshLayout: SwipeRefreshLayout?
) = requireActivity().setUpPagination(
        recyclerAdapter = recyclerAdapter,
        paginationViewModel = paginationViewModel,
        recyclerView = recyclerView,
        swipeRefreshLayout = swipeRefreshLayout
)

fun <Id : Any, RI : RecyclerItem<Id>> Activity.setUpPagination(
        recyclerAdapter: DelegatesRecyclerAdapter<Id, RI>,
        paginationViewModel: PaginationViewModel<*, Id, RI, *>,
        recyclerView: RecyclerView?,
        swipeRefreshLayout: SwipeRefreshLayout?
) {
    if (recyclerView != null) {
        recyclerView.setUpDefaults(recyclerAdapter)
        recyclerView.addPaginationScrollListener(paginationViewModel::onScrolled)
    }

    swipeRefreshLayout?.setColorSchemeColors(
            this.colorAttributeValue(com.google.android.material.R.attr.colorSecondary)
    )

    swipeRefreshLayout?.setOnRefreshListener { paginationViewModel.refresh() }
}