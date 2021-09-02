package com.ruslan.hlushan.network.impl.interceptor

import com.ruslan.hlushan.network.api.GzipMarker
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import okio.BufferedSink
import okio.GzipSink
import okio.buffer
import java.io.IOException

internal const val CONTENT_ENCODING_HEADER_HEY = "Content-Encoding"
internal const val CONTENT_ENCODING_GZIP_HEADER_VALUE = "gzip"

internal class GzipRequestInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val gzippedRequest = gzipRequestIfNeeded(originalRequest)
        return chain.proceed(gzippedRequest)
    }
}

private fun gzipRequestIfNeeded(originalRequest: Request): Request {
    val originalRequestBody = originalRequest.body

    return if ((originalRequestBody != null) && originalRequest.needToGzip) {
        originalRequest.newBuilder()
                .removeHeader(GzipMarker.USE_GZIP_FOR_REQUEST_HEADER_KEY)
                .header(CONTENT_ENCODING_HEADER_HEY, CONTENT_ENCODING_GZIP_HEADER_VALUE)
                .method(originalRequest.method, forceContentLength(gzip(originalRequestBody)))
                .build()
    } else {
        originalRequest
    }
}

private val Request.needToGzip: Boolean
    get() = ((this.header(CONTENT_ENCODING_HEADER_HEY) == null)
             && (this.header(GzipMarker.USE_GZIP_FOR_REQUEST_HEADER_KEY) != null))

/**
 * https://github.com/square/okhttp/issues/350
 */
@Throws(IOException::class)
private fun forceContentLength(requestBody: RequestBody): RequestBody {
    val buffer = Buffer()

    requestBody.writeTo(buffer)

    return object : RequestBody() {
        override fun contentType(): MediaType? =
                requestBody.contentType()

        @Throws(IOException::class)
        override fun contentLength(): Long =
                buffer.size

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            sink.write(buffer.snapshot())
        }
    }
}

private fun gzip(body: RequestBody): RequestBody =
        object : RequestBody() {
            
            override fun contentType(): MediaType? =
                    body.contentType()

            @Throws(IOException::class)
            override fun contentLength(): Long = -1 // We don't know the compressed length in advance!

            @Throws(IOException::class)
            override fun writeTo(sink: BufferedSink) {
                val gzipSink = GzipSink(sink).buffer()
                gzipSink.use {
                    body.writeTo(gzipSink)
                }
            }
        }