package com.ruslan.hlushan.game.storage.impl.remote.dto.server

import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.storage.impl.remote.dto.UpdateLocalNonModifiedResponse
import com.ruslan.hlushan.parsing.impl.utils.parsing.InstantAsEpochMillisSerializer
import kotlinx.serialization.Serializable
import org.threeten.bp.Instant
import java.lang.IllegalStateException

@Serializable
internal data class UpdateLocalNonModifiedApiResponse(
        val id: String,
        val status: ChangedStatus? = null,
        @Serializable(with = InstantAsEpochMillisSerializer::class)
        val lastSyncedTimestamp: Instant? = null,
        val game: RemoteApiGame? = null
) {

    enum class ChangedStatus {
        NO_CHANGES, CHANGED, DELETED
    }
}

@SuppressWarnings("ComplexMethod")
internal fun UpdateLocalNonModifiedApiResponse.toUpdateLocalNonModifiedResponse(): UpdateLocalNonModifiedResponse =
        when {
            (this.status == UpdateLocalNonModifiedApiResponse.ChangedStatus.NO_CHANGES
             && this.lastSyncedTimestamp != null
             && this.game == null) -> {
                UpdateLocalNonModifiedResponse.NoChanges(
                        remoteId = RemoteInfo.Id(this.id),
                        lastRemoteSyncedTimestamp = RemoteInfo.LastSyncedTimestamp(this.lastSyncedTimestamp)
                )
            }
            (this.status == UpdateLocalNonModifiedApiResponse.ChangedStatus.CHANGED
             && this.lastSyncedTimestamp == null
             && this.game != null) -> {
                UpdateLocalNonModifiedResponse.Changed(
                        remoteId = RemoteInfo.Id(this.id),
                        remoteRecord = this.game.toRemoteRecord()
                )
            }

            (this.status == UpdateLocalNonModifiedApiResponse.ChangedStatus.DELETED
             && this.lastSyncedTimestamp == null
             && this.game == null) -> {
                UpdateLocalNonModifiedResponse.Deleted(remoteId = RemoteInfo.Id(this.id))
            }
            (this.status == null
             && this.lastSyncedTimestamp == null
             && this.game == null) -> {
                UpdateLocalNonModifiedResponse.Fail(remoteId = RemoteInfo.Id(this.id))
            }
            else                   -> throw  IllegalStateException("Can't be mapped: $this")
        }