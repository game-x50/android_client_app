package play.dto

import com.ruslan.hlushan.game.core.api.play.dto.IllegalRemoteTimestampsException
import com.ruslan.hlushan.game.core.api.play.dto.RemoteInfo
import org.junit.Test
import org.threeten.bp.Instant

class RemoteInfoTest {

    @Test(expected = IllegalRemoteTimestampsException::class)
    fun `constructor not valid - remoteCreatedTimestamp can't be grater then lastRemoteSyncedTimestamp`() {
        RemoteInfo(
                remoteId = "",
                remoteActionId = "",
                remoteCreatedTimestamp = Instant.now(),
                lastRemoteSyncedTimestamp = Instant.now().minusMillis(10)
        )
    }
}