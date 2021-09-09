package com.ruslan.hlushan.game.play.api.calculations

@SuppressWarnings("LongParameterList")
data class GameViewDimensions(
        val tableViewSizeWithoutSums: Int,
        val usingViewHeight: Int,
        val usingViewWidth: Int,
        val horizontalNonUsingSpace: Int,
        val verticalNonUsingSpace: Int,
        val cellSizeWithoutLines: Int,
        val cellTextSizeParams: CellTextSizeParams
) {

    val leftRightNonUsingPadding: Float = (horizontalNonUsingSpace / 2).toFloat()

    val topBottomNonUsingPadding: Float = (verticalNonUsingSpace / 2).toFloat()

    val tableViewSizeWithSumsAndLines: Int get() = usingViewWidth

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
}