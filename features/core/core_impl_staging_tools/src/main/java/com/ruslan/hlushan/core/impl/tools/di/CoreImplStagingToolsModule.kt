package com.ruslan.hlushan.core.impl.tools.di

import com.ruslan.hlushan.core.api.tools.BlockCanaryTool
import com.ruslan.hlushan.core.api.tools.ChuckTool
import com.ruslan.hlushan.core.api.tools.DatabaseViewerTool
import com.ruslan.hlushan.core.api.tools.LeakCanaryTool
import com.ruslan.hlushan.core.api.tools.LynxTool
import com.ruslan.hlushan.core.api.tools.RxDisposableWatcherTool
import com.ruslan.hlushan.core.api.tools.TaktTool
import com.ruslan.hlushan.core.api.tools.TinyDancerTool
import com.ruslan.hlushan.core.impl.tools.impl.BlockCanaryToolStagingImpl
import com.ruslan.hlushan.core.impl.tools.impl.ChuckToolStagingImpl
import com.ruslan.hlushan.core.impl.tools.impl.DatabaseViewerToolStagingImpl
import com.ruslan.hlushan.core.impl.tools.impl.LeakCanaryToolStagingImpl
import com.ruslan.hlushan.core.impl.tools.impl.LynxToolStagingImpl
import com.ruslan.hlushan.core.impl.tools.impl.RxDisposableWatcherToolImpl
import com.ruslan.hlushan.core.impl.tools.impl.TaktToolStagingImpl
import com.ruslan.hlushan.core.impl.tools.impl.TinyDancerToolStagingImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * @author Ruslan Hlushan on 2019-07-18
 */

@Module
internal interface CoreImplStagingToolsModule {

    @Binds
    fun provideBlockCanaryTool(impl: BlockCanaryToolStagingImpl): BlockCanaryTool

    @Binds
    fun provideLynxTool(impl: LynxToolStagingImpl): LynxTool

    @Binds
    @Singleton
    fun provideTinyDancerTool(impl: TinyDancerToolStagingImpl): TinyDancerTool

    @Binds
    @Singleton
    fun provideTaktTool(impl: TaktToolStagingImpl): TaktTool

    @Binds
    fun provideDatabaseViewerTool(impl: DatabaseViewerToolStagingImpl): DatabaseViewerTool

    @Binds
    @Singleton
    fun provideChuckTool(impl: ChuckToolStagingImpl): ChuckTool

    @Binds
    fun provideLeakCanaryTool(impl: LeakCanaryToolStagingImpl): LeakCanaryTool

    @Binds
    @Singleton
    fun provideRxDisposableWatcherTool(impl: RxDisposableWatcherToolImpl): RxDisposableWatcherTool
}