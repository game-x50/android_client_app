package play.dto

import com.ruslan.hlushan.game.api.play.dto.IllegalRemoteTimestampsException
import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import org.junit.Test
import org.threeten.bp.Instant

class RemoteInfoTest {

    @Test(expected = IllegalRemoteTimestampsException::class)
    fun `constructor not valid - remoteCreatedTimestamp can't be grater then lastRemoteSyncedTimestamp`() {
        val nowTimestamp = Instant.now()
        RemoteInfo(
                remoteId = RemoteInfo.Id(""),
                remoteActionId = RemoteInfo.ActionId(""),
                remoteCreatedTimestamp = RemoteInfo.CreatedTimestamp(nowTimestamp),
                lastRemoteSyncedTimestamp = RemoteInfo.LastSyncedTimestamp(nowTimestamp.minusMillis(10))
        )
    }
}