package com.ruslan.hlushan.core.ui.impl.tools.di

import com.ruslan.hlushan.core.ui.api.utils.ViewModifier
import com.ruslan.hlushan.core.ui.fragment.manager.FragmentManagerConfigurator
import com.ruslan.hlushan.core.ui.impl.tools.DebugSettingsFragment
import com.ruslan.hlushan.core.ui.impl.tools.DrawerViewModifierImpl
import com.ruslan.hlushan.core.ui.impl.tools.FragmentManagerConfiguratorImpl
import com.ruslan.hlushan.core.ui.impl.tools.R
import dagger.Module
import dagger.Provides

@Module
object DebugUiToolsModule {

    @JvmStatic
    @Provides
    fun provideViewModifier(): ViewModifier =
            DrawerViewModifierImpl(
                    layoutResId = R.layout.core_ui_impl_debug_tools_settings_drawer_view
            )

    @JvmStatic
    @Provides
    fun provideFragmentManagerConfigurator(): FragmentManagerConfigurator =
            FragmentManagerConfiguratorImpl(
                    fragmentTagUsageViolationClasses = listOf(
                            DebugSettingsFragment::class
                    )
            )
}