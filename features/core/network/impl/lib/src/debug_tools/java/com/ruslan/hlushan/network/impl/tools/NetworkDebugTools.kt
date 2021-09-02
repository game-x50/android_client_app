package com.ruslan.hlushan.network.impl.tools

import android.content.Context
import com.ruslan.hlushan.core.api.log.AppLogger
import okhttp3.OkHttpClient

internal fun addTools(builder: OkHttpClient.Builder, appLogger: AppLogger, context: Context) =
        addDebugTools(builder, appLogger, context)