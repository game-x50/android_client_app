package com.ruslan.hlushan.game.core.api.play.dto

/**
 * @author Ruslan Hlushan on 2019-07-31
 */
enum class OrderType(val dbQueryValue: String) {

    ASC(dbQueryValue = "ASC"),
    DESC(dbQueryValue = "DESC");

    companion object {
        fun fromOrdinal(ordinal: Int): OrderType? =
                OrderType.values().getOrNull(ordinal)
    }
}