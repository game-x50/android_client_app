package com.ruslan.hlushan.core.ui.api.manager

import android.app.Activity
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread

/**
 * Created by Ruslan on 04.02.2018.
 */

interface AppActivitiesSettings {

    @UiMainThread
    fun checkLocaleAndRecreateIfNeeded(activity: Activity)

    @UiMainThread
    fun changeLangIfNeeded(activity: Activity)

    @UiMainThread
    fun changeThemeIfNeeded()
}