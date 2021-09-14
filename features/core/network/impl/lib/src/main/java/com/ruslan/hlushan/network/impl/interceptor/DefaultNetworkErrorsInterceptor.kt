package com.ruslan.hlushan.network.impl.interceptor

import com.ruslan.hlushan.core.error.networkErrorMap
import okhttp3.Interceptor
import okhttp3.Response

internal class DefaultNetworkErrorsInterceptor : Interceptor {

    @SuppressWarnings("TooGenericExceptionCaught")
    override fun intercept(chain: Interceptor.Chain): Response =
            try {
                chain.proceed(chain.request())
            } catch (error: Throwable) {
                throw networkErrorMap(error)
            }
}