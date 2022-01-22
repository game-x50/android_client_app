package com.ruslan.hlushan.core.impl.tools

import android.app.Application
import com.ruslan.hlushan.android.strict.mode.StrictModeUtil
import com.ruslan.hlushan.core.impl.tools.initUtils.initStetho
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.thread.UiMainThread

@UiMainThread
fun initDebugTools(app: Application, appLogger: AppLogger, logger: (String) -> Unit) {
    initStagingTools(app, appLogger, logger)
    initStetho(app)
    logger("after initStetho()")
    StrictModeUtil.init()
    logger("after StrictModeUtil.init()")
}