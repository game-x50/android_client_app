package view.calculations

import com.ruslan.hlushan.game.core.api.play.dto.GameSize
import com.ruslan.hlushan.game.play.ui.view.calculations.GameViewParams
import com.ruslan.hlushan.game.play.ui.view.calculations.countRowsAndColumns
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.sqrt

/**
 * @author Ruslan Hlushan on 2019-09-05
 */
class GameViewParamsTest {

    @Test
    fun equality() =
            assertForDifferentParams { params1, gameSize, countNewElements, tinyLine, boldLine, marginInCellsBetweenTableAndNewElementsLine ->

                val params2 = GameViewParams(
                        gameSize = gameSize,
                        countNewElements = countNewElements,
                        tinyLine = tinyLine,
                        boldLine = boldLine,
                        marginInCellsBetweenTableAndNewElementsLine = marginInCellsBetweenTableAndNewElementsLine)

                assertEquals(params2, params1)
            }

    @Test
    fun defaultParams() =
            assertForGameSizesAndCountNewElements { gameSize, countNewElements ->
                assertEquals(
                        GameViewParams(
                                gameSize = gameSize,
                                countNewElements = countNewElements,
                                tinyLine = 1,
                                boldLine = 3,
                                marginInCellsBetweenTableAndNewElementsLine = 0.5
                        ),
                        GameViewParams(
                                gameSize = gameSize,
                                countNewElements = countNewElements
                        )
                )
            }

    @Test
    fun countRowsAndColumns() =
            assertForGameSizesAndCountNewElements { gameSize, countNewElements ->
                assertEquals(
                        gameSize.countRowsAndColumns,
                        GameViewParams(gameSize = gameSize, countNewElements = countNewElements).countRowsAndColumns
                )
            }

    @Test
    fun countBigRowsAndColumns() =
            assertForGameSizesAndCountNewElements { gameSize, countNewElements ->
                assertEquals(
                        sqrt(gameSize.countRowsAndColumns.toDouble()).toInt(),
                        GameViewParams(gameSize = gameSize, countNewElements = countNewElements).countBigRowsAndColumns
                )
            }

    @Test
    fun countRowsAndColumnsIncludeSums() =
            assertForGameSizesAndCountNewElements { gameSize, countNewElements ->
                assertEquals(
                        (gameSize.countRowsAndColumns + 1),
                        GameViewParams(gameSize = gameSize, countNewElements = countNewElements).countRowsAndColumnsIncludeSums
                )
            }

    @Test
    fun totalGameLinesSize() =
            assertForDifferentParams { params, gameSize, countNewElements, tinyLine, boldLine, marginInCellsBetweenTableAndNewElementsLine ->

                val countOfAllLines: Int = (params.countRowsAndColumnsIncludeSums + 1)
                val countOfBoldLines: Int = (params.countBigRowsAndColumns + 1)
                val countOfTinyLines: Int = (countOfAllLines - countOfBoldLines)
                val expectedTotalGameLinesSize: Int = ((countOfTinyLines * tinyLine) + (countOfBoldLines * boldLine))

                assertEquals(expectedTotalGameLinesSize, params.totalGameLinesSize)
            }

    private fun assertForGameSizesAndCountNewElements(assert: (GameSize, countNewElements: Int) -> Unit) =
            GameSize.values().forEach { gameSize ->
                for (countNewElements in 1..100) {
                    assert(gameSize, countNewElements)
                }
            }

    private fun assertForDifferentParams(
            assert: (GameViewParams,
                     GameSize,
                     countNewElements: Int,
                     tinyLine: Int,
                     boldLine: Int,
                     marginInCellsBetweenTableAndNewElementsLine: Double) -> Unit
    ) =
            assertForGameSizesAndCountNewElements { gameSize, countNewElements ->
                for (tinyLine in 0..10) {
                    for (boldLine in 0..10) {
                        (0..10)
                                .map { margin -> margin.toDouble() / 2 }
                                .forEach { marginInCellsBetweenTableAndNewElementsLine ->

                                    val params = GameViewParams(
                                            gameSize = gameSize,
                                            countNewElements = countNewElements,
                                            tinyLine = tinyLine,
                                            boldLine = boldLine,
                                            marginInCellsBetweenTableAndNewElementsLine = marginInCellsBetweenTableAndNewElementsLine
                                    )

                                    assert(params, gameSize, countNewElements, tinyLine, boldLine, marginInCellsBetweenTableAndNewElementsLine)
                                }
                    }
                }
            }
}