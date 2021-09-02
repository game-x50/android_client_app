package com.ruslan.hlushan.game.storage.impl.remote.dto.server

import com.ruslan.hlushan.game.storage.impl.remote.dto.UpdateLocalNonModifiedResponse
import com.ruslan.hlushan.parsing.impl.utils.parsing.InstantAsEpochMillisSerializer
import kotlinx.serialization.Serializable
import org.threeten.bp.Instant
import java.lang.IllegalStateException

/**
 * @author Ruslan Hlushan on 2019-06-21
 */

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
                        remoteId = this.id,
                        lastRemoteSyncedTimestamp = this.lastSyncedTimestamp
                )
            }
            (this.status == UpdateLocalNonModifiedApiResponse.ChangedStatus.CHANGED
             && this.lastSyncedTimestamp == null
             && this.game != null) -> {
                UpdateLocalNonModifiedResponse.Changed(
                        remoteId = this.id,
                        remoteRecord = this.game.toRemoteRecord()
                )
            }

            (this.status == UpdateLocalNonModifiedApiResponse.ChangedStatus.DELETED
             && this.lastSyncedTimestamp == null
             && this.game == null) -> {
                UpdateLocalNonModifiedResponse.Deleted(remoteId = this.id)
            }
            (this.status == null
             && this.lastSyncedTimestamp == null
             && this.game == null) -> {
                UpdateLocalNonModifiedResponse.Fail(remoteId = this.id)
            }
            else                   -> throw  IllegalStateException("Can't be mapped: $this")
        }