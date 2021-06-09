package com.ruslan.hlushan.game.storage.impl.remote

import com.ruslan.hlushan.game.core.api.network.AuthorizedNetworkApiCreator
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.CurrentTimestampResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.GetNewRemoteCreatedRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.LocalModifiedApiResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.RemoteApiGame
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.UpdateLocalNonModifiedApiResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.UpdateLocalSyncedRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.UploadModifiedApiRequest
import com.ruslan.hlushan.network.api.GzipMarker
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface SyncRemoteHttpApi {

    @GET("getTimestamp")
    @Headers(GzipMarker.ACCEPT_GZIP_ENCODING_FOR_RESPONSES)
    fun getRemoteTimestamp(): Single<CurrentTimestampResponse>

    @POST("updateModified")
    @Headers(
            GzipMarker.USE_GZIP_FOR_REQUEST_HEADER_TO_REPLACE,
            GzipMarker.ACCEPT_GZIP_ENCODING_FOR_RESPONSES,
            AuthorizedNetworkApiCreator.AUTHORIZATION_HEADER_TO_REPLACE
    )
    fun uploadLocalModified(
            @Body requests: List<UploadModifiedApiRequest>
    ): Single<List<LocalModifiedApiResponse>>

    @POST("getUpdated")
    @Headers(
            GzipMarker.USE_GZIP_FOR_REQUEST_HEADER_TO_REPLACE,
            GzipMarker.ACCEPT_GZIP_ENCODING_FOR_RESPONSES,
            AuthorizedNetworkApiCreator.AUTHORIZATION_HEADER_TO_REPLACE
    )
    fun updateLocalSynced(
            @Body requests: List<UpdateLocalSyncedRequest>
    ): Single<List<UpdateLocalNonModifiedApiResponse>>

    @POST("getCreatedAfter")
    @Headers(
            GzipMarker.USE_GZIP_FOR_REQUEST_HEADER_TO_REPLACE,
            GzipMarker.ACCEPT_GZIP_ENCODING_FOR_RESPONSES,
            AuthorizedNetworkApiCreator.AUTHORIZATION_HEADER_TO_REPLACE
    )
    fun getNewRemoteCreated(
            @Body request: GetNewRemoteCreatedRequest
    ): Single<List<RemoteApiGame>>
}