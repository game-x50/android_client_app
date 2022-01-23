package com.ruslan.hlushan.game.api.play.dto

sealed class LocalAction {

    @JvmInline
    value class Id(val value: String)

    abstract val actionId: LocalAction.Id

    data class Create(override val actionId: LocalAction.Id) : LocalAction()

    data class Update(override val actionId: LocalAction.Id) : LocalAction()

    data class Delete(override val actionId: LocalAction.Id) : LocalAction()
}

fun LocalAction.isSameType(other: LocalAction?): Boolean =
        (this.javaClass == other?.javaClass)