package api.calculations

import com.ruslan.hlushan.game.play.api.calculations.CellTextSizeParams
import com.ruslan.hlushan.game.play.api.calculations.GameViewDimensions
import org.junit.Assert.assertEquals
import org.junit.Test

class GameViewDimensionsTest {

    private val cellTextSizeParams = CellTextSizeParams.createDefault()

    @Test
    fun equality() = assertDifferentParams { gameViewDimensions1,
                                             tableViewSizeWithoutSums,
                                             usingViewHeight,
                                             usingViewWidth,
                                             horizontalNonUsingSpace,
                                             verticalNonUsingSpace,
                                             cellSizeWithoutLines ->
        val gameViewDimensions2 = GameViewDimensions(
                tableViewSizeWithoutSums = tableViewSizeWithoutSums,
                usingViewHeight = usingViewHeight,
                usingViewWidth = usingViewWidth,
                horizontalNonUsingSpace = horizontalNonUsingSpace,
                verticalNonUsingSpace = verticalNonUsingSpace,
                cellSizeWithoutLines = cellSizeWithoutLines,
                cellTextSizeParams = cellTextSizeParams
        )

        assertEquals(gameViewDimensions2, gameViewDimensions1)
    }

    @Test
    fun createDefault() =
            assertEquals(GameViewDimensions(
                    tableViewSizeWithoutSums = 0,
                    usingViewHeight = 0,
                    usingViewWidth = 0,
                    horizontalNonUsingSpace = 0,
                    verticalNonUsingSpace = 0,
                    cellSizeWithoutLines = 0,
                    cellTextSizeParams = cellTextSizeParams
            ),
                         GameViewDimensions.createDefault())

    @Test
    fun leftRightNonUsingPadding() = assertDifferentParams { gameViewDimensions,
                                                             tableViewSizeWithoutSums,
                                                             usingViewHeight,
                                                             usingViewWidth,
                                                             horizontalNonUsingSpace,
                                                             verticalNonUsingSpace,
                                                             cellSizeWithoutLines ->

        assertEquals((horizontalNonUsingSpace / 2).toFloat(), gameViewDimensions.leftRightNonUsingPadding)
    }

    @Test
    fun topBottomNonUsingPadding() = assertDifferentParams { gameViewDimensions,
                                                             tableViewSizeWithoutSums,
                                                             usingViewHeight,
                                                             usingViewWidth,
                                                             horizontalNonUsingSpace,
                                                             verticalNonUsingSpace,
                                                             cellSizeWithoutLines ->

        assertEquals((verticalNonUsingSpace / 2).toFloat(), gameViewDimensions.topBottomNonUsingPadding)
    }

    @Test
    fun tableViewSizeWithSumsAndLines() = assertDifferentParams { gameViewDimensions,
                                                                  tableViewSizeWithoutSums,
                                                                  usingViewHeight,
                                                                  usingViewWidth,
                                                                  horizontalNonUsingSpace,
                                                                  verticalNonUsingSpace,
                                                                  cellSizeWithoutLines ->

        assertEquals(usingViewWidth, gameViewDimensions.tableViewSizeWithSumsAndLines)
    }

    @SuppressWarnings("NestedBlockDepth")
    private fun assertDifferentParams(assert: (GameViewDimensions,
                                               tableViewSizeWithoutSums: Int,
                                               usingViewHeight: Int,
                                               usingViewWidth: Int,
                                               horizontalNonUsingSpace: Int,
                                               verticalNonUsingSpace: Int,
                                               cellSizeWithoutLines: Int) -> Unit) {
        for (tableViewSizeWithoutSums in 0..100 step 10) {
            for (usingViewHeight in 0..100 step 10) {
                for (usingViewWidth in 0..100 step 10) {
                    for (horizontalNonUsingSpace in 0..100 step 10) {
                        for (verticalNonUsingSpace in 0..100 step 10) {
                            for (cellSizeWithoutLines in 0..100 step 10) {
                                val gameViewDimensions = GameViewDimensions(
                                        tableViewSizeWithoutSums = tableViewSizeWithoutSums,
                                        usingViewHeight = usingViewHeight,
                                        usingViewWidth = usingViewWidth,
                                        horizontalNonUsingSpace = horizontalNonUsingSpace,
                                        verticalNonUsingSpace = verticalNonUsingSpace,
                                        cellSizeWithoutLines = cellSizeWithoutLines,
                                        cellTextSizeParams = cellTextSizeParams
                                )

                                assert(gameViewDimensions,
                                       tableViewSizeWithoutSums,
                                       usingViewHeight,
                                       usingViewWidth,
                                       horizontalNonUsingSpace,
                                       verticalNonUsingSpace,
                                       cellSizeWithoutLines)
                            }
                        }
                    }
                }
            }
        }
    }
}