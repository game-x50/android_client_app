package com.ruslan.hlushan.core.ui.impl.tools.di

import com.ruslan.hlushan.core.ui.api.utils.ViewModifier
import com.ruslan.hlushan.core.ui.impl.tools.di.impl.ViewModifierNoOpImpl
import dagger.Module
import dagger.Provides

@Module
internal object UiToolsModule {

    @JvmStatic
    @Provides
    fun provideViewModifier(): ViewModifier = ViewModifierNoOpImpl()
}