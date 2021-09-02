package com.ruslan.hlushan.game.api.network

import kotlin.reflect.KClass

interface NonAuthorizedNetworkApiCreator {
    fun <T : Any> createApi(service: KClass<T>, cacheFolderName: String, baseUrl: String): T
}

interface AuthorizedNetworkApiCreator {

    fun <T : Any> createAuthorizedApi(service: KClass<T>, cacheFolderName: String, baseUrl: String): T

    companion object {
        const val AUTHORIZATION_HEADER_KEY = "Authorization"
        const val AUTHORIZATION_HEADER_TO_REPLACE = "$AUTHORIZATION_HEADER_KEY: AUTH_HEADER"
    }
}