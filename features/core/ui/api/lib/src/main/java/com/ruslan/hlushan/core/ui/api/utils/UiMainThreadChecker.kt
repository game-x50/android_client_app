package com.ruslan.hlushan.core.ui.api.utils

import android.os.Looper
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker

object UiMainThreadChecker : ThreadChecker {

    override val isNeededThread: Boolean
        get() = (Thread.currentThread() == Looper.getMainLooper().thread)
}