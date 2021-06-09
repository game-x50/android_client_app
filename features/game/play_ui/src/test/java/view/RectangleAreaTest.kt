package view

import com.ruslan.hlushan.game.play.ui.view.RectangleArea
import com.ruslan.hlushan.game.play.ui.view.bottomY
import com.ruslan.hlushan.game.play.ui.view.calculations.CellTextSizeParams
import com.ruslan.hlushan.game.play.ui.view.centerX
import com.ruslan.hlushan.game.play.ui.view.centerY
import com.ruslan.hlushan.game.play.ui.view.convertToFake
import com.ruslan.hlushan.game.play.ui.view.rightX
import com.ruslan.hlushan.game.play.ui.view.textBackgroundRadius
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Ruslan Hlushan on 2019-09-05
 */
class RectangleAreaTest {

    private val cellTextSizeParams = CellTextSizeParams.createDefault()

    @Test
    fun equality() = assertDifferentParams { rectangleArea1,
                                             leftX,
                                             topY,
                                             size,
                                             position,
                                             number,
                                             isFake,
                                             drawBackground ->

        val rectangleArea2 = RectangleArea(
                leftX = leftX,
                topY = topY,
                size = size,
                position = position,
                number = number,
                cellTextSizeParams = cellTextSizeParams,
                isFake = isFake,
                drawBackground = drawBackground
        )

        assertEquals(rectangleArea1, rectangleArea2)

        val newLeftX = leftX.inc()
        rectangleArea1.leftX = newLeftX
        rectangleArea2.leftX = newLeftX
        assertEquals(rectangleArea1, rectangleArea2)

        val newTopY = topY.inc()
        rectangleArea1.topY = newTopY
        rectangleArea2.topY = newTopY
        assertEquals(rectangleArea1, rectangleArea2)

        val newSize = size.inc()
        rectangleArea1.size = newSize
        rectangleArea2.size = newSize
        assertEquals(rectangleArea1, rectangleArea2)

        val newNumber = number.inc()
        rectangleArea1.number = newNumber
        rectangleArea2.number = newNumber
        assertEquals(rectangleArea1, rectangleArea2)

        val newIsFake = !isFake
        rectangleArea1.isFake = newIsFake
        rectangleArea2.isFake = newIsFake
        assertEquals(rectangleArea1, rectangleArea2)
    }

    @SuppressWarnings("NestedBlockDepth")
    @Test
    fun createDefault() {
        for (number in 0..100 step 10) {
            for (position in 0..100 step 10) {
                booleanArrayOf(true, false).forEach { isFake ->
                    booleanArrayOf(true, false).forEach { drawBackground ->

                        val rectangleArea = RectangleArea.createDefault(
                                position = position,
                                number = number,
                                isFake = isFake,
                                drawBackground = drawBackground
                        )

                        val expectedRectangleArea = RectangleArea(
                                leftX = 0f,
                                topY = 0f,
                                size = 0f,
                                position = position,
                                number = number,
                                cellTextSizeParams = CellTextSizeParams.createDefault(),
                                isFake = isFake,
                                drawBackground = drawBackground
                        )

                        assertEquals(expectedRectangleArea, rectangleArea)
                    }
                }
            }
        }
    }

    @Test
    fun rightX() = assertDifferentParams { rectangleArea,
                                           leftX, topY,
                                           size,
                                           position,
                                           number,
                                           isFake,
                                           drawBackground ->

        assertEquals((leftX + size), rectangleArea.rightX)

        val newLeftX = leftX.inc()
        rectangleArea.leftX = newLeftX
        assertEquals((newLeftX + size), rectangleArea.rightX)

        val newSize = size.inc()
        rectangleArea.size = newSize
        assertEquals((newLeftX + newSize), rectangleArea.rightX)
    }

