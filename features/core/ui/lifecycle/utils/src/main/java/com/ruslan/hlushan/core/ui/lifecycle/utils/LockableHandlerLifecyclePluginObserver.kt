package com.ruslan.hlushan.core.ui.lifecycle.utils

import com.ruslan.hlushan.core.thread.SingleThreadSafety
import com.ruslan.hlushan.core.ui.api.utils.LockableHandler
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver

@SingleThreadSafety
class LockableHandlerLifecyclePluginObserver(
        private val viewsHandler: LockableHandler
) : LifecyclePluginObserver {

    override fun onAfterSuperViewCreated() =
            viewsHandler.unlock()

    override fun onBeforeSuperDestroyView() =
            viewsHandler.lockAndClearDelayed()
}