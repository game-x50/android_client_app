package com.ruslan.hlushan.core.ui.recycler.item

interface RecyclerItem<out Id : Any> {

    val gridSpan: Int get() = 1

    val id: Id

    fun isTheSameItem(other: RecyclerItem<*>): Boolean = (this.id == other.id)
    fun hasTheSameContent(other: RecyclerItem<*>): Boolean = (this === other)
}