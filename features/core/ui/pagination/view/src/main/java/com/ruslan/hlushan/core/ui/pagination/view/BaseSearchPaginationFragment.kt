package com.ruslan.hlushan.core.ui.pagination.view

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.SearchView
import com.ruslan.hlushan.core.extensions.ifNotNull
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.pagination.viewmodel.SearchPaginationViewModel

abstract class BaseSearchPaginationFragment<VM : SearchPaginationViewModel<*, *, *>>
@ContentView
constructor(
        @LayoutRes layoutResId: Int
) : BaseFragment(layoutResId) {

    protected abstract val viewModel: VM

    protected abstract val searchView: SearchView

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        ifNotNull(intent) { nonNullIntent ->
            if (Intent.ACTION_SEARCH == nonNullIntent.action) {
                val query = nonNullIntent.getStringExtra(SearchManager.QUERY)
                searchView.setQuery(query, true)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSearchViewWatcher()
    }

    @UiMainThread
    private fun setSearchViewWatcher() = ifNotNull(activity) { nonNullActivity ->
        val searchManager = nonNullActivity.getSystemService(Context.SEARCH_SERVICE) as? SearchManager
        if (searchManager != null) {
            val searchableInfo = searchManager.getSearchableInfo(nonNullActivity.componentName)
            searchView.setSearchableInfo(searchableInfo)
        }
        searchView.setIconifiedByDefault(false)
        searchView.isIconified = false
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(searchString: String): Boolean {
                viewModel.setSearchText(searchString)
                return true
            }

            override fun onQueryTextSubmit(s: String): Boolean {
                hideKeyboard()
                return true
            }
        })
        searchView.clearFocus()
    }
}