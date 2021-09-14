package com.ruslan.hlushan.network.api

import android.content.Context
import com.ruslan.hlushan.core.api.dto.InitAppConfig
import com.ruslan.hlushan.core.logger.api.AppLogger
import okhttp3.OkHttpClient
import retrofit2.Retrofit

interface NetworkBuildHelper {

    fun provideRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder

    @SuppressWarnings("LongParameterList")
    fun provideOkHttpClientBuilder(
            initAppConfig: InitAppConfig,
            networkConfig: NetworkConfig,
            baseUrl: String,
            cacheFolderName: String,
            appLogger: AppLogger,
            context: Context
    ): OkHttpClient.Builder
}