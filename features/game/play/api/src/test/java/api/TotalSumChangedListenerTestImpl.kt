package api

import com.ruslan.hlushan.game.play.api.listeners.TotalSumChangedListener
import org.junit.Assert.assertEquals

class TotalSumChangedListenerTestImpl : TotalSumChangedListener {

    private val actualSums: MutableList<Int> = mutableListOf()

    private val expectedSums: MutableList<Int> = mutableListOf()
    private var lastExpectedSum: Int = expectedSums.sum()

    override fun onTotalSumChanged(totalSum: Int) {
        actualSums.add(totalSum)
    }

    fun addNextExpectedNumber(number: Int) {
        lastExpectedSum += number
        expectedSums.add(lastExpectedSum)
    }

    fun addNumbersForCombo(numbersBeforeCombo: List<Int>, finalCombo: Int) {
        numbersBeforeCombo.forEach { number ->
            addNextExpectedNumber(number)
        }

        addNextExpectedNumber(finalCombo - numbersBeforeCombo.sum())
    }

    fun assert() =
            assertEquals("expectedSums.size = ${expectedSums.size}, actualSums.size = ${actualSums.size}",
                         expectedSums,
                         actualSums)
}