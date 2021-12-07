package play.dto

import com.ruslan.hlushan.game.api.play.dto.IllegalCreateStatusAndLocalCreateIdException
import com.ruslan.hlushan.game.api.play.dto.IllegalCreateStatusAndRemoteActionsException
import com.ruslan.hlushan.game.api.play.dto.IllegalDeleteStateException
import com.ruslan.hlushan.game.api.play.dto.IllegalLocalCreatedIdValueException
import com.ruslan.hlushan.game.api.play.dto.IllegalSyncStatusException
import com.ruslan.hlushan.game.api.play.dto.IllegalUpdateStateException
import com.ruslan.hlushan.game.api.play.dto.LocalAction
import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.api.play.dto.SyncStatus
import com.ruslan.hlushan.game.api.play.dto.canBeFullyDeletedOnLocalDelete
import com.ruslan.hlushan.game.api.play.dto.createNewStateToStoreAfterUnknownSyncResult
import com.ruslan.hlushan.game.api.play.dto.toLocalDeletedOrThrow
import com.ruslan.hlushan.game.api.play.dto.toModifyingNowOrThrow
import com.ruslan.hlushan.game.api.play.dto.toNextModifiedAfterModifyingOrThrow
import com.ruslan.hlushan.game.api.play.dto.userLocallyDeleted
import com.ruslan.hlushan.game.api.play.dto.userModifiedRecord
import com.ruslan.hlushan.game.api.play.dto.userModifiedRecordAndStartedModifyingAgain
import com.ruslan.hlushan.game.api.play.dto.userStartedModifying
import com.ruslan.hlushan.game.api.test.utils.copyWithNewId
import com.ruslan.hlushan.game.api.test.utils.createLocalUpdatedState
import com.ruslan.hlushan.game.api.test.utils.createSyncedState
import com.ruslan.hlushan.game.api.test.utils.createSynchronizingState
import com.ruslan.hlushan.test.utils.assertThrows
import com.ruslan.hlushan.test.utils.generateFakeInstantTimestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.Instant

@SuppressWarnings("LargeClass")
class RecordSyncStateTest {

    @Test(expected = IllegalCreateStatusAndRemoteActionsException::class)
    fun `constructor not valid - if localAction == CREATE, all remote actions should be null`() {
        RecordSyncState(
                remoteInfo = RemoteInfo(
                        remoteId = "",
                        remoteActionId = "",
                        remoteCreatedTimestamp = Instant.now(),
                        lastRemoteSyncedTimestamp = Instant.now()
                ),
                localAction = LocalAction.Create(actionId = ""),
                lastLocalModifiedTimestamp = Instant.now(),
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.WAITING
        )
    }

    @Test(expected = IllegalLocalCreatedIdValueException::class)
    fun `constructor not valid - for synced record localCreateId should be null`() {
        RecordSyncState(
                remoteInfo = RemoteInfo(
                        remoteId = "",
                        remoteActionId = "",
                        remoteCreatedTimestamp = Instant.now(),
                        lastRemoteSyncedTimestamp = Instant.now()
                ),
                localAction = null,
                lastLocalModifiedTimestamp = Instant.now(),
                localCreateId = "",
                modifyingNow = false,
                syncStatus = SyncStatus.SYNCED
        )
    }

    @Test(expected = IllegalLocalCreatedIdValueException::class)
    fun `constructor not valid - for non local created record localCreateId should be null`() {
        RecordSyncState(
                remoteInfo = RemoteInfo(
                        remoteId = "",
                        remoteActionId = "",
                        remoteCreatedTimestamp = Instant.now(),
                        lastRemoteSyncedTimestamp = Instant.now()
                ),
                localAction = LocalAction.Update(actionId = ""),
                lastLocalModifiedTimestamp = Instant.now(),
                localCreateId = "",
                modifyingNow = false,
                syncStatus = SyncStatus.WAITING
        )
    }

    @Suppress("MaxLineLength")
    @Test(expected = IllegalCreateStatusAndLocalCreateIdException::class)
    fun `constructor not valid - for local created record with syncStatus == SINCHRONIZING  localCreateId should be not null`() {
        RecordSyncState(
                remoteInfo = null,
                localAction = LocalAction.Create(actionId = ""),
                lastLocalModifiedTimestamp = Instant.now(),
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.SYNCHRONIZING
        )
    }

    @Test(expected = IllegalCreateStatusAndLocalCreateIdException::class)
    fun `constructor not valid - for local created record and deleted while sync localCreateId should be not null`() {
        RecordSyncState(
                remoteInfo = null,
                localAction = LocalAction.Delete(actionId = ""),
                lastLocalModifiedTimestamp = Instant.now(),
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.WAITING
        )
    }

    @Test(expected = IllegalUpdateStateException::class)
    fun `constructor not valid - for local modified record all remote fields should be not null`() {
        RecordSyncState(
                remoteInfo = null,
                localAction = LocalAction.Update(actionId = ""),
                lastLocalModifiedTimestamp = Instant.now(),
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.WAITING
        )
    }

    @Test(expected = IllegalDeleteStateException::class)
    fun `constructor not valid - local deleted record can't be modified`() {
        RecordSyncState(
                remoteInfo = RemoteInfo(
                        remoteId = "",
                        remoteActionId = "",
                        remoteCreatedTimestamp = Instant.now(),
                        lastRemoteSyncedTimestamp = Instant.now()
                ),
                localAction = LocalAction.Delete(actionId = ""),
                lastLocalModifiedTimestamp = Instant.now(),
                localCreateId = null,
                modifyingNow = true,
                syncStatus = SyncStatus.WAITING
        )
    }

    @Test(expected = IllegalSyncStatusException::class)
    fun `constructor not valid - for local created record syncStatus can't be SYNCED`() {
        RecordSyncState(
                remoteInfo = null,
                localAction = LocalAction.Create(actionId = ""),
                lastLocalModifiedTimestamp = Instant.now(),
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.SYNCED
        )
    }

