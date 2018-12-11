package com.ruslan.hlushan.core.ui.api.presentation.presenter

import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem

/**
 * Created by Ruslan on 08.10.2017.
 */

abstract class SearchPaginationViewModel<ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any>(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        schedulersManager: SchedulersManager
) : PaginationViewModel<String, ItemId, RI, PageId>(
        initFilter = "",
        appLogger = appLogger,
        threadChecker = threadChecker,
        schedulersManager = schedulersManager
) {

    internal fun setSearchText(searchText: String) = updateFilter(searchText.trim())
}