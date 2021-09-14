package com.ruslan.hlushan.core.ui.api.utils

import android.os.Handler
import com.ruslan.hlushan.core.thread.SingleThreadSafety
import java.util.concurrent.TimeUnit

@SingleThreadSafety
class LockableHandler(defaultIsLocked: Boolean) {

    private val handler = Handler()

    private var isLocked: Boolean = defaultIsLocked

    fun post(action: Runnable) = executeIfUnlocked {
        handler.post(action)
    }

    fun postDelayed(delay: Long, delayTimeUnit: TimeUnit, action: Runnable) = executeIfUnlocked {
        handler.postDelayed(action, delayTimeUnit.toMillis(delay))
    }

    fun removeDelayed(action: Runnable) = handler.removeCallbacks(action)

    fun unlock() {
        isLocked = false
    }

    fun lockAndClearDelayed() {
        isLocked = true
        handler.removeCallbacksAndMessages(null)
    }

    private inline fun executeIfUnlocked(block: () -> Unit) {
        if (!isLocked) {
            block()
        }
    }
}