    @Test
    fun bottomY() = assertDifferentParams { rectangleArea,
                                            leftX, topY,
                                            size,
                                            position,
                                            number,
                                            isFake,
                                            drawBackground ->

        assertEquals((topY + size), rectangleArea.bottomY)

        val newTopY = topY.inc()
        rectangleArea.topY = newTopY
        assertEquals((newTopY + size), rectangleArea.bottomY)

        val newSize = size.inc()
        rectangleArea.size = newSize
        assertEquals((newTopY + newSize), rectangleArea.bottomY)
    }

    @Test
    fun centerX() = assertDifferentParams { rectangleArea,
                                            leftX, topY,
                                            size,
                                            position,
                                            number,
                                            isFake,
                                            drawBackground ->

        assertEquals((leftX + (size / 2)), rectangleArea.centerX)

        val newLeftX = leftX.inc()
        rectangleArea.leftX = newLeftX
        assertEquals((newLeftX + (size / 2)), rectangleArea.centerX)

        val newSize = size.inc()
        rectangleArea.size = newSize
        assertEquals((newLeftX + (newSize / 2)), rectangleArea.centerX)
    }

    @Test
    fun centerY() = assertDifferentParams { rectangleArea,
                                            leftX, topY,
                                            size,
                                            position,
                                            number,
                                            isFake,
                                            drawBackground ->

        assertEquals((topY + (size / 2)), rectangleArea.centerY)

        val newTopY = topY.inc()
        rectangleArea.topY = newTopY
        assertEquals((newTopY + (size / 2)), rectangleArea.centerY)

        val newSize = size.inc()
        rectangleArea.size = newSize
        assertEquals((newTopY + (newSize / 2)), rectangleArea.centerY)
    }

    @Test
    fun textBackgroundRadius() {
        val coef = 8

        assertDifferentParams { rectangleArea, leftX, topY, size, position, number, isFake, drawBackground ->

            assertEquals((size / coef), rectangleArea.textBackgroundRadius)

            val newSize = size.inc()
            rectangleArea.size = newSize
            assertEquals((newSize / coef), rectangleArea.textBackgroundRadius)
        }
    }

    @Test
    fun convertToFake() = assertDifferentParams { rectangleArea,
                                                  leftX, topY,
                                                  size,
                                                  position,
                                                  number,
                                                  isFake,
                                                  drawBackground ->

        rectangleArea.convertToFake()

        val expected = RectangleArea(
                leftX = leftX,
                topY = topY,
                size = size,
                position = position,
                number = 0,
                cellTextSizeParams = cellTextSizeParams,
                isFake = true,
                drawBackground = drawBackground
        )

        assertEquals(expected, rectangleArea)
    }

    @SuppressWarnings("NestedBlockDepth")
    private fun assertDifferentParams(
            assert: (RectangleArea,
                     leftX: Float,
                     topY: Float,
                     size: Float,
                     position: Int,
                     number: Int,
                     isFake: Boolean,
                     drawBackground: Boolean) -> Unit
    ) {
        for (leftX in 0..100 step 10) {
            for (topY in 0..100 step 10) {
                for (size in 0..100 step 10) {
                    for (number in 0..100 step 10) {
                        for (position in 0..100 step 10) {
                            booleanArrayOf(true, false).forEach { isFake ->
                                booleanArrayOf(true, false).forEach { drawBackground ->

                                    val leftXFloat = leftX.toFloat()
                                    val topYFloat = topY.toFloat()
                                    val sizeFloat = size.toFloat()

                                    val rectangleArea = RectangleArea(
                                            leftX = leftXFloat,
                                            topY = topYFloat,
                                            size = sizeFloat,
                                            position = position,
                                            number = number,
                                            cellTextSizeParams = cellTextSizeParams,
                                            isFake = isFake,
                                            drawBackground = drawBackground
                                    )

                                    assert(rectangleArea,
                                           leftXFloat,
                                           topYFloat,
                                           sizeFloat,
                                           position,
                                           number,
                                           isFake,
                                           drawBackground)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}