package com.ruslan.hlushan.game.play.ui.view.calculations

/**
 * @author Ruslan Hlushan on 8/30/18.
 */
@SuppressWarnings("LongParameterList")
internal data class GameViewDimensions(
        val tableViewSizeWithoutSums: Int,
        val usingViewHeight: Int,
        val usingViewWidth: Int,
        val horizontalNonUsingSpace: Int,
        val verticalNonUsingSpace: Int,
        val cellSizeWithoutLines: Int,
        val cellTextSizeParams: CellTextSizeParams
) {

    @SuppressWarnings("ClassOrdering")
    companion object {
        fun createDefault(): GameViewDimensions = GameViewDimensions(
                tableViewSizeWithoutSums = 0,
                usingViewHeight = 0,
                usingViewWidth = 0,
                horizontalNonUsingSpace = 0,
                verticalNonUsingSpace = 0,
                cellSizeWithoutLines = 0,
                cellTextSizeParams = CellTextSizeParams.createDefault()
        )
    }

    val leftRightNonUsingPadding: Float = (horizontalNonUsingSpace / 2).toFloat()

    val topBottomNonUsingPadding: Float = (verticalNonUsingSpace / 2).toFloat()

    val tableViewSizeWithSumsAndLines: Int get() = usingViewWidth
}