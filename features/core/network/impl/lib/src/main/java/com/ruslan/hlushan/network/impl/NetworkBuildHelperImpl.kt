package com.ruslan.hlushan.network.impl

import android.content.Context
import com.babylon.certificatetransparency.certificateTransparencyInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ruslan.hlushan.core.api.dto.InitAppConfig
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.network.api.NetworkBuildHelper
import com.ruslan.hlushan.network.api.NetworkConfig
import com.ruslan.hlushan.network.impl.interceptor.DefaultNetworkErrorsInterceptor
import com.ruslan.hlushan.network.impl.interceptor.GzipRequestInterceptor
import com.ruslan.hlushan.network.impl.interceptor.GzipResponseInterceptor
import com.ruslan.hlushan.network.impl.tools.addTools
import com.ruslan.hlushan.parsing.impl.utils.parsing.AppJson
import okhttp3.Cache
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.internal.tls.OkHostnameVerifier
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File
import java.net.URI
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class NetworkBuildHelperImpl @Inject constructor() : NetworkBuildHelper {

    override fun provideRetrofitBuilder(
            okHttpClient: OkHttpClient
    ): Retrofit.Builder =
            Retrofit.Builder()
                    .addConverterFactory(AppJson.asConverterFactory("application/json; charset=utf-8".toMediaType()))
                    .addCallAdapterFactory(provideRxJava2CallAdapterFactory())
                    .client(okHttpClient)

    override fun provideOkHttpClientBuilder(
            initAppConfig: InitAppConfig,
            networkConfig: NetworkConfig,
            baseUrl: String,
            cacheFolderName: String,
            appLogger: AppLogger,
            context: Context
    ): OkHttpClient.Builder {

        val okBuilder = OkHttpClient.Builder()

        okBuilder.connectTimeout(networkConfig.connectTimeoutSeconds, TimeUnit.SECONDS)
        okBuilder.readTimeout(networkConfig.readTimeoutSeconds, TimeUnit.SECONDS)
        okBuilder.writeTimeout(networkConfig.readTimeoutSeconds, TimeUnit.SECONDS)

        addTools(okBuilder, appLogger, context)

        okBuilder.addInterceptor(GzipRequestInterceptor())
        okBuilder.addInterceptor(GzipResponseInterceptor())

        okBuilder.addInterceptor(DefaultNetworkErrorsInterceptor())

        okBuilder.retryOnConnectionFailure(true)

        okBuilder.cache(provideCache(initAppConfig, networkConfig, cacheFolderName))

        val dispatcher = Dispatcher()
        dispatcher.maxRequests = networkConfig.maxRequests
        dispatcher.maxRequestsPerHost = networkConfig.maxRequestsPerHost
        okBuilder.dispatcher(dispatcher)

        val hostNameFromBaseUrl = extractHostName(baseUrl)
        okBuilder.hostnameVerifier { hostname, session ->
            OkHostnameVerifier.verify(hostNameFromBaseUrl, session)
        }
        val certificateTransparencyInterceptor = certificateTransparencyInterceptor {
            +hostNameFromBaseUrl
        }
        okBuilder.addNetworkInterceptor(certificateTransparencyInterceptor)

        return okBuilder
    }

    private fun provideCache(
            initAppConfig: InitAppConfig,
            networkConfig: NetworkConfig,
            cacheFolderName: String
    ): Cache {
        val httpCacheDirectory = File(initAppConfig.fileLogsFolder, "responses/$cacheFolderName")
        return Cache(httpCacheDirectory, networkConfig.cacheSize)
    }

    private fun provideRxJava2CallAdapterFactory() = RxJava2CallAdapterFactory.create()
}

private fun extractHostName(baseUrl: String): String = URI(baseUrl).host