package com.ruslan.hlushan.game.core.api.play.dto

import org.threeten.bp.Instant

data class RecordSyncState(
        val remoteInfo: RemoteInfo?,
        val localAction: LocalAction?,
        val localCreateId: String?,
        val lastLocalModifiedTimestamp: Instant,
        val modifyingNow: Boolean,
        val syncStatus: SyncStatus
) {

    @SuppressWarnings("ClassOrdering")
    companion object {

        fun forLocalCreated(
                localActionId: String,
                modifyingNow: Boolean,
                localCreatedTimestamp: Instant
        ): RecordSyncState =
                RecordSyncState(
                        remoteInfo = null,
                        localAction = LocalAction.Create(actionId = localActionId),
                        lastLocalModifiedTimestamp = localCreatedTimestamp,
                        localCreateId = null,
                        modifyingNow = modifyingNow,
                        syncStatus = SyncStatus.WAITING
                )

        fun forSync(
                remoteInfo: RemoteInfo,
                lastLocalModifiedTimestamp: Instant,
                modifyingNow: Boolean
        ): RecordSyncState =
                RecordSyncState(
                        remoteInfo = remoteInfo,
                        localAction = null,
                        localCreateId = null,
                        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp,
                        modifyingNow = modifyingNow,
                        syncStatus = SyncStatus.SYNCED
                )
    }

    init {
        if ((this.localAction is LocalAction.Create) && (this.remoteInfo != null)) {
            throw IllegalCreateStatusAndRemoteActionsException(this.localAction, this.remoteInfo)
        }

        if (((this.localAction == null) || (this.localAction is LocalAction.Update))
            && (this.localCreateId != null)) {
            throw IllegalLocalCreatedIdValueException(this.localAction, this.localCreateId)
        }

        @SuppressWarnings("ComplexCondition", "MaxLineLength")
        if ((((this.localAction is LocalAction.Create) && (this.syncStatus == SyncStatus.SYNCHRONIZING))
             || ((this.localAction is LocalAction.Delete) && (this.remoteInfo == null) && (this.syncStatus == SyncStatus.WAITING)))
            && (this.localCreateId == null)) {
            throw IllegalCreateStatusAndLocalCreateIdException(this.localAction, this.localCreateId, this.syncStatus)
        }

        if ((this.localAction is LocalAction.Update) && (this.remoteInfo == null)) {
            throw IllegalUpdateStateException()
        }

        if ((this.localAction is LocalAction.Delete) && this.modifyingNow) {
            throw IllegalDeleteStateException()
        }

        @SuppressWarnings("ComplexCondition")
        if (((this.localAction != null) && (this.syncStatus == SyncStatus.SYNCED))
            || ((this.localAction == null) && (this.syncStatus == SyncStatus.WAITING))) {
            throw IllegalSyncStatusException(this.localAction, this.syncStatus)
        }
    }
}

@SuppressWarnings("ComplexMethod")
fun RecordSyncState.userLocallyDeleted(actualStateFromDb: RecordSyncState): Boolean =
        (this.remoteInfo == actualStateFromDb.remoteInfo
         && actualStateFromDb.localAction is LocalAction.Delete
         && this.localAction?.actionId != actualStateFromDb.localAction.actionId
         && this.localCreateId == actualStateFromDb.localCreateId
         && !this.modifyingNow && !actualStateFromDb.modifyingNow
         && (if (this.syncStatus == SyncStatus.SYNCED) {
            (actualStateFromDb.syncStatus == SyncStatus.WAITING)
        } else {
            (this.syncStatus == actualStateFromDb.syncStatus)
        }))

@SuppressWarnings("ComplexMethod")
fun RecordSyncState.userStartedModifying(actualStateFromDb: RecordSyncState): Boolean =
        (this.remoteInfo == actualStateFromDb.remoteInfo
         && this.localAction == actualStateFromDb.localAction
         && this.localCreateId == actualStateFromDb.localCreateId
         && !this.modifyingNow && actualStateFromDb.modifyingNow
         && this.syncStatus == actualStateFromDb.syncStatus)

@SuppressWarnings("ComplexMethod")
fun RecordSyncState.userModifiedRecord(actualStateFromDb: RecordSyncState): Boolean =
        (this.remoteInfo == actualStateFromDb.remoteInfo
         && (if (this.localAction == null) {
            (actualStateFromDb.localAction is LocalAction.Update)
        } else {
            (this.localAction.isSameType(actualStateFromDb.localAction))
        })
         && this.localAction?.actionId != actualStateFromDb.localAction?.actionId
         && this.localCreateId == actualStateFromDb.localCreateId
         && !this.modifyingNow && !actualStateFromDb.modifyingNow
         && (if (this.syncStatus == SyncStatus.SYNCED) {
            (actualStateFromDb.syncStatus == SyncStatus.WAITING)
        } else {
            (this.syncStatus == actualStateFromDb.syncStatus)
        }))

