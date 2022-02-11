package com.ruslan.hlushan.core.impl.tools

import android.app.Application
import com.ruslan.hlushan.android.strict.mode.StrictModeUtil
import com.ruslan.hlushan.core.impl.tools.initUtils.initStetho
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.thread.UiMainThread

@UiMainThread
fun initDebugTools(app: Application, appLogger: AppLogger) {
    initStagingTools(app, appLogger)
    initStetho(app)
    appLogger.log("after initStetho()")
    StrictModeUtil.init()
    appLogger.log("after StrictModeUtil.init()")
}