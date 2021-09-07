package com.ruslan.hlushan.core.ui.fragment

import androidx.fragment.app.Fragment
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.manager.AppActivitiesSettings
import com.ruslan.hlushan.core.ui.api.presentation.lifecycle.LifecyclePluginObserver
import com.ruslan.hlushan.extensions.ifNotNull
import java.lang.ref.WeakReference

internal class AppActivitiesSettingsLifecyclePluginObserver(
        owner: Fragment,
        private val appActivitiesSettings: AppActivitiesSettings
) : LifecyclePluginObserver {

    private val ownerFragmentReference = WeakReference(owner)

    @UiMainThread
    override fun onBeforeSuperStart() {
        ifNotNull(ownerFragmentReference.get()?.activity) { nonNullActivity ->
            appActivitiesSettings.checkLocaleAndRecreateIfNeeded(nonNullActivity)
        }
    }
}