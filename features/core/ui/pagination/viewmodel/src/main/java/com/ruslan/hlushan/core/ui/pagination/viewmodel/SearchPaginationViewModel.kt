package com.ruslan.hlushan.core.ui.pagination.viewmodel

import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.pagination.api.PaginationLimits
import com.ruslan.hlushan.core.recycler.item.RecyclerItem

abstract class SearchPaginationViewModel<ItemId : Any, RI : RecyclerItem<ItemId>, PageId : Any>(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        schedulersManager: SchedulersManager,
        limits: PaginationLimits = PaginationLimits()
) : PaginationViewModel<String, ItemId, RI, PageId>(
        initFilter = "",
        limits = limits,
        appLogger = appLogger,
        threadChecker = threadChecker,
        schedulersManager = schedulersManager
) {

    fun setSearchText(searchText: String) = updateFilter(searchText.trim())
}