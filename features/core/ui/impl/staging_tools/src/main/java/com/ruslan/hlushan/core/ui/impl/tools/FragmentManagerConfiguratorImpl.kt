package com.ruslan.hlushan.core.ui.impl.tools

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.android.strict.mode.fragment.FragmentStrictModeConfigurator
import com.ruslan.hlushan.core.ui.fragment.manager.FragmentManagerConfigurator
import kotlin.reflect.KClass

class FragmentManagerConfiguratorImpl(
        private val fragmentTagUsageViolationClasses: List<KClass<out Fragment>>
) : FragmentManagerConfigurator {

    override fun configure(fragmentManager: FragmentManager) =
            FragmentStrictModeConfigurator.configure(
                    fragmentManager = fragmentManager,
                    fragmentTagUsageViolationClasses = fragmentTagUsageViolationClasses
            )
}