package com.ruslan.hlushan.game.storage.impl

import androidx.annotation.IntRange
import androidx.annotation.VisibleForTesting
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.play.dto.userLocallyDeleted
import com.ruslan.hlushan.game.api.play.dto.userModifiedRecord
import com.ruslan.hlushan.game.api.play.dto.userModifiedRecordAndStartedModifyingAgain
import com.ruslan.hlushan.game.api.play.dto.userStartedModifying
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepository
import com.ruslan.hlushan.game.storage.impl.remote.SyncRemoteRepository
import com.ruslan.hlushan.game.storage.impl.remote.dto.UpdateLocalNonModifiedResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.toUpdateLocalSyncedRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.toSyncLocalUpdateRequest
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.Instant
import javax.inject.Inject

/**
 * @author Ruslan Hlushan on 2019-05-22
 */
@SuppressWarnings("MaxLineLength")
internal class UpdateLocalSyncedUseCase
@Inject
constructor(
        private val remoteSyncRepository: SyncRemoteRepository,
        private val localRepository: LocalRecordsRepository,
        private val appLogger: AppLogger
) {

    fun updateAll(maxLastRemoteSyncedTimestamp: Instant, @IntRange(from = 1) stepCount: Int): Completable =
            localRepository.markAsSyncingAndGetSyncedWhereLastRemoteSyncSmallerThen(maxLastRemoteSyncedTimestamp, stepCount)
                    .flatMap { list ->
                        if (list.isNotEmpty()) {
                            uploadLocalSyncedRecords(list)
                        } else {
                            Single.just(SyncStepResult(allPreviousFailed = false, previousRecordsCount = 0))
                        }
                    }
                    .doOnError { error ->
                        appLogger.log(this, "updateAll maxLastRemoteSyncedTimestamp = $maxLastRemoteSyncedTimestamp, stepCount = $stepCount", error)
                    }
                    .repeatWhileSyncStepResultValid(minSize = 1)

    private fun uploadLocalSyncedRecords(localSyncedRecords: List<GameRecordWithSyncState>): Single<SyncStepResult> =
            makeRequestForUpdateLocalNonModifiedRecords(localSyncedRecords)
                    .flatMapObservable { pairs -> Observable.fromIterable(pairs) }
                    .concatMapSingle { (original, response) -> handleResponse(original, response) }
                    .toList()
                    .map { sucesses ->
                        SyncStepResult(
                                allPreviousFailed = sucesses.all { wasSuccess -> !wasSuccess },
                                previousRecordsCount = localSyncedRecords.size
                        )
                    }
                    .doOnDispose { localRepository.updateSyncStatusOnSyncCloseAsynchronously(localSyncedRecords.map { rec -> rec.record.id }, appLogger) }
                    .doOnError { error -> appLogger.log(this, "uploadLocalSyncedRecords localSyncedRecords = $localSyncedRecords", error) }

    @Suppress("UnsafeCallOnNullableType")
    private fun makeRequestForUpdateLocalNonModifiedRecords(
            localSyncedRecords: List<GameRecordWithSyncState>
    ): Single<List<Pair<GameRecordWithSyncState, UpdateLocalNonModifiedResponse>>> =
            Single.just(localSyncedRecords)
                    .map { list ->
                        val requests = list.map { rec -> rec.syncState.remoteInfo!!.toUpdateLocalSyncedRequest() }
                        list to requests
                    }
                    .flatMap { (originals, requests) ->
                        remoteSyncRepository.updateLocalSynced(requests)
                                .onErrorReturn { originals.map { orig -> UpdateLocalNonModifiedResponse.Fail(orig.syncState.remoteInfo!!.remoteId) } }
                                .map { responses ->
                                    originals.map { orig ->
                                        val resp = (responses.firstOrNull { r -> orig.syncState.remoteInfo?.remoteId == r.remoteId }
                                                    ?: UpdateLocalNonModifiedResponse.Fail(orig.syncState.remoteInfo!!.remoteId))//TODO or NonChanged
                                        orig to resp
                                    }
                                }
                    }

    @VisibleForTesting
    fun handleResponse(original: GameRecordWithSyncState, response: UpdateLocalNonModifiedResponse): Single<Boolean> =
            localRepository.getFullRecordData(original.record.id)
                    .map(GameRecordWithSyncState::syncState)
                    .map { actualFromDb -> Triple(original, actualFromDb, response) }
                    .flatMap { (original, actualFromDb, response) ->
                        (when (response) {
                            is UpdateLocalNonModifiedResponse.NoChanges -> updateLocalRecordAfterRemoteUpdateNonChanged(original, actualFromDb, response)
                            is UpdateLocalNonModifiedResponse.Changed   -> updateLocalRecordAfterRemoteUpdateChanged(original, actualFromDb, response)
                            is UpdateLocalNonModifiedResponse.Deleted   -> updateLocalRecordAfterRemoteUpdateDeleted(original, actualFromDb, response)
                            is UpdateLocalNonModifiedResponse.Fail      -> localRepository.updateSyncStatusOnSyncFail(original.record.id, actualFromDb)
                        }).toSingle { response !is UpdateLocalNonModifiedResponse.Fail }
                    }

    private fun updateLocalRecordAfterRemoteUpdateNonChanged(
            original: GameRecordWithSyncState,
            actualFromDb: RecordSyncState,
            response: UpdateLocalNonModifiedResponse.NoChanges
    ): Completable =
            when {
                (original.syncState == actualFromDb)
                || (original.syncState.userStartedModifying(actualFromDb))                       -> {
                    localRepository.updateLocalStateWithNew(
                            id = original.record.id,
                            original = actualFromDb,
                            syncStatus = SyncStatus.SYNCED,
                            remoteInfo = actualFromDb.remoteInfo?.copy(lastRemoteSyncedTimestamp = response.lastRemoteSyncedTimestamp)
                    )
                }
                (original.syncState.userLocallyDeleted(actualFromDb))
                || (original.syncState.userModifiedRecord(actualFromDb))
                || (original.syncState.userModifiedRecordAndStartedModifyingAgain(actualFromDb)) -> {
                    localRepository.updateLocalStateWithNew(
                            id = original.record.id,
                            original = actualFromDb,
                            remoteInfo = actualFromDb.remoteInfo?.copy(lastRemoteSyncedTimestamp = response.lastRemoteSyncedTimestamp),
                            syncStatus = SyncStatus.WAITING
                    )
                }
                else                                                                             -> {
                    Completable.error(UnexpectedAfterUpdateException(original, actualFromDb))
                }
            }.doOnError { error -> appLogger.logErrorFor(this, original, actualFromDb, response, error) }

    private fun updateLocalRecordAfterRemoteUpdateChanged(
            original: GameRecordWithSyncState,
            actualFromDb: RecordSyncState,
            response: UpdateLocalNonModifiedResponse.Changed
    ): Completable =
            when {
                (original.syncState == actualFromDb)
                || (original.syncState.userLocallyDeleted(actualFromDb))                         -> {
                    localRepository.updateRecord(original.record.id, response.remoteRecord.toSyncLocalUpdateRequest(modifyingNow = false))
                }
                (original.syncState.userStartedModifying(actualFromDb))                          -> {
                    localRepository.addNewAndUpdateSyncState(
                            addRequest = response.remoteRecord.toSyncLocalUpdateRequest(modifyingNow = false),
                            updateId = original.record.id,
                            updateSyncState = RecordSyncState.forLocalCreated(
                                    localActionId = generateLocalActionId(),
                                    modifyingNow = true,
                                    localCreatedTimestamp = actualFromDb.lastLocalModifiedTimestamp
                            ))
                }
                (original.syncState.userModifiedRecord(actualFromDb))
                || (original.syncState.userModifiedRecordAndStartedModifyingAgain(actualFromDb)) -> {
                    @Suppress("UnsafeCallOnNullableType")
                    localRepository.addNewAndUpdateSyncState(
                            addRequest = response.remoteRecord.toSyncLocalUpdateRequest(modifyingNow = false),
                            updateId = original.record.id,
                            updateSyncState = RecordSyncState.forLocalCreated(
                                    localActionId = actualFromDb.localAction!!.actionId,
                                    modifyingNow = actualFromDb.modifyingNow,
                                    localCreatedTimestamp = actualFromDb.lastLocalModifiedTimestamp
                            ))
                }
                else                                                                             -> {
                    Completable.error(UnexpectedAfterUpdateException(original, actualFromDb))
                }
            }.doOnError { error -> appLogger.logErrorFor(this, original, actualFromDb, response, error) }

    private fun updateLocalRecordAfterRemoteUpdateDeleted(
            original: GameRecordWithSyncState,
            actualFromDb: RecordSyncState,
            response: UpdateLocalNonModifiedResponse.Deleted
    ): Completable =
            when {
                (original.syncState == actualFromDb)
                || (original.syncState.userLocallyDeleted(actualFromDb))                         -> {
                    localRepository.removeRecordById(original.record.id)
                }
                (original.syncState.userStartedModifying(actualFromDb))                          -> {
                    localRepository.updateRecordSyncState(original.record.id,
                                                          RecordSyncState.forLocalCreated(
                                                                  localActionId = generateLocalActionId(),
                                                                  modifyingNow = true,
                                                                  localCreatedTimestamp = actualFromDb.lastLocalModifiedTimestamp
                                                          ))
                }
                (original.syncState.userModifiedRecord(actualFromDb))
                || (original.syncState.userModifiedRecordAndStartedModifyingAgain(actualFromDb)) -> {
                    @Suppress("UnsafeCallOnNullableType")
                    localRepository.updateRecordSyncState(original.record.id,
                                                          RecordSyncState.forLocalCreated(
                                                                  localActionId = actualFromDb.localAction!!.actionId,
                                                                  modifyingNow = actualFromDb.modifyingNow,
                                                                  localCreatedTimestamp = actualFromDb.lastLocalModifiedTimestamp
                                                          ))
                }
                else                                                                             -> {
                    Completable.error(UnexpectedAfterUpdateException(original, actualFromDb))
                }
            }.doOnError { error ->  appLogger.logErrorFor(this, original, actualFromDb, response, error) }
}