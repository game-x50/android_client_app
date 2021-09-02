package com.ruslan.hlushan.core.impl.tools

import com.ruslan.hlushan.android.core.api.di.AppContextProvider
import com.ruslan.hlushan.core.api.di.LoggersProvider
import com.ruslan.hlushan.core.api.di.ToolsProvider
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.impl.BaseApplication

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