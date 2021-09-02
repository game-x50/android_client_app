package com.ruslan.hlushan.game.core.api.play.dto

enum class OrderType {

    ASC,
    DESC;

    companion object {
        fun fromOrdinal(ordinal: Int): OrderType? =
                OrderType.values().getOrNull(ordinal)
    }
}