    @Test(expected = IllegalSyncStatusException::class)
    fun `constructor not valid - for local updated record syncStatus can't be SYNCED`() {
        RecordSyncState(
                remoteInfo = RemoteInfo(
                        remoteId = "",
                        remoteActionId = "",
                        remoteCreatedTimestamp = Instant.now(),
                        lastRemoteSyncedTimestamp = Instant.now()
                ),
                localAction = LocalAction.Update(actionId = ""),
                lastLocalModifiedTimestamp = Instant.now(),
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.SYNCED
        )
    }

    @Test(expected = IllegalSyncStatusException::class)
    fun `constructor not valid - for local deleted record syncStatus can't be SYNCED`() {
        RecordSyncState(
                remoteInfo = RemoteInfo(
                        remoteId = "",
                        remoteActionId = "",
                        remoteCreatedTimestamp = Instant.now(),
                        lastRemoteSyncedTimestamp = Instant.now()
                ),
                localAction = LocalAction.Delete(actionId = ""),
                lastLocalModifiedTimestamp = Instant.now(),
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.SYNCED
        )
    }

    @Test(expected = IllegalSyncStatusException::class)
    fun `constructor not valid - for synced record syncStatus can't be WAITING`() {
        RecordSyncState(
                remoteInfo = RemoteInfo(
                        remoteId = "",
                        remoteActionId = "",
                        remoteCreatedTimestamp = Instant.now(),
                        lastRemoteSyncedTimestamp = Instant.now()
                ),
                localAction = null,
                lastLocalModifiedTimestamp = Instant.now(),
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.WAITING
        )
    }

