package com.ruslan.hlushan.core.impl.tools

import android.app.Application
import com.ruslan.hlushan.core.impl.tools.initUtils.initBlockCanary
import com.ruslan.hlushan.core.impl.tools.initUtils.initLeakCanary
import com.ruslan.hlushan.core.impl.tools.initUtils.initSkippedFramesWarning
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.thread.UiMainThread

@UiMainThread
fun initStagingTools(app: Application, appLogger: AppLogger) {
    initSkippedFramesWarning(appLogger)
    appLogger.log("after ChoreographerSkippedFramesWarningThreshold()")
    initLeakCanary(appLogger)
    appLogger.log("after initLeakCanary()")
    initBlockCanary(app, appLogger)
    appLogger.log("after initBlockCanary()")
}