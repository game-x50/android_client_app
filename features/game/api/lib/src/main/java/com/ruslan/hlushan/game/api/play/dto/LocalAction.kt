package com.ruslan.hlushan.game.api.play.dto

sealed class LocalAction {

    abstract val actionId: String

    data class Create(override val actionId: String) : LocalAction()

    data class Update(override val actionId: String) : LocalAction()

    data class Delete(override val actionId: String) : LocalAction()
}

fun LocalAction.isSameType(other: LocalAction?): Boolean =
        (this.javaClass == other?.javaClass)