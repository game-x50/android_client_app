package com.ruslan.hlushan.game.storage.impl

import android.annotation.SuppressLint
import androidx.annotation.IntRange
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.LocalAction
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepository
import io.reactivex.Completable
import io.reactivex.Single
import org.threeten.bp.Instant
import java.util.UUID

internal data class SyncStepResult(
        val allPreviousFailed: Boolean,
        @IntRange(from = 0) val previousRecordsCount: Int
)

internal data class UnexpectedAfterUpdateException(
        val original: GameRecordWithSyncState,
        val actualFromDb: RecordSyncState
) : Exception()

internal fun Single<SyncStepResult>.repeatWhileSyncStepResultValid(
        @IntRange(from = 0) minSize: Int
): Completable =
        repeat()
                .takeWhile { (allPreviousFailed, previousRecordsCount) ->
                    ((!allPreviousFailed) && (previousRecordsCount >= minSize))
                }
                .ignoreElements()

internal fun generateLocalActionId(): String =
        UUID.randomUUID().toString()

@SuppressWarnings("LongParameterList")
internal fun LocalRecordsRepository.updateLocalStateWithNew(
        id: Long,
        original: RecordSyncState,
        remoteInfo: RemoteInfo? = original.remoteInfo,
        localAction: LocalAction? = original.localAction,
        lastLocalModifiedTimestamp: Instant = original.lastLocalModifiedTimestamp,
        localCreateId: String? = original.localCreateId,
        modifyingNow: Boolean = original.modifyingNow,
        syncStatus: SyncStatus = original.syncStatus
): Completable =
        this.updateRecordSyncState(
                id,
                RecordSyncState(
                        remoteInfo = remoteInfo,
                        localAction = localAction,
                        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp,
                        localCreateId = localCreateId,
                        modifyingNow = modifyingNow,
                        syncStatus = syncStatus
                )
        )

@SuppressLint("CheckResult")
internal fun LocalRecordsRepository.updateSyncStatusOnSyncCloseAsynchronously(ids: List<Long>, appLogger: AppLogger) {
    this.updateSyncStatusOnSyncFail(ids)
            .subscribe({
                           appLogger.log(
                                   this,
                                   message = "updateSyncStatusOnSyncCloseAsynchronously ids = $ids: Success"
                           )
                       },
                       { error ->
                           appLogger.log(
                                   this,
                                   message = "updateSyncStatusOnSyncCloseAsynchronously ids = $ids: Error",
                                   error = error
                           )
                       })
}

internal fun AppLogger.logErrorFor(
        any: Any,
        original: GameRecordWithSyncState,
        actualFromDb: RecordSyncState,
        response: Any,
        error: Throwable
) {
    this.log(any, "updateLocalRecordAfterRemoteUpdate original = $original," +
                  " actualFromDb = $actualFromDb," +
                  " response = $response",
             error)
}