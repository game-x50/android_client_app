package com.ruslan.hlushan.core.impl.tools.initUtils

import android.content.Context
import com.facebook.stetho.Stetho
import com.ruslan.hlushan.core.thread.UiMainThread

@UiMainThread
internal fun initStetho(appContext: Context) =
    Stetho.initializeWithDefaults(appContext)