package com.ruslan.hlushan.game.storage.impl.remote

import com.ruslan.hlushan.game.storage.impl.remote.dto.LocalModifiedResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.RemoteRecord
import com.ruslan.hlushan.game.storage.impl.remote.dto.UpdateLocalNonModifiedResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.UploadLocalModifiedRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.GetNewRemoteCreatedRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.UpdateLocalSyncedRequest
import io.reactivex.Single
import org.threeten.bp.Instant

internal interface SyncRemoteRepository {

    fun getRemoteTimestamp(): Single<Instant>

    fun uploadLocalModified(requests: List<UploadLocalModifiedRequest>): Single<List<LocalModifiedResponse>>

    fun updateLocalSynced(requests: List<UpdateLocalSyncedRequest>): Single<List<UpdateLocalNonModifiedResponse>>

    fun getNewRemoteCreated(request: GetNewRemoteCreatedRequest): Single<List<RemoteRecord>>
}