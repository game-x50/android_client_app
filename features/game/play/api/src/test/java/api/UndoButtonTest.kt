package api

import com.ruslan.hlushan.game.play.api.UndoButton
import org.junit.Assert.assertEquals
import org.junit.Test

class UndoButtonTest {

    @Test
    fun equality() = assertDifferentParams { undoButton1, leftX, topY, rightX, bottomY ->
        val undoButton2 = UndoButton(leftX = leftX,
                                     topY = topY,
                                     rightX = rightX,
                                     bottomY = bottomY)

        assertEquals(undoButton2, undoButton1)
    }

    @Test
    fun createDefault() =
            assertEquals(UndoButton(leftX = 0f,
                                    topY = 0f,
                                    rightX = 0f,
                                    bottomY = 0f),
                         UndoButton.createDefault())

    @Test
    fun centerX() = assertDifferentParams { undoButton, leftX, topY, rightX, bottomY ->
        assertEquals(((leftX + rightX) / 2), undoButton.centerX)
    }

    @Test
    fun centerY() = assertDifferentParams { undoButton, leftX, topY, rightX, bottomY ->
        assertEquals(((topY + bottomY) / 2), undoButton.centerY)
    }

    @Test
    fun drawingHeight() = assertDifferentParams { undoButton, leftX, topY, rightX, bottomY ->
        assertEquals(((bottomY - topY) * UndoButton.ALLOWED_DRAWING_PERCENT), undoButton.drawingHeight)
    }

    @Test
    fun drawingWidth() = assertDifferentParams { undoButton, leftX, topY, rightX, bottomY ->
        assertEquals(((rightX - leftX) * UndoButton.ALLOWED_DRAWING_PERCENT), undoButton.drawingWidth)
    }

    @SuppressWarnings("NestedBlockDepth")
    private fun assertDifferentParams(assert: (UndoButton,
                                               leftX: Float,
                                               topY: Float,
                                               rightX: Float,
                                               bottomY: Float) -> Unit) {
        for (leftX in 0..100 step 10) {
            for (topY in 0..100 step 10) {
                for (rightX in 0..100 step 10) {
                    for (bottomY in 0..100 step 10) {

                        val leftXFloat = leftX.toFloat()
                        val topYFloat = topY.toFloat()
                        val rightXFloat = rightX.toFloat()
                        val bottomYFloat = bottomY.toFloat()

                        val undoButton = UndoButton(leftX = leftXFloat,
                                                    topY = topYFloat,
                                                    rightX = rightXFloat,
                                                    bottomY = bottomYFloat)

                        assert(undoButton, leftXFloat, topYFloat, rightXFloat, bottomYFloat)
                    }
                }
            }
        }
    }
}