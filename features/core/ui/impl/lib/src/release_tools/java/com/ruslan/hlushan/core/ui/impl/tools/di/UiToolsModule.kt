package com.ruslan.hlushan.core.ui.impl.tools.di

import com.ruslan.hlushan.core.ui.api.utils.ViewModifier
import com.ruslan.hlushan.core.ui.impl.tools.di.impl.ViewModifierNoOpImpl
import dagger.Binds
import dagger.Module

@Module
internal interface UiToolsModule {

    @Binds
    fun provideViewModifier(impl: ViewModifierNoOpImpl): ViewModifier

//    @Binds
//    fun provideFragmentManagerConfigurator(impl: FragmentManagerConfiguratorNoOpImpl): FragmentManagerConfigurator
}