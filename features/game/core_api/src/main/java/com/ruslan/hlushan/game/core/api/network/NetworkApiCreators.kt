package com.ruslan.hlushan.game.core.api.network

import kotlin.reflect.KClass

/**
 * @author Ruslan Hlushan on 2019-07-26
 */

interface NonAuthorizedNetworkApiCreator {
    fun <T : Any> createApi(service: KClass<T>, cacheFolderName: String, baseUrl: String): T
}

interface AuthorizedNetworkApiCreator {

    @SuppressWarnings("ClassOrdering")
    companion object {
        const val AUTHORIZATION_HEADER_KEY = "Authorization"
        const val AUTHORIZATION_HEADER_TO_REPLACE = "$AUTHORIZATION_HEADER_KEY: AUTH_HEADER"
    }

    fun <T : Any> createAuthorizedApi(service: KClass<T>, cacheFolderName: String, baseUrl: String): T
}