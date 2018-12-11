package com.ruslan.hlushan.network.impl.tools

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import okhttp3.OkHttpClient

fun addStagingTools(builder: OkHttpClient.Builder, context: Context) {
    val chuckInterceptor = provideChuckInterceptor(context)
    builder.addInterceptor(chuckInterceptor)
}

private fun provideChuckInterceptor(context: Context): ChuckerInterceptor {
    val collector = ChuckerCollector(
            context = context,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
    )
    return ChuckerInterceptor.Builder(context = context)
            .collector(collector = collector)
            .maxContentLength(length = 120_000L)
            .build()
}