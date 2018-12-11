package com.ruslan.hlushan.core.impl.tools

import android.app.Application
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.impl.tools.initUtils.initBlockCanary
import com.ruslan.hlushan.core.impl.tools.initUtils.initLeakCanary
import com.ruslan.hlushan.core.impl.tools.initUtils.initSkippedFramesWarning

@UiMainThread
fun initStagingTools(app: Application, appLogger: AppLogger, logger: (String) -> Unit) {
    initSkippedFramesWarning(appLogger)
    logger("after ChoreographerSkippedFramesWarningThreshold()")
    initLeakCanary(appLogger)
    logger("after initLeakCanary()")
    initBlockCanary(app, appLogger)
    logger("after initBlockCanary()")
}