package com.ruslan.hlushan.game.auth.impl.network

import com.ruslan.hlushan.game.api.network.AuthorizedNetworkApiCreator
import com.ruslan.hlushan.game.auth.impl.repo.AuthRepository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

private const val UNAUTHORIZED_CODE = 401

@Singleton//todo: should not be here (if possible move to dagger module), but Singleton scope is correct
internal class AuthHttpInterceptor
@Inject
constructor(private val authRepository: AuthRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val oldRequest = chain.request()

        return if (oldRequest.header(AuthorizedNetworkApiCreator.AUTHORIZATION_HEADER_KEY) != null) {

            val newRequest = oldRequest.withAuthHeader()
            val response = chain.proceed(newRequest)

            if (response.code == UNAUTHORIZED_CODE) {

                synchronized(authRepository) {
                    @Suppress("MaxLineLength")
                    if (newRequest.header(AuthorizedNetworkApiCreator.AUTHORIZATION_HEADER_KEY) == authRepository.getUserToken()) {
                        authRepository.updateUserToken()
                    }

                    val newRequestAfterUnauthorized = oldRequest.withAuthHeader()
                    chain.proceed(newRequestAfterUnauthorized)
                }
            } else {
                response
            }
        } else {
            chain.proceed(oldRequest)
        }
    }

    private fun Request.withAuthHeader(): Request =
            this.newBuilder()
                    .header(
                            AuthorizedNetworkApiCreator.AUTHORIZATION_HEADER_KEY,
                            authRepository.getUserToken().orEmpty()
                    )
                    .build()
}