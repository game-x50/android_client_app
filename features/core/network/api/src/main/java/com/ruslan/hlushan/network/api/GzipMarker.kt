package com.ruslan.hlushan.network.api

object GzipMarker {

    const val USE_GZIP_FOR_REQUEST_HEADER_KEY: String = "Use-Gzip-For-Request"
    const val USE_GZIP_FOR_REQUEST_HEADER_TO_REPLACE: String = "$USE_GZIP_FOR_REQUEST_HEADER_KEY: TRUE"

    const val ACCEPT_GZIP_ENCODING_FOR_RESPONSES: String = "Accept-Encoding: gzip, deflate"
}