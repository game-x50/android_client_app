package com.ruslan.hlushan.game.storage.impl

import androidx.annotation.IntRange
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepository
import com.ruslan.hlushan.game.storage.impl.remote.SyncRemoteRepository
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.GetNewRemoteCreatedRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.toSyncLocalUpdateRequest
import io.reactivex.Completable
import javax.inject.Inject

@SuppressWarnings("MaxLineLength")
internal class DownloadNewRemoteCreatedUseCase
@Inject
constructor(
        private val remoteSyncRepository: SyncRemoteRepository,
        private val localRepository: LocalRecordsRepository,
        private val appLogger: AppLogger
) {

    fun downloadNew(@IntRange(from = 0) stepLimit: Int): Completable =
            localRepository.getLastCreatedTimestampWithExcludedRemoteIds()
                    .map { (lastCreatedTimestamp, excludedRemoteIds) ->
                        lastCreatedTimestamp.value to excludedRemoteIds.map(RemoteInfo.Id::value)
                    }
                    .map { (lastCreatedTimestamp, excludedRemoteIds) -> GetNewRemoteCreatedRequest(lastCreatedTimestamp, excludedRemoteIds, stepLimit) }
                    .flatMap { request -> remoteSyncRepository.getNewRemoteCreated(request) }
                    .map { newRecords -> newRecords.map { rec -> rec.toSyncLocalUpdateRequest(modifyingNow = false) } }
                    .flatMap { newRecords ->
                        val maxRemoteCreatedTimestamp = newRecords.mapNotNull { rec -> rec.syncState.remoteInfo?.remoteCreatedTimestamp }.maxOrNull()
                        (if (maxRemoteCreatedTimestamp != null && newRecords.isNotEmpty()) {
                            localRepository.addNewRecordsList(newRecords)
                                    .andThen(localRepository.storeLastCreatedTimestamp(maxRemoteCreatedTimestamp))
                        } else {
                            Completable.complete()
                        })
                                .toSingle { SyncStepResult(allPreviousFailed = false, previousRecordsCount = newRecords.size) }
                    }
                    .doOnError { error -> appLogger.log(this, "downloadNew stepLimit = $stepLimit", error) }
                    .onErrorReturn { SyncStepResult(allPreviousFailed = true, previousRecordsCount = 0) }
                    .repeatWhileSyncStepResultValid(minSize = stepLimit)
}