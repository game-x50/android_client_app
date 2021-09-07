package com.ruslan.hlushan.core.ui.pagination.viewmodel

import com.ruslan.hlushan.core.api.dto.pagination.PaginationLimits
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.ui.recycler.item.RecyclerItem

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