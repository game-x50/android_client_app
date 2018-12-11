package com.ruslan.hlushan.game.core.api.play.dto

/**
 * @author Ruslan Hlushan on 10/17/18.
 */
enum class GameSize(
        val countRowsAndColumns: Int,
        val defaultNewItemsCount: Int
) {

    SMALL(countRowsAndColumns = 9, defaultNewItemsCount = 6),
    MEDIUM(countRowsAndColumns = 16, defaultNewItemsCount = 10),
    BIG(countRowsAndColumns = 25, defaultNewItemsCount = 15);

    companion object {

        fun fromCountRowsAndColumns(countRowsAndColumns: Int): GameSize =
                (GameSize.values().firstOrNull { size -> size.countRowsAndColumns == countRowsAndColumns }
                 ?: SMALL)
    }
}