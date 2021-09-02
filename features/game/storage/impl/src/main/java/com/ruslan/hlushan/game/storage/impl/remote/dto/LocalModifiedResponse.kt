package com.ruslan.hlushan.game.storage.impl.remote.dto

import com.ruslan.hlushan.game.core.api.play.dto.RemoteInfo

internal sealed class LocalModifiedResponse {

    abstract val id: Long

    sealed class Create : LocalModifiedResponse() {

        data class Success(
                override val id: Long,
                val remoteInfo: RemoteInfo
        ) : Create()

        data class WasChanged(
                override val id: Long,
                val remoteRecord: RemoteRecord
        ) : Create()
    }

    data class Update(
            override val id: Long,
            val remoteInfo: RemoteInfo
    ) : LocalModifiedResponse()

    sealed class Delete : LocalModifiedResponse() {

        data class Success(
                override val id: Long
        ) : Delete()

        data class WasChanged(
                override val id: Long,
                val remoteRecord: RemoteRecord
        ) : Delete()
    }

    data class Fail(
            override val id: Long
    ) : LocalModifiedResponse()
}