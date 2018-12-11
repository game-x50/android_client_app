package com.ruslan.hlushan.game.core.api.play.dto

import org.threeten.bp.Instant

class IllegalCreateStatusAndRemoteActionsException(
        localAction: LocalAction?,
        remoteInfo: RemoteInfo
) : IllegalArgumentException(
        "For local created record all remote fields should be null." +
        " But current: localAction = $localAction, remoteInfo = $remoteInfo."
)

class IllegalCreateStatusAndLocalCreateIdException(
        localAction: LocalAction?,
        localCreateId: String?,
        syncStatus: SyncStatus
) : IllegalArgumentException(
        "For local created record with syncStatus == ${SyncStatus.SYNCHRONIZING}," +
        " or local created and deleted while sync localCreateId should be not null." +
        " But current: localAction = $localAction, localCreateId = $localCreateId, syncStatus = $syncStatus."
)

class IllegalLocalCreatedIdValueException(
        localAction: LocalAction?,
        localCreateId: String?
) : IllegalArgumentException(
        "For non local created record localCreateId should be null," +
        " but localAction = $localAction, localCreateId = $localCreateId."
)

class IllegalUpdateStateException : IllegalArgumentException(
        "For local modified record all remote fields should be not null, but remoteInfo = null."
)

class IllegalDeleteStateException : IllegalArgumentException("Local deleted record can't be modified.")

class IllegalSyncStatusException(
        localAction: LocalAction?,
        syncStatus: SyncStatus
) : IllegalArgumentException(
        "If localAction != null, syncStatus can't be ${SyncStatus.SYNCED}," +
        " or if localAction == null, syncStatus can't be ${SyncStatus.WAITING}." +
        " But current: localAction = $localAction, syncStatus = $syncStatus."
)

class IllegalRemoteTimestampsException(
        remoteCreatedTimestamp: Instant?,
        lastRemoteSyncedTimestamp: Instant?
) : IllegalArgumentException(
        "remoteCreatedTimestamp can't be grater then lastRemoteSyncedTimestamp, but current:" +
        " remoteCreatedTimestamp= $remoteCreatedTimestamp, lastRemoteSyncedTimestamp = $lastRemoteSyncedTimestamp"
)