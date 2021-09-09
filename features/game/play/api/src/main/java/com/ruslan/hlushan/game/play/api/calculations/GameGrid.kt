package com.ruslan.hlushan.game.play.api.calculations

data class GameGrid(
        val tableGridLines: MutableList<GridLine>,
        val newElementsGrid: MutableList<GridLine>
)

data class GridLine(
        val leftOfLine: Float,
        val topOfLine: Float,
        val width: Float,
        val height: Float
) {

    val rightOfLine: Float = (leftOfLine + width)

    val bottomOfLine: Float = (topOfLine + height)
}