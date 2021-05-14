package com.ruslan.hlushan.core.ui.api.presentation.view.fragment

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ruslan.hlushan.android.extensions.addPaginationScrollListener
import com.ruslan.hlushan.android.extensions.colorAttributeValue
import com.ruslan.hlushan.android.extensions.setUpDefaults
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.PaginationViewModel
import com.ruslan.hlushan.core.ui.api.recycler.DelegatesRecyclerAdapter
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem

/**
 * Created by Ruslan on 08.10.2017.
 */

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

    swipeRefreshLayout?.setColorSchemeColors(this.colorAttributeValue(com.google.android.material.R.attr.colorSecondary))

    swipeRefreshLayout?.setOnRefreshListener { paginationViewModel.refresh() }
}