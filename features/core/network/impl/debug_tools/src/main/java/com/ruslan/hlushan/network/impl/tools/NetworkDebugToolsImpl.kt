package com.ruslan.hlushan.network.impl.tools

import android.content.Context
import com.ruslan.hlushan.core.api.log.AppLogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

fun addDebugTools(builder: OkHttpClient.Builder, appLogger: AppLogger, context: Context) {
    val httpLoggingInterceptor = provideHttpLoggingInterceptor(appLogger)
    builder.addInterceptor(httpLoggingInterceptor)
    addStagingTools(builder, context)
}

private fun provideHttpLoggingInterceptor(appLogger: AppLogger): HttpLoggingInterceptor {
    val logging = HttpLoggingInterceptor { message ->
        appLogger.log("NETWORK-API-HTTP-CALL", message)
    }

    logging.level = HttpLoggingInterceptor.Level.BODY

    return logging
}