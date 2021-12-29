package sync

import com.ruslan.hlushan.game.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.storage.impl.local.LocalUpdateRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.toSyncLocalUpdateRequest
import org.junit.Assert.assertEquals
import org.junit.Test
import utils.generateFakeRemoteRecord

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