package com.ruslan.hlushan.core.thread

import java.lang.ref.WeakReference

class SingleThreadChecker(
        thread: Thread = Thread.currentThread()
) : ThreadChecker {

    private val threadReference = WeakReference<Thread>(thread)

    override val isNeededThread: Boolean
        get() = (threadReference.get() == Thread.currentThread())
}