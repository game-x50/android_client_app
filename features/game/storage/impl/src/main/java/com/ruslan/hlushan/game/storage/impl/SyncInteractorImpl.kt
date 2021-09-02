package com.ruslan.hlushan.game.storage.impl

import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepository
import com.ruslan.hlushan.game.storage.impl.local.markSynchronizingFinished
import com.ruslan.hlushan.game.storage.impl.local.markSynchronizingStarted
import com.ruslan.hlushan.game.storage.impl.remote.SyncRemoteRepository
import io.reactivex.Completable
import javax.inject.Inject

private const val UPLOAD_LOCAL_MODIFIED_STEP_COUNT = 10
private const val UPLOAD_LOCAL_SYNCED_STEP_COUNT = 10
private const val DOWNLOAD_REMOTE_CREATED_STEP_COUNT = 10

internal class SyncInteractorImpl
@Inject
constructor(
        private val updaLocalModifiedUseCase: UploadLocalModifiedUseCase,
        private val updateLocalSyncedUseCase: UpdateLocalSyncedUseCase,
        private val downloadNewRemoteCreatedUseCase: DownloadNewRemoteCreatedUseCase,
        private val localRecordsRepository: LocalRecordsRepository,
        private val remoteRepository: SyncRemoteRepository,
        private val appLogger: AppLogger
) : SyncInteractor {

    override fun sync(): Completable =
            Completable.fromAction { localRecordsRepository.markSynchronizingStarted() }
                    .andThen(remoteRepository.getRemoteTimestamp())
                    .flatMap { remoteTimestamp ->
                        updaLocalModifiedUseCase.uploadAll(UPLOAD_LOCAL_MODIFIED_STEP_COUNT)
                                .toSingle { remoteTimestamp }
                    }
                    .flatMapCompletable { startedSyncRemoteTimestamp ->
                        updateLocalSyncedUseCase.updateAll(startedSyncRemoteTimestamp, UPLOAD_LOCAL_SYNCED_STEP_COUNT)
                    }
                    .andThen(downloadNewRemoteCreatedUseCase.downloadNew(DOWNLOAD_REMOTE_CREATED_STEP_COUNT))
                    .doFinally { localRecordsRepository.markSynchronizingFinished() }
                    .doOnComplete { appLogger.log(this, "startIfNotActive: SUCCESS") }
                    .doOnError { error -> appLogger.log(this, "startIfNotActive: ERROR!", error) }
}