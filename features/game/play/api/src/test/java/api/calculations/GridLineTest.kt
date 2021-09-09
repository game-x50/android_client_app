package api.calculations

import com.ruslan.hlushan.game.play.api.calculations.GridLine
import org.junit.Assert.assertEquals
import org.junit.Test

class GridLineTest {

    @Test
    fun equality() =
            assertDifferentParams { gridLine1, leftOfLine, topOfLine, width, height ->
                val gridLine2 = GridLine(
                        leftOfLine = leftOfLine,
                        topOfLine = topOfLine,
                        width = width,
                        height = height
                )
                assertEquals(gridLine2, gridLine1)
            }

    @Test
    fun rightOfLine() =
            assertDifferentParams { gridLine, leftOfLine, topOfLine, width, height ->
                assertEquals((leftOfLine + width), gridLine.rightOfLine)
            }

    @Test
    fun bottomOfLine() =
            assertDifferentParams { gridLine, leftOfLine, topOfLine, width, height ->
                assertEquals((topOfLine + height), gridLine.bottomOfLine)
            }

    @SuppressWarnings("NestedBlockDepth")
    private fun assertDifferentParams(
            assert: (GridLine, leftOfLine: Float, topOfLine: Float, width: Float, height: Float) -> Unit
    ) {
        for (leftOfLine in 0..100 step 10) {
            for (topOfLine in 0..100 step 10) {
                for (width in 0..100 step 10) {
                    for (height in 0..100 step 10) {

                        val leftOfLineFloat = leftOfLine.toFloat()
                        val topOfLineFloat = topOfLine.toFloat()
                        val widthFloat = width.toFloat()
                        val heightFloat = height.toFloat()

                        val gridLine = GridLine(
                                leftOfLine = leftOfLineFloat,
                                topOfLine = topOfLineFloat,
                                width = widthFloat,
                                height = heightFloat
                        )

                        assert(gridLine, leftOfLineFloat, topOfLineFloat, widthFloat, heightFloat)
                    }
                }
            }
        }
    }
}