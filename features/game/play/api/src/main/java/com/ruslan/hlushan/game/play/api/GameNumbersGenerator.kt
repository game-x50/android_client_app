package com.ruslan.hlushan.game.play.api

import com.ruslan.hlushan.core.thread.SingleThreadSafety
import com.ruslan.hlushan.game.api.play.dto.GameSize
import java.util.Random

//todo
@SingleThreadSafety
internal class GameNumbersGenerator(
        private var gameSize: GameSize,
        private var sum: Int
) {

    private val random = Random()

    private var tempCounter = 1

    fun updateState(size: GameSize = gameSize, totalSum: Int = sum) {
        this.gameSize = size
        this.sum = totalSum
        tempCounter++
    }

    fun generateSingleNew(): Int {
        val position = random.nextInt(AVAILABLE_NUMBERS.lastIndex)
        return AVAILABLE_NUMBERS[position]
    }

    @SuppressWarnings("MagicNumber")
    fun generateNew(): GeneratedNumbers = if (tempCounter % 3 == 0) {
        GeneratedNumbers.Double(generateSingleNew(), generateSingleNew())
    } else {
        GeneratedNumbers.Single(generateSingleNew())
    }
}

sealed class GeneratedNumbers(open val firstNumber: Int) {
    data class Single(override val firstNumber: Int) : GeneratedNumbers(firstNumber)
    data class Double(override val firstNumber: Int, val secondNumber: Int) : GeneratedNumbers(firstNumber)
}