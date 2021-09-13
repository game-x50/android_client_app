package com.ruslan.hlushan.core.impl.tools

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.di.ToolsProvider
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.impl.BaseApplication
import com.ruslan.hlushan.core.logger.api.di.LoggersProvider

@UiMainThread
internal fun initTools(
        application: BaseApplication,
        appLogger: AppLogger,
        logger: (String) -> Unit
) = Unit

fun createToolsProvider(
        appContextProvider: AppContextProvider,
        loggersProvider: LoggersProvider
): ToolsProvider = object : ToolsProvider {}