package com.ruslan.hlushan.core.thread.test.utils

import com.ruslan.hlushan.core.thread.ThreadChecker
import java.util.concurrent.atomic.AtomicBoolean

class ThreadCheckerMock(defaultIsNeededThread: Boolean) : ThreadChecker {

    private val atomicIsNeededThread = AtomicBoolean(defaultIsNeededThread)

    override var isNeededThread: Boolean
        get() = atomicIsNeededThread.get()
        set(newValue) {
            atomicIsNeededThread.set(newValue)
        }
}