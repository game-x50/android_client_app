package com.ruslan.hlushan.game.storage.impl.remote.dto.server

import com.ruslan.hlushan.game.api.play.dto.RemoteInfo
import com.ruslan.hlushan.game.storage.impl.remote.dto.LocalModifiedResponse
import com.ruslan.hlushan.parsing.impl.utils.parsing.InstantAsEpochMillisSerializer
import kotlinx.serialization.Serializable
import org.threeten.bp.Instant
import java.lang.IllegalStateException

/**
 * @author Ruslan Hlushan on 2019-06-21
 */
@SuppressWarnings("LongParameterList")
@Serializable
internal data class LocalModifiedApiResponse(
        val localId: Long,
        val action: LocalActionTypeApi? = null,
        val id: String? = null,
        val lastActionId: String? = null,
        @Serializable(with = InstantAsEpochMillisSerializer::class)
        val createdTimestamp: Instant? = null,
        @Serializable(with = InstantAsEpochMillisSerializer::class)
        val lastSyncedTimestamp: Instant? = null,
        val game: RemoteApiGame? = null
)

@SuppressWarnings("LongMethod", "ComplexMethod")
internal fun LocalModifiedApiResponse.toLocalModifiedResponse(): LocalModifiedResponse =
        when {
            (this.action == LocalActionTypeApi.CREATE
             && this.id != null
             && this.lastActionId != null
             && this.createdTimestamp != null
             && this.lastSyncedTimestamp != null
             && this.game == null) -> {
                LocalModifiedResponse.Create.Success(
                        id = this.localId,
                        remoteInfo = createRemoteInfo()!!
                )
            }
            (this.action == LocalActionTypeApi.CREATE
             && this.id != null
             && this.lastActionId == null
             && this.createdTimestamp == null
             && this.lastSyncedTimestamp == null
             && this.game != null) -> {
                LocalModifiedResponse.Create.WasChanged(
                        id = this.localId,
                        remoteRecord = this.game.toRemoteRecord()
                )
            }
            (this.action == LocalActionTypeApi.UPDATE
             && this.id != null
             && this.lastActionId != null
             && this.createdTimestamp != null
             && this.lastSyncedTimestamp != null
             && this.game == null) -> {
                LocalModifiedResponse.Update(
                        id = this.localId,
                        remoteInfo = createRemoteInfo()!!
                )
            }
            (this.action == LocalActionTypeApi.DELETE
             && this.id == null
             && this.lastActionId == null
             && this.createdTimestamp == null
             && this.lastSyncedTimestamp == null
             && this.game == null) -> {
                LocalModifiedResponse.Delete.Success(id = this.localId)
            }
            (this.action == LocalActionTypeApi.DELETE
             && this.id != null
             && this.lastActionId == null
             && this.createdTimestamp == null
             && this.lastSyncedTimestamp == null
             && this.game != null) -> {
                LocalModifiedResponse.Delete.WasChanged(
                        id = this.localId,
                        remoteRecord = this.game.toRemoteRecord()
                )
            }
            (this.action == null
             && this.id == null
             && this.lastActionId == null
             && this.createdTimestamp == null
             && this.lastSyncedTimestamp == null
             && this.game == null) -> {
                LocalModifiedResponse.Fail(id = this.localId)
            }
            else                   -> throw  IllegalStateException("Can't be mapped: $this")
        }

private fun LocalModifiedApiResponse.createRemoteInfo(): RemoteInfo? =
        @Suppress("ComplexCondition")
        if ((this.id != null)
            && (this.lastActionId != null)
            && (this.createdTimestamp != null)
            && (this.lastSyncedTimestamp != null)) {
            RemoteInfo(
                    remoteId = this.id,
                    remoteActionId = this.lastActionId,
                    remoteCreatedTimestamp = this.createdTimestamp,
                    lastRemoteSyncedTimestamp = this.lastSyncedTimestamp
            )
        } else {
            null
        }