package com.ruslan.hlushan.core.ui.impl.tools.di

import com.ruslan.hlushan.core.ui.api.utils.ViewModifier
import com.ruslan.hlushan.core.ui.impl.tools.DrawerViewModifierImpl
import com.ruslan.hlushan.core.ui.impl.tools.R
import dagger.Module
import dagger.Provides

@Module
object DebugUiToolsModule {

    @JvmStatic
    @Provides
    fun provideViewModifier(): ViewModifier = DrawerViewModifierImpl(
            layoutResId = R.layout.core_ui_impl_debug_tools_settings_drawer_view
    )
}