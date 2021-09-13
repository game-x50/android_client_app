package com.ruslan.hlushan.core.impl.tools.impl

import android.content.Context
import com.ruslan.hlushan.core.api.tools.LeakCanaryTool
import leakcanary.AppWatcher
import leakcanary.LeakCanary
import javax.inject.Inject

internal class LeakCanaryToolStagingImpl
@Inject
constructor(
        private val appContext: Context
) : LeakCanaryTool {

    override var enabled: Boolean
        get() = AppWatcher.config.enabled
        set(newValue) {
            if (enabled != newValue) {
                AppWatcher.config = AppWatcher.config.copy(enabled = newValue)
            }
        }

    override fun openScreen() = appContext.startActivity(LeakCanary.newLeakDisplayActivityIntent())
}