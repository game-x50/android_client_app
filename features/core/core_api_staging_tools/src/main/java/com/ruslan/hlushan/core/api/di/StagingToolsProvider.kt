package com.ruslan.hlushan.core.api.di

import com.ruslan.hlushan.core.api.tools.BlockCanaryTool
import com.ruslan.hlushan.core.api.tools.ChuckTool
import com.ruslan.hlushan.core.api.tools.DatabaseViewerTool
import com.ruslan.hlushan.core.api.tools.LeakCanaryTool
import com.ruslan.hlushan.core.api.tools.LynxTool
import com.ruslan.hlushan.core.api.tools.RxDisposableWatcherTool
import com.ruslan.hlushan.core.api.tools.TaktTool
import com.ruslan.hlushan.core.api.tools.TinyDancerTool

/**
 * @author Ruslan Hlushan on 2019-07-18
 */

interface StagingToolsProvider : BlockCanaryToolProvider,
                                 TinyDancerToolProvider,
                                 TaktToolProvider,
                                 LynxToolProvider,
                                 DatabaseViewerToolProvider,
                                 ChuckToolProvider,
                                 LeakCanaryToolProvider,
                                 RxDisposableWatcherToolProvider

interface BlockCanaryToolProvider {
    fun provideBlockCanaryTool(): BlockCanaryTool
}

interface TinyDancerToolProvider {
    fun provideTinyDancerTool(): TinyDancerTool
}

interface TaktToolProvider {
    fun provideTaktTool(): TaktTool
}

interface LynxToolProvider {
    fun provideLynxTool(): LynxTool
}

interface DatabaseViewerToolProvider {
    fun provideDatabaseViewerTool(): DatabaseViewerTool
}

interface ChuckToolProvider {
    fun provideChuckTool(): ChuckTool
}

interface LeakCanaryToolProvider {
    fun provideLeakCanaryTool(): LeakCanaryTool
}

interface RxDisposableWatcherToolProvider {
    fun provideRxDisposableWatcherTool(): RxDisposableWatcherTool
}