package view

import com.ruslan.hlushan.game.play.ui.view.listeners.TotalSumChangedListener
import org.junit.Assert.assertEquals

/**
 * @author Ruslan Hlushan on 2019-10-18
 */
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