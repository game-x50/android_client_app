package com.ruslan.hlushan.core.api.test.utils.utils.thread

import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import java.util.concurrent.atomic.AtomicBoolean

class ThreadCheckerMock(defaultIsNeededThread: Boolean) : ThreadChecker {

    private val atomicIsNeededThread = AtomicBoolean(defaultIsNeededThread)

    override var isNeededThread: Boolean
        get() = atomicIsNeededThread.get()
        set(newValue) {
            atomicIsNeededThread.set(newValue)
        }
}