package com.ruslan.hlushan.network.impl.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.GzipSource
import okio.buffer
import java.io.IOException

private const val CONTENT_LENGTH_HEADER_KEY = "Content-Length"

internal class GzipResponseInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val ungzippedResponse = ungzipIfNeeded(originalResponse)
        return ungzippedResponse
    }
}

@Throws(IOException::class)
private fun ungzipIfNeeded(response: Response): Response {
    val gzippedResponseBody = response.body
    if ((gzippedResponseBody != null) && response.isGzipped) {

        GzipSource(gzippedResponseBody.source()).use { gzipSource ->

            val bodyString = gzipSource.buffer().readUtf8()

            val responseBody = bodyString.toResponseBody(gzippedResponseBody.contentType())

            val strippedHeaders = response.headers.newBuilder()
                    .removeAll(CONTENT_ENCODING_HEADER_HEY)
                    .removeAll(CONTENT_LENGTH_HEADER_KEY)
                    .build()

            return response.newBuilder()
                    .headers(strippedHeaders)
                    .body(responseBody)
                    .message(response.message)
                    .build()
        }
    } else {
        return response
    }
}

private val Response.isGzipped: Boolean
    get() {
        val contentEncodingValue = this.header(CONTENT_ENCODING_HEADER_HEY)
        return contentEncodingValue.equals(CONTENT_ENCODING_GZIP_HEADER_VALUE, ignoreCase = true)
    }