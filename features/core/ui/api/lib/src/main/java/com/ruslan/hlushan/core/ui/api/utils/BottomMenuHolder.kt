package com.ruslan.hlushan.core.ui.api.utils

import com.ruslan.hlushan.core.thread.UiMainThread

interface BottomMenuHolder {

    @UiMainThread
    fun showBottomMenu(show: Boolean)
}