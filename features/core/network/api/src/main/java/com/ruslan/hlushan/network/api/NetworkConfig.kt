package com.ruslan.hlushan.network.api

@SuppressWarnings("MagicNumber")
data class NetworkConfig(
        val connectTimeoutSeconds: Long = 15,
        val readTimeoutSeconds: Long = 20,
        val cacheSize: Long = 25 * 1024 * 1024,
        val maxRequests: Int = 100,
        val maxRequestsPerHost: Int = 10
)