package com.ruslan.hlushan.game.auth.impl.repo

import com.ruslan.hlushan.game.auth.impl.repo.dto.server.UniqueDataResponse
import com.ruslan.hlushan.game.auth.impl.repo.dto.server.UserNameRequest
import com.ruslan.hlushan.network.api.GzipMarker
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface AuthHttpsApi {

    @POST("checkUniqueUserName")
    @Headers(
            GzipMarker.USE_GZIP_FOR_REQUEST_HEADER_TO_REPLACE,
            GzipMarker.ACCEPT_GZIP_ENCODING_FOR_RESPONSES
    )
    fun checkUniqueUserName(@Body request: UserNameRequest): Single<UniqueDataResponse>
}