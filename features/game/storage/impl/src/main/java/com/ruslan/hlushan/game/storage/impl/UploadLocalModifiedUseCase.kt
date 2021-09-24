package com.ruslan.hlushan.game.storage.impl

import androidx.annotation.IntRange
import androidx.annotation.VisibleForTesting
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.LocalAction
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.play.dto.userLocallyDeleted
import com.ruslan.hlushan.game.api.play.dto.userModifiedRecord
import com.ruslan.hlushan.game.api.play.dto.userModifiedRecordAndStartedModifyingAgain
import com.ruslan.hlushan.game.api.play.dto.userStartedModifying
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepository
import com.ruslan.hlushan.game.storage.impl.remote.SyncRemoteRepository
import com.ruslan.hlushan.game.storage.impl.remote.dto.LocalModifiedResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.UploadLocalModifiedRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.toSyncLocalUpdateRequest
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.UUID
import javax.inject.Inject

@SuppressWarnings("MaxLineLength")
internal class UploadLocalModifiedUseCase
@Inject
constructor(
        private val remoteSyncRepository: SyncRemoteRepository,
        private val localRepository: LocalRecordsRepository,
        private val appLogger: AppLogger
) {

    fun uploadAll(@IntRange(from = 1) stepCount: Int): Completable =
            localRepository.markAsSyncingAndGetWaitingRecords(stepCount, this::generateCreateId)
                    .flatMap { list ->
                        if (list.isNotEmpty()) {
                            uploadLocalModifiedRecords(list)
                        } else {
                            Single.just(SyncStepResult(allPreviousFailed = false, previousRecordsCount = 0))
                        }
                    }
                    .doOnError { error -> appLogger.log(this, "uploadAll stepCount = $stepCount", error) }
                    .repeatWhileSyncStepResultValid(minSize = 1)

    private fun uploadLocalModifiedRecords(localModifiedRecords: List<GameRecordWithSyncState>): Single<SyncStepResult> =
            makeRequestForLocalModifiedRecords(localModifiedRecords)
                    .flatMapObservable { pairs -> Observable.fromIterable(pairs) }
                    .concatMapSingle { (original, response) -> handleLocalModifiedResponse(original, response) }
                    .toList()
                    .map { sucesses ->
                        SyncStepResult(
                                allPreviousFailed = sucesses.all { wasSuccess -> !wasSuccess },
                                previousRecordsCount = localModifiedRecords.size
                        )
                    }
                    .doOnDispose { localRepository.updateSyncStatusOnSyncCloseAsynchronously(localModifiedRecords.map { rec -> rec.record.id }, appLogger) }
                    .doOnError { error -> appLogger.log(this, "uploadLocalModifiedRecords localModifiedRecords = $localModifiedRecords", error) }

    private fun makeRequestForLocalModifiedRecords(localModifiedRecords: List<GameRecordWithSyncState>): Single<List<Pair<GameRecordWithSyncState, LocalModifiedResponse>>> =
            Single.just(localModifiedRecords)
                    .map { list ->
                        val requests = list.map { rec -> rec to rec.syncState }
                                .map { (modifiedRecord, syncState) ->
                                    @Suppress("UnsafeCallOnNullableType")
                                    when (syncState.localAction!!) {
                                        is LocalAction.Create -> {
                                            UploadLocalModifiedRequest.Created(
                                                    record = modifiedRecord.record,
                                                    localCreateId = syncState.localCreateId!!,
                                                    lastLocalModifiedTimestamp = syncState.lastLocalModifiedTimestamp
                                            )
                                        }
                                        is LocalAction.Update -> {
                                            UploadLocalModifiedRequest.Updated(
                                                    record = modifiedRecord.record,
                                                    remoteId = syncState.remoteInfo!!.remoteId,
                                                    remoteActionId = syncState.remoteInfo!!.remoteActionId,
                                                    lastLocalModifiedTimestamp = syncState.lastLocalModifiedTimestamp
                                            )
                                        }
                                        is LocalAction.Delete -> {
                                            UploadLocalModifiedRequest.Deleted(
                                                    localRecordId = modifiedRecord.record.id,
                                                    remoteId = syncState.remoteInfo!!.remoteId,
                                                    remoteActionId = syncState.remoteInfo!!.remoteActionId
                                            )
                                        }
                                    }
                                }
                        list to requests
                    }
                    .flatMap { (originals, requests) ->
                        remoteSyncRepository.uploadLocalModified(requests)
                                .onErrorReturn { originals.map { orig -> LocalModifiedResponse.Fail(orig.record.id) } }
                                .map { responses ->
                                    originals.map { orig ->
                                        val resp = (responses.firstOrNull { r -> orig.record.id == r.id }
                                                    ?: LocalModifiedResponse.Fail(orig.record.id))
                                        orig to resp
                                    }
                                }
                    }

    @VisibleForTesting
    fun handleLocalModifiedResponse(original: GameRecordWithSyncState, response: LocalModifiedResponse): Single<Boolean> =
            localRepository.getFullRecordData(original.record.id)
                    .map(GameRecordWithSyncState::syncState)
                    .map { actualFromDb -> Triple(original, actualFromDb, response) }
                    .flatMap { (original, actualFromDb, response) ->
                        (when (response) {
                            is LocalModifiedResponse.Create -> updateLocalRecordAfterRemoteCreate(original, actualFromDb, response)
                            is LocalModifiedResponse.Update -> updateLocalRecordAfterRemoteUpdate(original, actualFromDb, response)
                            is LocalModifiedResponse.Delete -> updateLocalRecordAfterRemoteDelete(original, actualFromDb, response)
                            is LocalModifiedResponse.Fail   -> localRepository.updateSyncStatusOnSyncFail(original.record.id, actualFromDb)
                        }).toSingle { response !is LocalModifiedResponse.Fail }
                    }

    private fun updateLocalRecordAfterRemoteCreate(
            original: GameRecordWithSyncState,
            actualFromDb: RecordSyncState,
            response: LocalModifiedResponse.Create
    ): Completable =
            when (response) {
                is LocalModifiedResponse.Create.Success    -> updateLocalRecordAfterRemoteCreateSuccess(original, actualFromDb, response)
                is LocalModifiedResponse.Create.WasChanged -> updateLocalRecordAfterRemoteCreateWasChanged(original, actualFromDb, response)
            }

    private fun updateLocalRecordAfterRemoteUpdate(
            original: GameRecordWithSyncState,
            actualFromDb: RecordSyncState,
            response: LocalModifiedResponse.Update
    ): Completable =
            when {
                (original.syncState.userLocallyDeleted(actualFromDb))
                || (original.syncState.userModifiedRecord(actualFromDb))
                || (original.syncState.userModifiedRecordAndStartedModifyingAgain(actualFromDb)) -> {
                    localRepository.updateLocalStateWithNew(
                            id = original.record.id,
                            original = actualFromDb,
                            remoteInfo = response.remoteInfo,
                            syncStatus = SyncStatus.WAITING
                    )
                }
                (original.syncState == actualFromDb)
                || (original.syncState.userStartedModifying(actualFromDb))                       -> {
                    localRepository.updateRecordSyncState(original.record.id,
                                                          RecordSyncState.forSync(
                                                                  remoteInfo = response.remoteInfo,
                                                                  lastLocalModifiedTimestamp = actualFromDb.lastLocalModifiedTimestamp,
                                                                  modifyingNow = actualFromDb.modifyingNow
                                                          ))
                }
                else                                                                             -> Completable.error(UnexpectedAfterUpdateException(original, actualFromDb))
            }.doOnError { error ->  appLogger.logErrorFor(this, original, actualFromDb, response, error) }

    private fun updateLocalRecordAfterRemoteDelete(
            original: GameRecordWithSyncState,
            actualFromDb: RecordSyncState,
            response: LocalModifiedResponse.Delete
    ): Completable =
            @Suppress("MandatoryBracesIfStatements")
            if (original.syncState != actualFromDb) {
                Completable.error(UnexpectedAfterUpdateException(original, actualFromDb))
            } else when (response) {
                is LocalModifiedResponse.Delete.Success    -> localRepository.removeRecordById(original.record.id)
                is LocalModifiedResponse.Delete.WasChanged -> {
                    localRepository.updateRecord(original.record.id, response.remoteRecord.toSyncLocalUpdateRequest(modifyingNow = false))
                }
            }.doOnError { error ->  appLogger.logErrorFor(this, original, actualFromDb, response, error) }

    private fun updateLocalRecordAfterRemoteCreateSuccess(
            original: GameRecordWithSyncState,
            actualFromDb: RecordSyncState,
            response: LocalModifiedResponse.Create.Success
    ): Completable =
            when {
                (original.syncState == actualFromDb)
                || (original.syncState.userStartedModifying(actualFromDb))                       -> {
                    localRepository.updateRecordSyncState(original.record.id,
                                                          RecordSyncState.forSync(
                                                                  remoteInfo = response.remoteInfo,
                                                                  lastLocalModifiedTimestamp = actualFromDb.lastLocalModifiedTimestamp,
                                                                  modifyingNow = actualFromDb.modifyingNow
                                                          ))
                }
                (original.syncState.userLocallyDeleted(actualFromDb))                            -> {
                    localRepository.updateLocalStateWithNew(
                            id = original.record.id,
                            original = actualFromDb,
                            remoteInfo = response.remoteInfo,
                            localCreateId = null,
                            syncStatus = SyncStatus.WAITING
                    )
                }
                (original.syncState.userModifiedRecord(actualFromDb))
                || (original.syncState.userModifiedRecordAndStartedModifyingAgain(actualFromDb)) -> {
                    localRepository.updateLocalStateWithNew(
                            id = original.record.id,
                            original = actualFromDb,
                            remoteInfo = response.remoteInfo,
                            localAction = LocalAction.Update(actionId = actualFromDb.localAction!!.actionId),
                            localCreateId = null,
                            syncStatus = SyncStatus.WAITING
                    )
                }
                else                                                                             -> Completable.error(UnexpectedAfterUpdateException(original, actualFromDb))
            }.doOnError { error ->  appLogger.logErrorFor(this, original, actualFromDb, response, error) }

    private fun updateLocalRecordAfterRemoteCreateWasChanged(
            original: GameRecordWithSyncState,
            actualFromDb: RecordSyncState,
            response: LocalModifiedResponse.Create.WasChanged
    ): Completable =
            when {
                (original.syncState == actualFromDb)
                || (original.syncState.userLocallyDeleted(actualFromDb))                         -> {
                    localRepository.updateRecord(original.record.id, response.remoteRecord.toSyncLocalUpdateRequest(modifyingNow = false))
                }
                (original.syncState.userStartedModifying(actualFromDb))
                || (original.syncState.userModifiedRecord(actualFromDb))
                || (original.syncState.userModifiedRecordAndStartedModifyingAgain(actualFromDb)) -> {
                    localRepository.addNewAndUpdateSyncState(
                            addRequest = response.remoteRecord.toSyncLocalUpdateRequest(modifyingNow = false),
                            updateId = original.record.id,
                            updateSyncState = actualFromDb.copy(localCreateId = null, syncStatus = SyncStatus.WAITING)
                    )
                }
                else                                                                             -> Completable.error(UnexpectedAfterUpdateException(original, actualFromDb))
            }.doOnError { error ->  appLogger.logErrorFor(this, original, actualFromDb, response, error) }

    private fun generateCreateId(gameRecord: GameRecord): String =
            "${UUID.randomUUID()}" +
            "${UUID.randomUUID()}" +
            "_${gameRecord.gameState.current.immutableNumbersMatrix.totalSum}" +
            "_${gameRecord.totalPlayed.seconds}" +
            "_${gameRecord.gameState.current.immutableNumbersMatrix.gameSize.countRowsAndColumns}" +
            "_${gameRecord.gameState.current.immutableNumbersMatrix.numbers.count { number -> number != null }}" +
            "_${gameRecord.gameState.current.newItems.sum()}" +
            "_${gameRecord.gameState.stack.size}"
}