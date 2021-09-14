package com.ruslan.hlushan.core.ui.pagination.viewmodel

import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.pagination.api.PaginationLimits
import com.ruslan.hlushan.core.recycler.item.RecyclerItem
import com.ruslan.hlushan.core.thread.ThreadChecker
import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager

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