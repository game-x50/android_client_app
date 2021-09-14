package com.ruslan.hlushan.core.ui.api.manager

import android.app.Activity
import com.ruslan.hlushan.core.thread.UiMainThread

interface AppActivitiesSettings {

    @UiMainThread
    fun checkLocaleAndRecreateIfNeeded(activity: Activity)

    @UiMainThread
    fun changeLangIfNeeded(activity: Activity)

    @UiMainThread
    fun changeThemeIfNeeded()
}