@SuppressWarnings("ComplexMethod")
fun RecordSyncState.userModifiedRecordAndStartedModifyingAgain(actualStateFromDb: RecordSyncState): Boolean =
        (this.remoteInfo == actualStateFromDb.remoteInfo
         && (if (this.localAction == null) {
            (actualStateFromDb.localAction is LocalAction.Update)
        } else {
            (this.localAction.isSameType(actualStateFromDb.localAction))
        })
         && this.localAction?.actionId != actualStateFromDb.localAction?.actionId
         && this.localCreateId == actualStateFromDb.localCreateId
         && !this.modifyingNow && actualStateFromDb.modifyingNow
         && (if (this.syncStatus == SyncStatus.SYNCED) {
            (actualStateFromDb.syncStatus == SyncStatus.WAITING)
        } else {
            (this.syncStatus == actualStateFromDb.syncStatus)
        }))

@Throws(IllegalStateException::class)
fun RecordSyncState.toModifyingNowOrThrow(): RecordSyncState =
        if (this.localAction is LocalAction.Delete) {
            throw IllegalStateException("Record with localAction = ${this.localAction} can not be playing now.")
        } else {
            this.copy(modifyingNow = true)
        }

@Throws(IllegalStateException::class)
fun RecordSyncState.toNextModifiedAfterModifyingOrThrow(
        newLocalActionId: String,
        newLastLocalModifiedTimestamp: Instant
): RecordSyncState {
    if (!this.modifyingNow) {
        throw IllegalStateException("Can't move to next modified state that was is not modifying now: $this.")
    }

    val newLocalAction: LocalAction = when (this.localAction) {
        is LocalAction.Create       -> {
            LocalAction.Create(actionId = newLocalActionId)
        }
        null, is LocalAction.Update -> {
            LocalAction.Update(actionId = newLocalActionId)
        }
        is LocalAction.Delete       -> {
            throw IllegalStateException("Can't move to next modified state that was locally deleted: $this.")
        }
    }

    val newSyncStatus: SyncStatus = when (this.syncStatus) {
        SyncStatus.SYNCED, SyncStatus.WAITING -> SyncStatus.WAITING
        SyncStatus.SYNCHRONIZING              -> SyncStatus.SYNCHRONIZING
    }

    return this.copy(
            localAction = newLocalAction,
            lastLocalModifiedTimestamp = newLastLocalModifiedTimestamp,
            modifyingNow = false,
            syncStatus = newSyncStatus
    )
}

@Suppress("MaxLineLength")
fun RecordSyncState.canBeFullyDeletedOnLocalDelete(): Boolean =
        (!this.modifyingNow
         && (this.syncStatus == SyncStatus.WAITING)
         && ((this.localAction is LocalAction.Create) || ((this.localAction is LocalAction.Delete) && (this.remoteInfo == null))))

@SuppressWarnings("ThrowsCount")
@Throws(IllegalStateException::class)
fun RecordSyncState.toLocalDeletedOrThrow(newLocalActionId: String): RecordSyncState {
    if (this.localAction is LocalAction.Delete) {
        throw IllegalStateException("Record with localAction = ${this.localAction} can't be deleted again.")
    }

    if (this.modifyingNow) {
        throw IllegalStateException("Record witch is modifying now can't be deleted.")
    }

    if ((this.localAction is LocalAction.Create) && (this.syncStatus == SyncStatus.WAITING)) {
        @Suppress("MaxLineLength")
        throw IllegalStateException("Local created record with syncStatus = ${SyncStatus.WAITING} should be fully deleted.")
    }

    val newSyncStatus: SyncStatus = when (this.syncStatus) {
        SyncStatus.SYNCED, SyncStatus.WAITING -> SyncStatus.WAITING
        SyncStatus.SYNCHRONIZING              -> SyncStatus.SYNCHRONIZING
    }

    return this.copy(
            localAction = LocalAction.Delete(actionId = newLocalActionId),
            syncStatus = newSyncStatus
    )
}

//null if delete, same copy if change non needed
@SuppressWarnings("ComplexMethod")
fun RecordSyncState.createNewStateToStoreAfterUnknownSyncResult(): RecordSyncState? =
        when {
            ((this.syncStatus == SyncStatus.SYNCHRONIZING) && (this.localAction == null)) -> {
                this.copy(syncStatus = SyncStatus.SYNCED)
            }
            ((this.syncStatus == SyncStatus.SYNCHRONIZING)
             && !this.modifyingNow
             && ((this.localAction is LocalAction.Delete) && (this.remoteInfo == null)))  -> {
                null
            }
            ((this.syncStatus == SyncStatus.SYNCHRONIZING) && (this.localAction != null)) -> {
                this.copy(syncStatus = SyncStatus.WAITING)
            }
            else                                                                          -> {
                this.copy()
            }
        }