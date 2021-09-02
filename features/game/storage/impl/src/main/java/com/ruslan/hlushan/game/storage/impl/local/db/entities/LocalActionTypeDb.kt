package com.ruslan.hlushan.game.storage.impl.local.db.entities

import com.ruslan.hlushan.game.core.api.play.dto.LocalAction

internal enum class LocalActionTypeDb {
    CREATE,
    UPDATE,
    DELETE;
}

internal val LocalAction.typeDb: LocalActionTypeDb
    get() = when (this) {
        is LocalAction.Create -> LocalActionTypeDb.CREATE
        is LocalAction.Update -> LocalActionTypeDb.UPDATE
        is LocalAction.Delete -> LocalActionTypeDb.DELETE
    }

internal fun LocalActionTypeDb.toLocalAction(
        actionId: String
): LocalAction = when (this) {
    LocalActionTypeDb.CREATE -> LocalAction.Create(actionId = actionId)
    LocalActionTypeDb.UPDATE -> LocalAction.Update(actionId = actionId)
    LocalActionTypeDb.DELETE -> LocalAction.Delete(actionId = actionId)
}