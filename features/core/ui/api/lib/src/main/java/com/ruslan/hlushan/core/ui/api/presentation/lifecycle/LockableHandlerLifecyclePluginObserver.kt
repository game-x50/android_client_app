package com.ruslan.hlushan.core.ui.api.presentation.lifecycle

import com.ruslan.hlushan.core.api.utils.thread.SingleThreadSafety
import com.ruslan.hlushan.core.ui.api.utils.LockableHandler

@SingleThreadSafety
class LockableHandlerLifecyclePluginObserver(
        private val viewsHandler: LockableHandler
) : LifecyclePluginObserver {

    override fun onAfterSuperViewCreated() =
            viewsHandler.unlock()

    override fun onBeforeSuperDestroyView() =
            viewsHandler.lockAndClearDelayed()
}