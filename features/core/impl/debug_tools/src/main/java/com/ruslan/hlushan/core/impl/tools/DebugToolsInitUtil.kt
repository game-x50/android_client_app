package com.ruslan.hlushan.core.impl.tools

import android.app.Application
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.impl.tools.initUtils.initStetho
import com.ruslan.hlushan.core.impl.tools.initUtils.initStrictMode

@UiMainThread
fun initDebugTools(app: Application, appLogger: AppLogger, logger: (String) -> Unit) {
    initStagingTools(app, appLogger, logger)
    initStetho(app)
    logger("after initStetho()")
    initStrictMode()
    logger("after initStrictMode()")
}