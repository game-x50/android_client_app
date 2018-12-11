package com.ruslan.hlushan.game.play.ui.view.calculations

/**
 * @author Ruslan Hlushan on 8/31/18.
 */
internal data class GameGrid(
        val tableGridLines: MutableList<GridLine>,
        val newElementsGrid: MutableList<GridLine>
)

internal data class GridLine(
        val leftOfLine: Float,
        val topOfLine: Float,
        val width: Float,
        val height: Float
) {

    val rightOfLine: Float = (leftOfLine + width)

    val bottomOfLine: Float = (topOfLine + height)
}