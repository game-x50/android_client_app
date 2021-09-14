package com.ruslan.hlushan.core.impl.tools.initUtils

import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.ruslan.hlushan.core.thread.UiMainThread

@UiMainThread
internal fun initStrictMode() {

    StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                                       .detectAll()
                                       .penaltyLog()
                                       .build())

    StrictMode.setVmPolicy(VmPolicy.Builder()
                                   .detectAll()
                                   .penaltyLog()
                                   .build())
}