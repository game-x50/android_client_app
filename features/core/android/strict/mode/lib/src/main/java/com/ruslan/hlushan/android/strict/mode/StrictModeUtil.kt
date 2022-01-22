package com.ruslan.hlushan.android.strict.mode

import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.ruslan.hlushan.core.thread.UiMainThread

object StrictModeUtil {

    @UiMainThread
    fun init() {
        StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build()
        )

        StrictMode.setVmPolicy(
                VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build()
        )
    }
}