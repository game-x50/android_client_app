package com.ruslan.hlushan.core.ui.fragment.manager

import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver
import java.lang.ref.WeakReference

class FragmentManagerConfiguratorPluginObserver(
        private val configurator: FragmentManagerConfigurator,
        fragmentManager: FragmentManager
) : LifecyclePluginObserver {

    private val fragmentManagerReference = WeakReference(fragmentManager)

    override fun onBeforeSuperAttach() {
        fragmentManagerReference.get()?.let(configurator::configure)
    }
}