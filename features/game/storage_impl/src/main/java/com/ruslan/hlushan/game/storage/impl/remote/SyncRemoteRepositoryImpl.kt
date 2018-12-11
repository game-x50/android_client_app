package com.ruslan.hlushan.game.storage.impl.remote

import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.game.storage.impl.remote.dto.LocalModifiedResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.RemoteRecord
import com.ruslan.hlushan.game.storage.impl.remote.dto.UpdateLocalNonModifiedResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.UploadLocalModifiedRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.CurrentTimestampResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.GetNewRemoteCreatedRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.LocalModifiedApiResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.RemoteApiGame
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.UpdateLocalNonModifiedApiResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.UpdateLocalSyncedRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.UploadModifiedApiRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.toLocalModifiedResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.toRemoteRecord
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.toUpdateLocalNonModifiedResponse
import io.reactivex.Single
import org.threeten.bp.Instant
import javax.inject.Inject

internal class SyncRemoteRepositoryImpl
@Inject
constructor(
        private val syncRemoteHttpApi: SyncRemoteHttpApi,
        private val schedulersManager: SchedulersManager,
        private val appLogger: AppLogger
) : SyncRemoteRepository {

    override fun getRemoteTimestamp(): Single<Instant> =
            syncRemoteHttpApi.getRemoteTimestamp()
                    .map(CurrentTimestampResponse::nowTimestamp)
                    .doOnError { error -> appLogger.log(this@SyncRemoteRepositoryImpl, "getRemoteTimestamp: ERROR", error) }
                    .subscribeOn(schedulersManager.io)

    override fun uploadLocalModified(requests: List<UploadLocalModifiedRequest>): Single<List<LocalModifiedResponse>> =
            Single.fromCallable { requests.map(UploadModifiedApiRequest::from) }
                    .flatMap { apiRequests -> syncRemoteHttpApi.uploadLocalModified(apiRequests) }
                    .map { list -> list.map(LocalModifiedApiResponse::toLocalModifiedResponse) }
                    .doOnError { error -> appLogger.log(this@SyncRemoteRepositoryImpl, "uploadLocalModified: ERROR: requests = $requests", error) }
                    .subscribeOn(schedulersManager.io)

    override fun updateLocalSynced(requests: List<UpdateLocalSyncedRequest>): Single<List<UpdateLocalNonModifiedResponse>> =
            syncRemoteHttpApi.updateLocalSynced(requests)
                    .map { list -> list.map(UpdateLocalNonModifiedApiResponse::toUpdateLocalNonModifiedResponse) }
                    .doOnError { error -> appLogger.log(this@SyncRemoteRepositoryImpl, "updateLocalSynced: ERROR: requests = $requests", error) }
                    .subscribeOn(schedulersManager.io)

    override fun getNewRemoteCreated(request: GetNewRemoteCreatedRequest): Single<List<RemoteRecord>> =
            syncRemoteHttpApi.getNewRemoteCreated(request)
                    .map { list -> list.map(RemoteApiGame::toRemoteRecord) }
                    .doOnError { error -> appLogger.log(this@SyncRemoteRepositoryImpl, "getNewRemoteCreated: ERROR: request = $request", error) }
                    .subscribeOn(schedulersManager.io)
}