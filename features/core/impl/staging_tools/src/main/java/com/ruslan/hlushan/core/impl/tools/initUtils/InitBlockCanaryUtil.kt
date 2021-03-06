package com.ruslan.hlushan.core.impl.tools.initUtils

import android.content.Context
import com.github.moduth.blockcanary.BlockCanary
import com.ruslan.hlushan.core.impl.tools.impl.AppBlockCanaryContext
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.thread.UiMainThread

@UiMainThread
internal fun initBlockCanary(appContext: Context, appLogger: AppLogger) =
    BlockCanary.install(appContext, AppBlockCanaryContext(appLogger))
            .start()