package com.ruslan.hlushan.core.impl.utils

import androidx.annotation.IntRange
import java.lang.IllegalArgumentException
import kotlin.math.min

/**
 * Created by mac-131 on 4/9/18.
 */

//TODO: #write_unit_tests
class AlphabetComparator(private val alphabet: String?) : Comparator<String?> {

    @Suppress("MaxLineLength", "ComplexMethod")
    override fun compare(o1: String?, o2: String?): Int =
            when {
                (o1.isNullOrEmpty() && o2.isNullOrEmpty()) -> 0
                o1.isNullOrEmpty()                         -> 1
                o2.isNullOrEmpty()                         -> -1
                alphabet.isNullOrBlank()                   -> {
                    o1.compareTo(o2)
                }
                else                                       -> {
                    compareOnAllDataPresented(nonEmptyString1 = o1, nonEmptyString2 = o2, nonEmptyAlphabet = alphabet)
                }
            }
}

@SuppressWarnings("UnconditionalJumpStatementInLoop")
private fun compareOnAllDataPresented(
        nonEmptyString1: String,
        nonEmptyString2: String,
        nonEmptyAlphabet: String
): Int {
    @IntRange(from = 1) val minLength = min(nonEmptyString1.length, nonEmptyString2.length)

    for (index in 0 until minLength) {
        val char1 = nonEmptyString1[index]
        val char2 = nonEmptyString2[index]

        val alphabetContainsChar1 = nonEmptyAlphabet.contains(char1, ignoreCase = true)
        val alphabetContainsChar2 = nonEmptyAlphabet.contains(char2, ignoreCase = true)

        return when {
            (alphabetContainsChar1 && alphabetContainsChar2) -> {
                (nonEmptyAlphabet.indexOf(char1.uppercaseChar()) - nonEmptyAlphabet.indexOf(char2.uppercaseChar()))
            }
            alphabetContainsChar1                            -> {
                -1
            }
            alphabetContainsChar2                            -> {
                1
            }
            else                                             -> {
                nonEmptyString1.compareTo(nonEmptyString2)
            }
        }
    }

    throw IllegalArgumentException(
            "nonEmptyString1 = $nonEmptyString1," +
            " nonEmptyString2 = $nonEmptyString2," +
            " nonEmptyAlphabet = $nonEmptyAlphabet"
    )
}