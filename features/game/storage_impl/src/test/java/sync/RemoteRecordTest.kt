package sync

import com.ruslan.hlushan.game.core.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.storage.impl.local.LocalUpdateRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.toSyncLocalUpdateRequest
import generateFakeRemoteRecord
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Ruslan Hlushan on 2019-05-31
 */
class RemoteRecordTest {

    @Test
    fun testRemoteRecordToSyncLocalUpdateRequestModifyingNow() =
            testRemoteRecordToSyncLocalUpdateRequest(modifyingNow = true)

    @Test
    fun testRemoteRecordToSyncLocalUpdateRequestNotModifyingNow() =
            testRemoteRecordToSyncLocalUpdateRequest(modifyingNow = false)

    private fun testRemoteRecordToSyncLocalUpdateRequest(modifyingNow: Boolean) {
        val remoteRecord = generateFakeRemoteRecord()

        val syncState = RecordSyncState.forSync(
                remoteInfo = remoteRecord.remoteInfo,
                lastLocalModifiedTimestamp = remoteRecord.lastLocalModifiedTimestamp,
                modifyingNow = modifyingNow
        )

        val localUpdateRequest = LocalUpdateRequest(
                gameState = remoteRecord.gameState,
                totalPlayed = remoteRecord.totalPlayed,
                syncState = syncState
        )

        assertEquals(localUpdateRequest, remoteRecord.toSyncLocalUpdateRequest(modifyingNow = modifyingNow))
    }
}