package com.ruslan.hlushan.game.storage.impl

import android.annotation.SuppressLint
import com.ruslan.hlushan.core.api.dto.ValueHolder
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.pagination.api.PaginationPagesRequest
import com.ruslan.hlushan.core.pagination.api.PaginationResponse
import com.ruslan.hlushan.game.api.play.PlayRecordsInteractor
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.GameState
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RequestParams
import com.ruslan.hlushan.game.api.play.dto.canBeFullyDeletedOnLocalDelete
import com.ruslan.hlushan.game.api.play.dto.toLocalDeletedOrThrow
import com.ruslan.hlushan.game.api.play.dto.toModifyingNowOrThrow
import com.ruslan.hlushan.game.api.play.dto.toNextModifiedAfterModifyingOrThrow
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepository
import com.ruslan.hlushan.game.storage.impl.local.LocalUpdateRequest
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import javax.inject.Inject

internal class PlayRecordsInteractorImpl
@Inject
constructor(
        private val localRepository: LocalRecordsRepository,
        private val appLogger: AppLogger
) : PlayRecordsInteractor {

    override fun getAvailableRecords(
            pagesRequest: PaginationPagesRequest<RequestParams>,
            filter: GameRecordWithSyncState.Order.Params,
            limit: Int
    ): Single<PaginationResponse<GameRecordWithSyncState, RequestParams>> =
            localRepository.getAvailableRecords(pagesRequest, filter, limit)

    override fun observeGameRecord(id: Long): Observable<ValueHolder<GameRecordWithSyncState?>> =
            localRepository.observeGameRecord(id)

    override fun updateAndGetRecordForPlaying(id: Long): Single<GameRecord> =
            localRepository.getFullRecordData(id)
                    .map { recordWithSyncState ->
                        recordWithSyncState.record to recordWithSyncState.syncState.toModifyingNowOrThrow()
                    }
                    .flatMap { (record, newSyncState) ->
                        localRepository.updateRecordSyncState(record.id, newSyncState)
                                .toSingle { record }
                    }
                    .doOnError { error ->
                        appLogger.log(this, "updateAndGetRecordForPlaying id = $id", error)
                    }

    override fun markAsNonPlaying(id: Long): Completable =
            localRepository.setPlayingById(id = id, playing = false)

    @SuppressLint("CheckResult")
    override fun markAsNonPlayingAsynchronously(id: Long) {
        markAsNonPlaying(id = id)
                .subscribe(
                        {
                            appLogger.log(
                                    this,
                                    message = "markAsNonPlayingAsynchronously id = $id: Success"
                            )
                        },
                        { error ->
                            appLogger.log(
                                    this,
                                    message = "markAsNonPlayingAsynchronously id = $id: Error",
                                    error = error
                            )
                        }
                )
    }

    override fun removeRecordById(id: Long): Completable =
            localRepository.getFullRecordData(id)
                    .map { recordWithSyncState -> recordWithSyncState.record.id to recordWithSyncState.syncState }
                    .flatMapCompletable { (id, syncState) ->
                        if (syncState.canBeFullyDeletedOnLocalDelete()) {
                            localRepository.removeRecordById(id)
                        } else {
                            Single.fromCallable { syncState.toLocalDeletedOrThrow(generateLocalActionId()) }
                                    .flatMapCompletable { newSyncState ->
                                        localRepository.updateRecordSyncState(id, newSyncState)
                                    }
                        }
                    }
                    .doOnError { error -> appLogger.log(this, "removeRecordById id = $id", error) }

    override fun updateRecordAfterPlaying(
            id: Long,
            gameState: GameState,
            totalPlayed: Duration,
            localModifiedTimestamp: Instant
    ): Completable =
            localRepository.getFullRecordData(id)
                    .map { recordWithSyncState ->
                        recordWithSyncState.syncState.toNextModifiedAfterModifyingOrThrow(
                                newLocalActionId = generateLocalActionId(),
                                newLastLocalModifiedTimestamp = localModifiedTimestamp
                        )
                    }
                    .map { newSyncState -> LocalUpdateRequest(gameState, totalPlayed, newSyncState) }
                    .flatMapCompletable { request -> localRepository.updateRecord(id, request) }
                    .doOnError { error ->
                        appLogger.log(
                                this, "updateRecordAfterPlaying id = $id," +
                                      " gameState = $gameState," +
                                      " totalPlayed = $totalPlayed",
                                error
                        )
                    }

    override fun addNewRecordAfterPlaying(
            gameState: GameState,
            totalPlayed: Duration,
            localCreatedTimestamp: Instant
    ): Completable =
            Single.fromCallable {
                RecordSyncState.forLocalCreated(
                        localActionId = generateLocalActionId(),
                        modifyingNow = false,
                        localCreatedTimestamp = localCreatedTimestamp
                )
            }
                    .map { syncState -> LocalUpdateRequest(gameState, totalPlayed, syncState) }
                    .flatMapCompletable { request -> localRepository.addNewRecord(request) }
                    .doOnError { error ->
                        appLogger.log(
                                this, "updateRecordAfterPlaying  gameState = $gameState," +
                                      " totalPlayed = $totalPlayed",
                                error
                        )
                    }

    override fun getCountOfNotSynchronizedRecords(): Single<Int> =
            localRepository.getCountOfNotSynchronizedRecords()
}