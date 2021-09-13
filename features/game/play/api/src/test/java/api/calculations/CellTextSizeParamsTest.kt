package api.calculations

import com.ruslan.hlushan.game.play.api.calculations.CellTextSizeParams
import com.ruslan.hlushan.game.play.api.calculations.getCellTextSizeForText
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.StringBuilder

class CellTextSizeParamsTest {

    @Test
    fun equality() {
        (1..10)
                .map { i ->
                    (1..i)
                            .map { Pair(i, i.toFloat()) }
                            .toMap()
                }
                .forEach { map ->
                    assertEquals(CellTextSizeParams(map), CellTextSizeParams(map))
                }
    }

    @Test
    fun createDefault() {
        assertEquals(CellTextSizeParams(emptyMap()), CellTextSizeParams.createDefault())
    }

    @Test
    fun cellTextSizeForTextForEmpty() {
        val emptyParams = CellTextSizeParams(emptyMap())
        arrayOf("", "1", "22", "333", "4444", "s;aldfksdl").forEach { string ->
            assertEquals(0f, emptyParams.getCellTextSizeForText(string))
        }
    }

    @Test
    fun cellTextSizeForTextSameLength() {
        val checkLength = 10
        val checkLSize = 12f

        val params = CellTextSizeParams(mapOf(Pair(checkLength, checkLSize), Pair(checkLength + 1, 123f)))

        (0 until 10)
                .map { i ->
                    val buider = StringBuilder()
                    repeat(checkLength) {
                        buider.append(i)
                    }
                    buider.toString()
                }.forEach { string ->
                    assertEquals(checkLSize, params.getCellTextSizeForText(string))
                }
    }

    @Test
    fun cellTextSizeForTextLengthLessThenMin() {
        val minLength = 10
        val minSize = 12f

        val params = CellTextSizeParams(mapOf(Pair(minLength, minSize), Pair(minLength + 1, minSize + 1)))

        (0 until minLength)
                .map { length ->
                    val buider = StringBuilder()
                    repeat(length) {
                        buider.append(length)
                    }
                    buider.toString()
                }.forEach { string ->
                    assertEquals(minSize, params.getCellTextSizeForText(string))
                }
    }

    @Test
    fun cellTextSizeForTextLengthMoreThenMax() {
        val maxLength = 10
        val maxSize = 12f

        val params = CellTextSizeParams(mapOf(Pair(maxLength, maxSize), Pair(maxLength - 1, maxSize - 1)))

        (maxLength + 1..maxLength + 10)
                .map { length ->
                    val buider = StringBuilder()
                    repeat(length) {
                        buider.append(length)
                    }
                    buider.toString()
                }.forEach { string ->
                    assertEquals(maxSize, params.getCellTextSizeForText(string))
                }
    }
}