    @Test
    fun createLocalCreated() {
        val modifyingNow = false
        val localActionId = "1"
        val localCreatedTimestamp = generateFakeInstantTimestamp()

        val localCreatedState = RecordSyncState.forLocalCreated(
                localActionId = localActionId,
                modifyingNow = modifyingNow,
                localCreatedTimestamp = localCreatedTimestamp
        )

        assertEquals(RecordSyncState(
                remoteInfo = null,
                localAction = LocalAction.Create(actionId = localActionId),
                lastLocalModifiedTimestamp = localCreatedTimestamp,
                localCreateId = null,
                modifyingNow = modifyingNow,
                syncStatus = SyncStatus.WAITING
        ),
                     localCreatedState)

        assertOriginalAndChanged(localCreatedState, localCreatedState.copy())

        assertTrue(localCreatedState.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            localCreatedState.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertEquals(localCreatedState.copy(modifyingNow = true), localCreatedState.toModifyingNowOrThrow())

        assertThrows(IllegalStateException::class) {
            localCreatedState.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(localCreatedState.copy(), localCreatedState.createNewStateToStoreAfterUnknownSyncResult())
    }

    @Test
    fun testLocalCreated() {
        val localActionId = "1"
        val localCreatedTimestamp = generateFakeInstantTimestamp()

        val localCreatedState = RecordSyncState.forLocalCreated(
                localActionId = localActionId,
                modifyingNow = false,
                localCreatedTimestamp = localCreatedTimestamp
        )

        assertEquals(RecordSyncState(
                remoteInfo = null,
                localAction = LocalAction.Create(actionId = localActionId),
                lastLocalModifiedTimestamp = localCreatedTimestamp,
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.WAITING
        ),
                     localCreatedState)

        assertOriginalAndChanged(localCreatedState, localCreatedState.copy())
        assertTrue(localCreatedState.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            localCreatedState.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertEquals(localCreatedState.copy(modifyingNow = true), localCreatedState.toModifyingNowOrThrow())

        assertThrows(IllegalStateException::class) {
            localCreatedState.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(localCreatedState.copy(), localCreatedState.createNewStateToStoreAfterUnknownSyncResult())
    }

    @Test
    fun testLocalCreatedStartedModifying() {
        val localActionId1 = "1"
        val lastLocalModifiedTimestamp1 = generateFakeInstantTimestamp()

        val localCreatedStateOriginal = RecordSyncState.forLocalCreated(
                localActionId = localActionId1,
                modifyingNow = false,
                localCreatedTimestamp = lastLocalModifiedTimestamp1
        )

        val localCreatedStartedModifyingState = localCreatedStateOriginal.toModifyingNowOrThrow()

        assertOriginalAndChanged(
                localCreatedStateOriginal,
                localCreatedStartedModifyingState,
                userStartedModifying = true
        )
        assertFalse(localCreatedStartedModifyingState.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            localCreatedStartedModifyingState.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertEquals(
                localCreatedStartedModifyingState.copy(),
                localCreatedStartedModifyingState.toModifyingNowOrThrow()
        )

        val localActionId2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        assertEquals(
                localCreatedStartedModifyingState.copy(
                        localAction = localCreatedStartedModifyingState.localAction?.copyWithNewId(
                                actionId = localActionId2
                        ),
                        modifyingNow = false,
                        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                ),
                localCreatedStartedModifyingState.toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )
        )

        assertEquals(
                localCreatedStartedModifyingState.copy(),
                localCreatedStartedModifyingState.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testLocalCreatedDeleted() {
        val localActionId1 = "1"
        val lastLocalModifiedTimestamp1 = generateFakeInstantTimestamp()

        val localCreatedStateOriginal = RecordSyncState.forLocalCreated(
                localActionId = localActionId1,
                modifyingNow = false,
                localCreatedTimestamp = lastLocalModifiedTimestamp1
        )

        assertThrows(IllegalStateException::class) {
            localCreatedStateOriginal.toLocalDeletedOrThrow("fail")
        }
    }

    @Test
    fun testLocalCreatedModified() {
        val localActionId1 = "1"
        val lastLocalModifiedTimestamp1 = generateFakeInstantTimestamp()

        val localCreatedStateOriginal = RecordSyncState.forLocalCreated(
                localActionId = localActionId1,
                modifyingNow = false,
                localCreatedTimestamp = lastLocalModifiedTimestamp1
        )

        val localActionId2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        val localCreatedModifiedState = localCreatedStateOriginal.toModifyingNowOrThrow()
                .toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )

        assertOriginalAndChanged(localCreatedStateOriginal, localCreatedModifiedState, userModifiedRecord = true)
        assertTrue(localCreatedModifiedState.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            localCreatedModifiedState.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertEquals(
                localCreatedModifiedState.copy(modifyingNow = true),
                localCreatedModifiedState.toModifyingNowOrThrow()
        )

        assertThrows(IllegalStateException::class) {
            localCreatedModifiedState.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(
                localCreatedModifiedState.copy(),
                localCreatedModifiedState.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testLocalCreatedModifiedAndStartedModifyingAgain() {
        val localActionId1 = "1"
        val lastLocalModifiedTimestamp1 = generateFakeInstantTimestamp()

        val localCreatedStateOriginal = RecordSyncState.forLocalCreated(
                localActionId = localActionId1,
                modifyingNow = false,
                localCreatedTimestamp = lastLocalModifiedTimestamp1
        )

        val localActionId2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        val localCreatedModifiedAndStartedModifyingAgainState = localCreatedStateOriginal.toModifyingNowOrThrow()
                .toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )
                .toModifyingNowOrThrow()

        assertOriginalAndChanged(
                localCreatedStateOriginal,
                localCreatedModifiedAndStartedModifyingAgainState,
                userModifiedRecordAndStartedModifyingAgain = true
        )
        assertFalse(localCreatedModifiedAndStartedModifyingAgainState.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            localCreatedModifiedAndStartedModifyingAgainState.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertEquals(
                localCreatedModifiedAndStartedModifyingAgainState.copy(),
                localCreatedModifiedAndStartedModifyingAgainState.toModifyingNowOrThrow()
        )

        val localActionId3 = "3"
        val lastLocalModifiedTimestamp3 = generateFakeInstantTimestamp()

        assertEquals(
                localCreatedModifiedAndStartedModifyingAgainState.copy(
                        localAction = localCreatedModifiedAndStartedModifyingAgainState.localAction?.copyWithNewId(
                                actionId = localActionId3
                        ),
                        modifyingNow = false,
                        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp3
                ),
                localCreatedModifiedAndStartedModifyingAgainState.toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId3,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp3
                )
        )

        assertEquals(
                localCreatedModifiedAndStartedModifyingAgainState.copy(),
                localCreatedModifiedAndStartedModifyingAgainState.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testLocalCreatedSyncing() {
        val localActionId1 = "1"
        val localCreateId = "lC1"
        val lastLocalModifiedTimestamp1 = generateFakeInstantTimestamp()

        val localCreatedSyncingState = RecordSyncState.forLocalCreated(
                localActionId = localActionId1,
                modifyingNow = false,
                localCreatedTimestamp = lastLocalModifiedTimestamp1
        )
                .copy(localCreateId = localCreateId, syncStatus = SyncStatus.SYNCHRONIZING)

        assertOriginalAndChanged(localCreatedSyncingState, localCreatedSyncingState.copy())
        assertFalse(localCreatedSyncingState.canBeFullyDeletedOnLocalDelete())

        val localActionId2 = "2"
        assertEquals(
                localCreatedSyncingState.copy(localAction = LocalAction.Delete(actionId = localActionId2)),
                localCreatedSyncingState.toLocalDeletedOrThrow(newLocalActionId = localActionId2)
        )

        assertEquals(
                localCreatedSyncingState.copy(modifyingNow = true),
                localCreatedSyncingState.toModifyingNowOrThrow()
        )

        assertThrows(IllegalStateException::class) {
            localCreatedSyncingState.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(
                localCreatedSyncingState.copy(syncStatus = SyncStatus.WAITING),
                localCreatedSyncingState.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testLocalCreatedSyncingStartedModifying() {
        val localActionId1 = "1"
        val localCreateId = "lC1"
        val lastLocalModifiedTimestamp1 = generateFakeInstantTimestamp()

        val localCreatedSyncingStateOriginal = RecordSyncState.forLocalCreated(
                localActionId = localActionId1,
                modifyingNow = false,
                localCreatedTimestamp = lastLocalModifiedTimestamp1
        )
                .copy(localCreateId = localCreateId, syncStatus = SyncStatus.SYNCHRONIZING)

        val localCreatedSyncingStartedModifyingState = localCreatedSyncingStateOriginal.toModifyingNowOrThrow()

        assertOriginalAndChanged(
                localCreatedSyncingStateOriginal,
                localCreatedSyncingStartedModifyingState,
                userStartedModifying = true
        )
        assertFalse(localCreatedSyncingStartedModifyingState.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            localCreatedSyncingStartedModifyingState.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertEquals(
                localCreatedSyncingStartedModifyingState.copy(),
                localCreatedSyncingStartedModifyingState.toModifyingNowOrThrow()
        )

        val localActionId2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        assertEquals(
                localCreatedSyncingStartedModifyingState.copy(
                        localAction = localCreatedSyncingStartedModifyingState.localAction?.copyWithNewId(
                                actionId = localActionId2
                        ),
                        modifyingNow = false,
                        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                ),
                localCreatedSyncingStartedModifyingState.toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )
        )

        assertEquals(
                localCreatedSyncingStartedModifyingState.copy(syncStatus = SyncStatus.WAITING),
                localCreatedSyncingStartedModifyingState.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testLocalCreatedSyncingDeleted() {
        val localActionId1 = "1"
        val localCreateId = "lC1"
        val lastLocalModifiedTimestamp1 = generateFakeInstantTimestamp()

        val localCreatedSyncingStateOriginal = RecordSyncState.forLocalCreated(
                localActionId = localActionId1,
                modifyingNow = false,
                localCreatedTimestamp = lastLocalModifiedTimestamp1
        ).copy(localCreateId = localCreateId, syncStatus = SyncStatus.SYNCHRONIZING)

        val localActionId2 = "2"
        val localCreatedSyncingDeletedState = localCreatedSyncingStateOriginal.toLocalDeletedOrThrow(
                newLocalActionId = localActionId2
        )

        assertOriginalAndChanged(
                localCreatedSyncingStateOriginal,
                localCreatedSyncingDeletedState,
                userLocallyDeleted = true
        )

        assertFalse(localCreatedSyncingDeletedState.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            localCreatedSyncingDeletedState.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertThrows(IllegalStateException::class) {
            localCreatedSyncingDeletedState.toModifyingNowOrThrow()
        }

        assertThrows(IllegalStateException::class) {
            localCreatedSyncingDeletedState.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(null, localCreatedSyncingDeletedState.createNewStateToStoreAfterUnknownSyncResult())
    }

    @Test
    fun testLocalCreatedSyncingModified() {
        val localActionId1 = "1"
        val localCreateId = "lC1"
        val lastLocalModifiedTimestamp1 = generateFakeInstantTimestamp()

        val localCreatedSyncingStateOriginal = RecordSyncState.forLocalCreated(
                localActionId = localActionId1,
                modifyingNow = false,
                localCreatedTimestamp = lastLocalModifiedTimestamp1
        )
                .copy(localCreateId = localCreateId, syncStatus = SyncStatus.SYNCHRONIZING)

        val localActionId2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        val localCreatedSyncingModifiedState = localCreatedSyncingStateOriginal
                .toModifyingNowOrThrow()
                .toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )

        assertOriginalAndChanged(
                localCreatedSyncingStateOriginal,
                localCreatedSyncingModifiedState,
                userModifiedRecord = true
        )
        assertFalse(localCreatedSyncingModifiedState.canBeFullyDeletedOnLocalDelete())

        val localActionId3 = "3"
        assertEquals(
                localCreatedSyncingModifiedState.copy(localAction = LocalAction.Delete(actionId = localActionId3)),
                localCreatedSyncingModifiedState.toLocalDeletedOrThrow(newLocalActionId = localActionId3)
        )

        assertEquals(
                localCreatedSyncingModifiedState.copy(modifyingNow = true),
                localCreatedSyncingModifiedState.toModifyingNowOrThrow()
        )

        assertThrows(IllegalStateException::class) {
            localCreatedSyncingModifiedState.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(
                localCreatedSyncingModifiedState.copy(syncStatus = SyncStatus.WAITING),
                localCreatedSyncingModifiedState.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testLocalCreatedSyncingModifiedAndStartedModifyingAgain() {
        val localActionId1 = "1"
        val localCreateId = "lC1"
        val lastLocalModifiedTimestamp1 = generateFakeInstantTimestamp()

        val localCreatedSyncingStateOriginal = RecordSyncState.forLocalCreated(
                localActionId = localActionId1,
                modifyingNow = false,
                localCreatedTimestamp = lastLocalModifiedTimestamp1
        )
                .copy(localCreateId = localCreateId, syncStatus = SyncStatus.SYNCHRONIZING)

        val localActionId2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        val localCreatedSyncingModifiedAndStartedModifyingAgainState = localCreatedSyncingStateOriginal
                .toModifyingNowOrThrow()
                .toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )
                .toModifyingNowOrThrow()

        assertOriginalAndChanged(
                localCreatedSyncingStateOriginal,
                localCreatedSyncingModifiedAndStartedModifyingAgainState,
                userModifiedRecordAndStartedModifyingAgain = true
        )
        assertFalse(localCreatedSyncingModifiedAndStartedModifyingAgainState.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            localCreatedSyncingModifiedAndStartedModifyingAgainState.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        val localActionId3 = "3"
        val lastLocalModifiedTimestamp3 = generateFakeInstantTimestamp()

        assertEquals(
                localCreatedSyncingModifiedAndStartedModifyingAgainState.copy(
                        localAction = localCreatedSyncingModifiedAndStartedModifyingAgainState
                                .localAction?.copyWithNewId(actionId = localActionId3),
                        modifyingNow = false,
                        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp3
                ),
                localCreatedSyncingModifiedAndStartedModifyingAgainState.toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId3,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp3
                )
        )

        assertEquals(
                localCreatedSyncingModifiedAndStartedModifyingAgainState.copy(syncStatus = SyncStatus.WAITING),
                localCreatedSyncingModifiedAndStartedModifyingAgainState.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testLocalUpdated() {
        val remoteCreatedTimestamp = generateFakeInstantTimestamp()
        val newLastLocalModifiedTimestamp = generateFakeInstantTimestamp()
        val remoteInfo = RemoteInfo(
                remoteId = "123",
                remoteActionId = "12345",
                remoteCreatedTimestamp = remoteCreatedTimestamp,
                lastRemoteSyncedTimestamp = remoteCreatedTimestamp
        )

        val localActionId1 = "1"

        val localUpdatedState = createLocalUpdatedState(
                remoteInfo = remoteInfo,
                newLocalActionId = localActionId1,
                newLastLocalModifiedTimestamp = newLastLocalModifiedTimestamp
        )

        assertEquals(
                RecordSyncState(
                        remoteInfo = remoteInfo,
                        localAction = LocalAction.Update(actionId = localActionId1),
                        lastLocalModifiedTimestamp = newLastLocalModifiedTimestamp,
                        localCreateId = null,
                        modifyingNow = false,
                        syncStatus = SyncStatus.WAITING
                ),
                localUpdatedState
        )

        assertOriginalAndChanged(localUpdatedState, localUpdatedState.copy())
        assertFalse(localUpdatedState.canBeFullyDeletedOnLocalDelete())

        val localActionId2 = "2"

        assertEquals(
                localUpdatedState.copy(localAction = LocalAction.Delete(actionId = localActionId2)),
                localUpdatedState.toLocalDeletedOrThrow(newLocalActionId = localActionId2)
        )

        assertEquals(localUpdatedState.copy(modifyingNow = true), localUpdatedState.toModifyingNowOrThrow())

        assertThrows(IllegalStateException::class) {
            localUpdatedState.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(localUpdatedState.copy(), localUpdatedState.createNewStateToStoreAfterUnknownSyncResult())
    }

    @Test
    fun testLocalUpdatedStartedModifying() {
        val localUpdatedStateOriginal = createLocalUpdatedState()

        val localUpdatedStartedModifyingState = localUpdatedStateOriginal.toModifyingNowOrThrow()

        assertOriginalAndChanged(
                localUpdatedStateOriginal,
                localUpdatedStartedModifyingState,
                userStartedModifying = true
        )
        assertFalse(localUpdatedStartedModifyingState.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            localUpdatedStartedModifyingState.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertEquals(
                localUpdatedStartedModifyingState.copy(),
                localUpdatedStartedModifyingState.toModifyingNowOrThrow()
        )

        val localActionId2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        assertEquals(
                localUpdatedStartedModifyingState.copy(
                        localAction = localUpdatedStartedModifyingState.localAction?.copyWithNewId(
                                actionId = localActionId2
                        ),
                        modifyingNow = false,
                        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                ),
                localUpdatedStartedModifyingState.toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )
        )

        assertEquals(
                localUpdatedStartedModifyingState.copy(),
                localUpdatedStartedModifyingState.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testLocalUpdatedDeleted() {
        val localUpdatedStateOriginal = createLocalUpdatedState()

        val localActionId2 = "2"
        val localUpdatedDeletedState = localUpdatedStateOriginal.toLocalDeletedOrThrow(
                newLocalActionId = localActionId2
        )

        assertOriginalAndChanged(localUpdatedStateOriginal, localUpdatedDeletedState, userLocallyDeleted = true)
        assertFalse(localUpdatedDeletedState.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            localUpdatedDeletedState.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertThrows(IllegalStateException::class) {
            localUpdatedDeletedState.toModifyingNowOrThrow()
        }

        assertThrows(IllegalStateException::class) {
            localUpdatedDeletedState.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(
                localUpdatedDeletedState.copy(),
                localUpdatedDeletedState.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testLocalUpdatedModified() {
        val localUpdatedStateOriginal = createLocalUpdatedState()

        val localActionId2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        val localUpdatedModifiedState = localUpdatedStateOriginal
                .toModifyingNowOrThrow()
                .toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )

        assertOriginalAndChanged(localUpdatedStateOriginal, localUpdatedModifiedState, userModifiedRecord = true)
        assertFalse(localUpdatedModifiedState.canBeFullyDeletedOnLocalDelete())

        val localActionId3 = "3"
        assertEquals(
                localUpdatedModifiedState.copy(localAction = LocalAction.Delete(actionId = localActionId3)),
                localUpdatedModifiedState.toLocalDeletedOrThrow(newLocalActionId = localActionId3)
        )

        assertEquals(
                localUpdatedModifiedState.copy(modifyingNow = true),
                localUpdatedModifiedState.toModifyingNowOrThrow()
        )

        assertThrows(IllegalStateException::class) {
            localUpdatedModifiedState.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(
                localUpdatedModifiedState.copy(),
                localUpdatedModifiedState.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testLocalUpdatedModifiedAndStartedModifyingAgain() {
        val localUpdatedStateOriginal = createLocalUpdatedState()

        val localActionId2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        val localUpdatedModifiedAndStartedModifyingAgainState = localUpdatedStateOriginal
                .toModifyingNowOrThrow()
                .toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )
                .toModifyingNowOrThrow()

        assertOriginalAndChanged(
                localUpdatedStateOriginal,
                localUpdatedModifiedAndStartedModifyingAgainState,
                userModifiedRecordAndStartedModifyingAgain = true
        )

        assertFalse(localUpdatedModifiedAndStartedModifyingAgainState.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            localUpdatedModifiedAndStartedModifyingAgainState.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertEquals(
                localUpdatedModifiedAndStartedModifyingAgainState.copy(),
                localUpdatedModifiedAndStartedModifyingAgainState.toModifyingNowOrThrow()
        )

        val localActionId3 = "3"
        val lastLocalModifiedTimestamp3 = generateFakeInstantTimestamp()

        assertEquals(
                localUpdatedModifiedAndStartedModifyingAgainState.copy(
                        localAction = localUpdatedModifiedAndStartedModifyingAgainState.localAction?.copyWithNewId(
                                actionId = localActionId3
                        ),
                        modifyingNow = false,
                        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp3),
                localUpdatedModifiedAndStartedModifyingAgainState.toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId3,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp3
                )
        )

        assertEquals(localUpdatedModifiedAndStartedModifyingAgainState.copy(),
                     localUpdatedModifiedAndStartedModifyingAgainState.createNewStateToStoreAfterUnknownSyncResult())
    }

    @Test
    fun testLocalUpdatedSyncing() {
        val localUpdatedStateSyncing = createLocalUpdatedState().copy(syncStatus = SyncStatus.SYNCHRONIZING)

        assertOriginalAndChanged(localUpdatedStateSyncing, localUpdatedStateSyncing.copy())

        assertFalse(localUpdatedStateSyncing.canBeFullyDeletedOnLocalDelete())

        val localActionId2 = "2"
        assertEquals(
                localUpdatedStateSyncing.copy(localAction = LocalAction.Delete(actionId = localActionId2)),
                localUpdatedStateSyncing.toLocalDeletedOrThrow(newLocalActionId = localActionId2)
        )

        assertEquals(
                localUpdatedStateSyncing.copy(modifyingNow = true),
                localUpdatedStateSyncing.toModifyingNowOrThrow()
        )

        assertThrows(IllegalStateException::class) {
            localUpdatedStateSyncing.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(
                localUpdatedStateSyncing.copy(syncStatus = SyncStatus.WAITING),
                localUpdatedStateSyncing.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testLocalUpdatedSyncingStartedModifying() {
        val localUpdatedStateSyncingOriginal = createLocalUpdatedState().copy(syncStatus = SyncStatus.SYNCHRONIZING)

        val localUpdatedStateSyncingStartedModifying = localUpdatedStateSyncingOriginal.toModifyingNowOrThrow()

        assertOriginalAndChanged(localUpdatedStateSyncingOriginal, localUpdatedStateSyncingStartedModifying,
                                 userStartedModifying = true)

        assertFalse(localUpdatedStateSyncingStartedModifying.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            localUpdatedStateSyncingStartedModifying.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertEquals(
                localUpdatedStateSyncingStartedModifying.copy(),
                localUpdatedStateSyncingStartedModifying.toModifyingNowOrThrow()
        )

        val localAction2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        assertEquals(
                localUpdatedStateSyncingStartedModifying.copy(
                        localAction = localUpdatedStateSyncingStartedModifying.localAction?.copyWithNewId(
                                actionId = localAction2
                        ),
                        modifyingNow = false,
                        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                ),
                localUpdatedStateSyncingStartedModifying.toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localAction2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )
        )

        assertEquals(
                localUpdatedStateSyncingStartedModifying.copy(syncStatus = SyncStatus.WAITING),
                localUpdatedStateSyncingStartedModifying.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testLocalUpdatedSyncingDeleted() {
        val localUpdatedStateSyncingOriginal = createLocalUpdatedState().copy(syncStatus = SyncStatus.SYNCHRONIZING)

        val localAction2 = "2"

        val localUpdatedStateSyncingDeleted = localUpdatedStateSyncingOriginal.toLocalDeletedOrThrow(
                newLocalActionId = localAction2
        )

        assertOriginalAndChanged(
                localUpdatedStateSyncingOriginal,
                localUpdatedStateSyncingDeleted,
                userLocallyDeleted = true
        )

        assertFalse(localUpdatedStateSyncingDeleted.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            localUpdatedStateSyncingDeleted.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertThrows(IllegalStateException::class) {
            localUpdatedStateSyncingDeleted.toModifyingNowOrThrow()
        }

        assertThrows(IllegalStateException::class) {
            localUpdatedStateSyncingDeleted.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(
                localUpdatedStateSyncingDeleted.copy(syncStatus = SyncStatus.WAITING),
                localUpdatedStateSyncingDeleted.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testLocalUpdatedSyncingModified() {
        val localUpdatedStateSyncingOriginal = createLocalUpdatedState().copy(syncStatus = SyncStatus.SYNCHRONIZING)

        val localAction2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        val localUpdatedStateSyncingModified = localUpdatedStateSyncingOriginal
                .toModifyingNowOrThrow()
                .toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localAction2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )

        assertOriginalAndChanged(
                localUpdatedStateSyncingOriginal,
                localUpdatedStateSyncingModified,
                userModifiedRecord = true
        )

        assertFalse(localUpdatedStateSyncingModified.canBeFullyDeletedOnLocalDelete())

        val localAction3 = "3"
        assertEquals(
                localUpdatedStateSyncingModified.copy(localAction = LocalAction.Delete(actionId = localAction3)),
                localUpdatedStateSyncingModified.toLocalDeletedOrThrow(newLocalActionId = localAction3)
        )

        assertEquals(
                localUpdatedStateSyncingModified.copy(modifyingNow = true),
                localUpdatedStateSyncingModified.toModifyingNowOrThrow()
        )

        assertThrows(IllegalStateException::class) {
            localUpdatedStateSyncingModified.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(
                localUpdatedStateSyncingModified.copy(syncStatus = SyncStatus.WAITING),
                localUpdatedStateSyncingModified.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testLocalUpdatedSyncingModifiedAndStartedModifyingAgain() {
        val localUpdatedStateSyncingOriginal = createLocalUpdatedState().copy(syncStatus = SyncStatus.SYNCHRONIZING)

        val localAction2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        val localUpdatedStateSyncingModifiedAndStartedModifyingAgain = localUpdatedStateSyncingOriginal
                .toModifyingNowOrThrow()
                .toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localAction2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )
                .toModifyingNowOrThrow()

        assertOriginalAndChanged(
                localUpdatedStateSyncingOriginal,
                localUpdatedStateSyncingModifiedAndStartedModifyingAgain,
                userModifiedRecordAndStartedModifyingAgain = true
        )

        assertFalse(localUpdatedStateSyncingModifiedAndStartedModifyingAgain.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            localUpdatedStateSyncingModifiedAndStartedModifyingAgain.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertEquals(
                localUpdatedStateSyncingModifiedAndStartedModifyingAgain.copy(),
                localUpdatedStateSyncingModifiedAndStartedModifyingAgain.toModifyingNowOrThrow()
        )

        val localAction3 = "3"
        val lastLocalModifiedTimestamp3 = generateFakeInstantTimestamp()

        assertEquals(
                localUpdatedStateSyncingModifiedAndStartedModifyingAgain.copy(
                        localAction = localUpdatedStateSyncingModifiedAndStartedModifyingAgain
                                .localAction?.copyWithNewId(actionId = localAction3),
                        modifyingNow = false,
                        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp3
                ),
                localUpdatedStateSyncingModifiedAndStartedModifyingAgain.toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localAction3,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp3
                )
        )

        assertEquals(
                localUpdatedStateSyncingModifiedAndStartedModifyingAgain.copy(syncStatus = SyncStatus.WAITING),
                localUpdatedStateSyncingModifiedAndStartedModifyingAgain.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testSynced() {
        val remoteCreatedTimestamp = generateFakeInstantTimestamp()
        val remoteInfo = RemoteInfo(
                remoteId = "123",
                remoteActionId = "12345",
                remoteCreatedTimestamp = remoteCreatedTimestamp,
                lastRemoteSyncedTimestamp = remoteCreatedTimestamp
        )
        val lastLocalModifiedTimestamp = generateFakeInstantTimestamp()

        val syncedState = createSyncedState(
                remoteInfo = remoteInfo,
                lastLocalModifiedTimestamp = lastLocalModifiedTimestamp
        )

        assertEquals(RecordSyncState(
                remoteInfo = remoteInfo,
                localAction = null,
                lastLocalModifiedTimestamp = lastLocalModifiedTimestamp,
                localCreateId = null,
                modifyingNow = false,
                syncStatus = SyncStatus.SYNCED
        ),
                     syncedState)

        assertOriginalAndChanged(syncedState, syncedState.copy())
        assertFalse(syncedState.canBeFullyDeletedOnLocalDelete())

        val localActionId2 = "2"

        assertEquals(
                syncedState.copy(
                        localAction = LocalAction.Delete(actionId = localActionId2),
                        syncStatus = SyncStatus.WAITING
                ),
                syncedState.toLocalDeletedOrThrow(newLocalActionId = localActionId2)
        )

        assertEquals(syncedState.copy(modifyingNow = true), syncedState.toModifyingNowOrThrow())

        assertThrows(IllegalStateException::class) {
            syncedState.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(syncedState.copy(), syncedState.createNewStateToStoreAfterUnknownSyncResult())
    }

    @Test
    fun testSyncedStartedModifying() {
        val syncedStateOriginal = createSyncedState()

        val syncedStateStartedModifying = syncedStateOriginal.toModifyingNowOrThrow()

        assertOriginalAndChanged(syncedStateOriginal, syncedStateStartedModifying, userStartedModifying = true)
        assertFalse(syncedStateStartedModifying.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            syncedStateStartedModifying.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertEquals(syncedStateStartedModifying.copy(), syncedStateStartedModifying.toModifyingNowOrThrow())

        val localActionId3 = "3"
        val lastLocalModifiedTimestamp3 = generateFakeInstantTimestamp()

        assertEquals(
                syncedStateStartedModifying.copy(
                        localAction = LocalAction.Update(actionId = localActionId3),
                        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp3,
                        modifyingNow = false,
                        syncStatus = SyncStatus.WAITING
                ),
                syncedStateStartedModifying.toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId3,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp3
                )
        )

        assertEquals(
                syncedStateStartedModifying.copy(),
                syncedStateStartedModifying.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testSyncedDeleted() {
        val syncedStateOriginal = createSyncedState()

        val localActionId2 = "2"
        val syncedStateDeleted = syncedStateOriginal.toLocalDeletedOrThrow(newLocalActionId = localActionId2)

        assertOriginalAndChanged(syncedStateOriginal, syncedStateDeleted, userLocallyDeleted = true)
        assertFalse(syncedStateDeleted.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            syncedStateDeleted.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertThrows(IllegalStateException::class) {
            syncedStateDeleted.toModifyingNowOrThrow()
        }

        assertThrows(IllegalStateException::class) {
            syncedStateDeleted.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(syncedStateDeleted.copy(), syncedStateDeleted.createNewStateToStoreAfterUnknownSyncResult())
    }

    @Test
    fun testSyncedModified() {
        val syncedStateOriginal = createSyncedState()

        val localActionId2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        val syncedStateModified = syncedStateOriginal
                .toModifyingNowOrThrow()
                .toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )

        assertOriginalAndChanged(syncedStateOriginal, syncedStateModified, userModifiedRecord = true)
        assertFalse(syncedStateModified.canBeFullyDeletedOnLocalDelete())

        val localActionId3 = "3"

        assertEquals(
                syncedStateModified.copy(localAction = LocalAction.Delete(actionId = localActionId3)),
                syncedStateModified.toLocalDeletedOrThrow(newLocalActionId = localActionId3)
        )

        assertEquals(syncedStateModified.copy(modifyingNow = true), syncedStateModified.toModifyingNowOrThrow())

        assertThrows(IllegalStateException::class) {
            syncedStateModified.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(syncedStateModified.copy(), syncedStateModified.createNewStateToStoreAfterUnknownSyncResult())
    }

    @Test
    fun testSyncedModifiedAndStartedModifyingAgain() {
        val syncedStateOriginal = createSyncedState()

        val localActionId2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        val syncedStateModifiedAndStartedModifyingAgain = syncedStateOriginal
                .toModifyingNowOrThrow()
                .toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )
                .toModifyingNowOrThrow()

        assertOriginalAndChanged(
                syncedStateOriginal,
                syncedStateModifiedAndStartedModifyingAgain,
                userModifiedRecordAndStartedModifyingAgain = true
        )

        assertFalse(syncedStateModifiedAndStartedModifyingAgain.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            syncedStateModifiedAndStartedModifyingAgain.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertEquals(
                syncedStateModifiedAndStartedModifyingAgain.copy(),
                syncedStateModifiedAndStartedModifyingAgain.toModifyingNowOrThrow()
        )

        val localActionId3 = "3"
        val lastLocalModifiedTimestamp3 = generateFakeInstantTimestamp()

        assertEquals(
                syncedStateModifiedAndStartedModifyingAgain.copy(
                        localAction = syncedStateModifiedAndStartedModifyingAgain.localAction?.copyWithNewId(
                                actionId = localActionId3
                        ),
                        modifyingNow = false,
                        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp3
                ),
                syncedStateModifiedAndStartedModifyingAgain.toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId3,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp3
                )
        )

        assertEquals(
                syncedStateModifiedAndStartedModifyingAgain.copy(),
                syncedStateModifiedAndStartedModifyingAgain.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testSyncedSyncing() {
        val syncedStateSyncing = createSynchronizingState()

        assertOriginalAndChanged(syncedStateSyncing, syncedStateSyncing.copy())

        assertFalse(syncedStateSyncing.canBeFullyDeletedOnLocalDelete())

        val localActionId2 = "2"

        assertEquals(
                syncedStateSyncing.copy(localAction = LocalAction.Delete(actionId = localActionId2)),
                syncedStateSyncing.toLocalDeletedOrThrow(newLocalActionId = localActionId2)
        )

        assertEquals(syncedStateSyncing.copy(modifyingNow = true), syncedStateSyncing.toModifyingNowOrThrow())

        assertThrows(IllegalStateException::class) {
            syncedStateSyncing.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(
                syncedStateSyncing.copy(syncStatus = SyncStatus.SYNCED),
                syncedStateSyncing.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testSyncedSyncingStartedModifying() {
        val syncedStateSyncingOriginal = createSynchronizingState()

        val syncedStateSyncingStartedModifying = syncedStateSyncingOriginal.toModifyingNowOrThrow()

        assertOriginalAndChanged(
                syncedStateSyncingOriginal,
                syncedStateSyncingStartedModifying,
                userStartedModifying = true
        )

        assertFalse(syncedStateSyncingStartedModifying.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            syncedStateSyncingStartedModifying.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertEquals(
                syncedStateSyncingStartedModifying.copy(),
                syncedStateSyncingStartedModifying.toModifyingNowOrThrow()
        )

        val localActionId2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        assertEquals(
                syncedStateSyncingStartedModifying.copy(
                        localAction = LocalAction.Update(actionId = localActionId2),
                        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp2,
                        modifyingNow = false
                ),
                syncedStateSyncingStartedModifying.toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )
        )

        assertEquals(
                syncedStateSyncingStartedModifying.copy(syncStatus = SyncStatus.SYNCED),
                syncedStateSyncingStartedModifying.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testSyncedSyncingDeleted() {
        val syncedStateSyncingOriginal = createSynchronizingState()

        val localActionId2 = "2"

        val syncedStateSyncingDeleted = syncedStateSyncingOriginal.toLocalDeletedOrThrow(
                newLocalActionId = localActionId2
        )

        assertOriginalAndChanged(syncedStateSyncingOriginal, syncedStateSyncingDeleted, userLocallyDeleted = true)

        assertFalse(syncedStateSyncingDeleted.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            syncedStateSyncingDeleted.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertThrows(IllegalStateException::class) {
            syncedStateSyncingDeleted.toModifyingNowOrThrow()
        }

        assertThrows(IllegalStateException::class) {
            syncedStateSyncingDeleted.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(
                syncedStateSyncingDeleted.copy(syncStatus = SyncStatus.WAITING),
                syncedStateSyncingDeleted.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testSyncedSyncingModified() {
        val syncedStateSyncingOriginal = createSynchronizingState()

        val localActionId2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        val syncedStateSyncingModified = syncedStateSyncingOriginal
                .toModifyingNowOrThrow()
                .toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )

        assertOriginalAndChanged(syncedStateSyncingOriginal, syncedStateSyncingModified, userModifiedRecord = true)

        assertFalse(syncedStateSyncingModified.canBeFullyDeletedOnLocalDelete())

        val localActionId3 = "3"

        assertEquals(
                syncedStateSyncingModified.copy(localAction = LocalAction.Delete(actionId = localActionId3)),
                syncedStateSyncingModified.toLocalDeletedOrThrow(newLocalActionId = localActionId3)
        )

        assertEquals(
                syncedStateSyncingModified.copy(modifyingNow = true),
                syncedStateSyncingModified.toModifyingNowOrThrow()
        )

        assertThrows(IllegalStateException::class) {
            syncedStateSyncingModified.toNextModifiedAfterModifyingOrThrow(
                    newLocalActionId = "fail",
                    newLastLocalModifiedTimestamp = Instant.now()
            )
        }

        assertEquals(
                syncedStateSyncingModified.copy(syncStatus = SyncStatus.WAITING),
                syncedStateSyncingModified.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @Test
    fun testSyncedSyncingModifiedAndStartedModifyingAgain() {
        val syncedStateSyncingOriginal = createSynchronizingState()

        val localActionId2 = "2"
        val lastLocalModifiedTimestamp2 = generateFakeInstantTimestamp()

        val syncedStateSyncingModifiedAndStartedModifyingAgain = syncedStateSyncingOriginal
                .toModifyingNowOrThrow()
                .toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId2,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp2
                )
                .toModifyingNowOrThrow()

        assertOriginalAndChanged(syncedStateSyncingOriginal, syncedStateSyncingModifiedAndStartedModifyingAgain,
                                 userModifiedRecordAndStartedModifyingAgain = true)

        assertFalse(syncedStateSyncingModifiedAndStartedModifyingAgain.canBeFullyDeletedOnLocalDelete())

        assertThrows(IllegalStateException::class) {
            syncedStateSyncingModifiedAndStartedModifyingAgain.toLocalDeletedOrThrow(newLocalActionId = "fail")
        }

        assertEquals(syncedStateSyncingModifiedAndStartedModifyingAgain.copy(),
                     syncedStateSyncingModifiedAndStartedModifyingAgain.toModifyingNowOrThrow())

        val localActionId3 = "3"
        val lastLocalModifiedTimestamp3 = generateFakeInstantTimestamp()

        assertEquals(
                syncedStateSyncingModifiedAndStartedModifyingAgain.copy(
                        localAction = syncedStateSyncingModifiedAndStartedModifyingAgain.localAction?.copyWithNewId(
                                actionId = localActionId3
                        ),
                        modifyingNow = false,
                        lastLocalModifiedTimestamp = lastLocalModifiedTimestamp3
                ),
                syncedStateSyncingModifiedAndStartedModifyingAgain.toNextModifiedAfterModifyingOrThrow(
                        newLocalActionId = localActionId3,
                        newLastLocalModifiedTimestamp = lastLocalModifiedTimestamp3
                )
        )

        assertEquals(
                syncedStateSyncingModifiedAndStartedModifyingAgain.copy(syncStatus = SyncStatus.WAITING),
                syncedStateSyncingModifiedAndStartedModifyingAgain.createNewStateToStoreAfterUnknownSyncResult()
        )
    }

    @SuppressWarnings("LongParameterList")
    private fun assertOriginalAndChanged(
            original: RecordSyncState,
            changed: RecordSyncState,
            userLocallyDeleted: Boolean = false,
            userStartedModifying: Boolean = false,
            userModifiedRecord: Boolean = false,
            userModifiedRecordAndStartedModifyingAgain: Boolean = false
    ) {

        assertEquals(userLocallyDeleted, original.userLocallyDeleted(changed))
        assertEquals(userStartedModifying, original.userStartedModifying(changed))
        assertEquals(userModifiedRecord, original.userModifiedRecord(changed))
        assertEquals(
                userModifiedRecordAndStartedModifyingAgain,
                original.userModifiedRecordAndStartedModifyingAgain(changed)
        )
        assertEquals(userLocallyDeleted, original.userLocallyDeleted(changed))
    }
}