package com.ruslan.hlushan.core.api.tools

import com.ruslan.hlushan.third_party.androidx.room.utils.DatabaseViewInfo

interface BlockCanaryTool {
    fun start()
    fun stop()
    fun openScreen()
}

interface TinyDancerTool {
    var show: Boolean
}

interface TaktTool {
    var show: Boolean
}

interface LynxTool {
    fun openScreen()
}

interface DatabaseViewerTool {

    fun showDatabase(database: DatabaseViewInfo)
}

interface ChuckTool {
    fun openScreen()
}

interface LeakCanaryTool {
    var enabled: Boolean
    fun openScreen()
}

interface RxDisposableWatcherTool {
    fun showReport()
}