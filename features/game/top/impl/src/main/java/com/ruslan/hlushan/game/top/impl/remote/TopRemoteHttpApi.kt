package com.ruslan.hlushan.game.top.impl.remote

import com.ruslan.hlushan.game.top.impl.remote.dto.RemoteApiGamePreviewWithUserDetails
import com.ruslan.hlushan.network.api.GzipMarker
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

internal interface TopRemoteHttpApi {

    @GET("getBestGames")
    @Headers(GzipMarker.ACCEPT_GZIP_ENCODING_FOR_RESPONSES)
    fun getBestGames(
            @Query("countRowsAndColumns") countRowsAndColumns: Int
    ): Single<List<RemoteApiGamePreviewWithUserDetails>>
}