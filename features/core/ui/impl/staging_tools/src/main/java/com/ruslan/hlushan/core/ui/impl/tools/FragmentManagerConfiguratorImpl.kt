package com.ruslan.hlushan.core.ui.impl.tools

import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.android.strict.mode.fragment.FragmentStrictModeConfigurator
import com.ruslan.hlushan.core.ui.fragment.manager.FragmentManagerConfigurator

class FragmentManagerConfiguratorImpl : FragmentManagerConfigurator {

    override fun configure(fragmentManager: FragmentManager) =
            FragmentStrictModeConfigurator.configure(fragmentManager)
}