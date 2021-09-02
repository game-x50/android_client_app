package com.ruslan.hlushan.core.impl.tools.initUtils

import android.content.Context
import com.github.moduth.blockcanary.BlockCanary
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.impl.tools.impl.AppBlockCanaryContext

@UiMainThread
internal fun initBlockCanary(appContext: Context, appLogger: AppLogger) =
    BlockCanary.install(appContext, AppBlockCanaryContext(appLogger))
            .start()