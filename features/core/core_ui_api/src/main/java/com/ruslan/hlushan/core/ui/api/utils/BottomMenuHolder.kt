package com.ruslan.hlushan.core.ui.api.utils

import com.ruslan.hlushan.core.api.utils.thread.UiMainThread

//todo: move to another package
interface BottomMenuHolder {

    @UiMainThread
    fun showBottomMenu(show: Boolean)
}