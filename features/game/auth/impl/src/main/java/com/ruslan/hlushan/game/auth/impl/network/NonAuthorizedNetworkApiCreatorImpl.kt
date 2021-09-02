package com.ruslan.hlushan.game.auth.impl.network

import android.content.Context
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.utils.InitAppConfig
import com.ruslan.hlushan.game.api.network.NonAuthorizedNetworkApiCreator
import com.ruslan.hlushan.network.api.NetworkBuildHelper
import com.ruslan.hlushan.network.api.NetworkConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * @author Ruslan Hlushan on 2019-07-26
 */
internal class NonAuthorizedNetworkApiCreatorImpl
@Inject
constructor(
        private val networkBuildHelper: dagger.Lazy<NetworkBuildHelper>,
        private val initAppConfig: dagger.Lazy<InitAppConfig>,
        private val networkConfig: dagger.Lazy<NetworkConfig>,
        private val appLogger: dagger.Lazy<AppLogger>,
        private val context: dagger.Lazy<Context>
) : NonAuthorizedNetworkApiCreator {

    override fun <T : Any> createApi(service: KClass<T>, cacheFolderName: String, baseUrl: String): T {
        val nonAuthorizedOkHttpClient = provideNonAuthorizedOkHttpClient(
                initAppConfig = initAppConfig.get(),
                networkConfig = networkConfig.get(),
                baseUrl = baseUrl,
                appLogger = appLogger.get(),
                context = context.get(),
                cacheFolderName = cacheFolderName
        )
        val apiRetrofit = provideApiRetrofit(nonAuthorizedOkHttpClient, baseUrl)
        return apiRetrofit.create(service.java)
    }

    private fun provideApiRetrofit(
            okHttpClient: OkHttpClient,
            baseUrl: String
    ): Retrofit =
            networkBuildHelper.get().provideRetrofitBuilder(okHttpClient)
                    .baseUrl(baseUrl)
                    .build()

    @SuppressWarnings("LongParameterList")
    private fun provideNonAuthorizedOkHttpClient(
            initAppConfig: InitAppConfig,
            networkConfig: NetworkConfig,
            baseUrl: String,
            appLogger: AppLogger,
            context: Context,
            cacheFolderName: String
    ): OkHttpClient =
            networkBuildHelper.get().provideOkHttpClientBuilder(
                    initAppConfig = initAppConfig,
                    networkConfig = networkConfig,
                    baseUrl = baseUrl,
                    cacheFolderName = cacheFolderName,
                    appLogger = appLogger,
                    context = context
            )
                    .build()
}