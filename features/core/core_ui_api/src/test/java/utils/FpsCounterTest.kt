package utils

import com.ruslan.hlushan.core.ui.api.utils.FpsCounter
import com.ruslan.hlushan.core.ui.api.utils.frameTimeToFps
import com.ruslan.hlushan.test.utils.generateFakeBool
import com.ruslan.hlushan.test.utils.generateFakePositiveInt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.LinkedList

class FpsCounterTest {

    @Test
    fun `init state`() {
        val fpsCounter = FpsCounter(
                maxMeasurementPeriodsCount = 10,
                periodNanos = FpsCounter.NANOS_IN_SEC
        )

        assertFps(0.0, fpsCounter.currentSecondFps)
        assertFps(0.0, fpsCounter.fullObservedPeriodFps)
        assertEquals(0, fpsCounter.fullObservedPeriodNanos)
    }

    @Test
    fun `first frame`() {
        val fpsCounter = FpsCounter(
                maxMeasurementPeriodsCount = 10,
                periodNanos = FpsCounter.NANOS_IN_SEC
        )

        fpsCounter.doFrame(1000)

        assertFps(0.0, fpsCounter.currentSecondFps)
        assertFps(0.0, fpsCounter.fullObservedPeriodFps)
        assertEquals(0, fpsCounter.fullObservedPeriodNanos)
    }

    @Test
    fun `first frame zero`() {
        val fpsCounter = FpsCounter(
                maxMeasurementPeriodsCount = 10,
                periodNanos = FpsCounter.NANOS_IN_SEC
        )

        fpsCounter.doFrame(0)

        assertFps(0.0, fpsCounter.currentSecondFps)
        assertFps(0.0, fpsCounter.fullObservedPeriodFps)
        assertEquals(0, fpsCounter.fullObservedPeriodNanos)
    }

    @Test
    fun `2 frames, first at 0`() {
        val fpsCounter = FpsCounter(
                maxMeasurementPeriodsCount = 10,
                periodNanos = FpsCounter.NANOS_IN_SEC
        )

        val secondFrameTimestampNanos: Long = 10

        fpsCounter.doFrame(0)
        fpsCounter.doFrame(secondFrameTimestampNanos)

        val expectedFps = frameTimeToFps(secondFrameTimestampNanos.toDouble())

        assertFps(expectedFps, fpsCounter.currentSecondFps)
        assertFps(expectedFps, fpsCounter.fullObservedPeriodFps)
        assertEquals(secondFrameTimestampNanos, fpsCounter.fullObservedPeriodNanos)
    }

    @Test
    fun `2 frames, first at not 0`() {
        val fpsCounter = FpsCounter(
                maxMeasurementPeriodsCount = 10,
                periodNanos = FpsCounter.NANOS_IN_SEC
        )

        val firstFrameTimestampNanos: Long = 10
        val secondFrameTimestampNanos: Long = 30

        fpsCounter.doFrame(firstFrameTimestampNanos)
        fpsCounter.doFrame(secondFrameTimestampNanos)

        val expectedFullPeriodNanos = (secondFrameTimestampNanos - firstFrameTimestampNanos)
        val expectedFps = frameTimeToFps(expectedFullPeriodNanos.toDouble())

        assertFps(expectedFps, fpsCounter.currentSecondFps)
        assertFps(expectedFps, fpsCounter.fullObservedPeriodFps)
        assertEquals(expectedFullPeriodNanos, fpsCounter.fullObservedPeriodNanos)
    }

    @Test
    fun `many frames in one period with same rate`() {
        val fpsCounter = FpsCounter(
                maxMeasurementPeriodsCount = 10,
                periodNanos = FpsCounter.NANOS_IN_SEC
        )

        val frameTime: Long = (16 * 1000)
        val fps = frameTimeToFps(frameTime.toDouble())
        val firstFrameTimestampNanos = frameTime

        (firstFrameTimestampNanos..FpsCounter.NANOS_IN_SEC step frameTime).forEachIndexed { index, timestampNanos ->

            fpsCounter.doFrame(timestampNanos)

            val (expectedFps, expectedFullPeriodNanos) = if (index > 0) {
                fps to (timestampNanos - firstFrameTimestampNanos)
            } else {
                0.0 to 0L
            }

            assertFps(expectedFps, fpsCounter.currentSecondFps)
            assertFps(expectedFps, fpsCounter.fullObservedPeriodFps)
            assertEquals(expectedFullPeriodNanos, fpsCounter.fullObservedPeriodNanos)
        }
    }

    @Test
    fun `first frame in period but second exact end of period`() {
        val fpsCounter = FpsCounter(
                maxMeasurementPeriodsCount = 10,
                periodNanos = FpsCounter.NANOS_IN_SEC
        )

        val firstFrameTimestampNanos: Long = (FpsCounter.NANOS_IN_SEC / 4)
        val secondFrameTimestampNanos: Long = (firstFrameTimestampNanos + FpsCounter.NANOS_IN_SEC)

        fpsCounter.doFrame(firstFrameTimestampNanos)
        fpsCounter.doFrame(secondFrameTimestampNanos)

        val expectedFps = 1.toDouble()

        assertFps(expectedFps, fpsCounter.currentSecondFps)
        assertFps(expectedFps, fpsCounter.fullObservedPeriodFps)
        assertEquals(FpsCounter.NANOS_IN_SEC, fpsCounter.fullObservedPeriodNanos)
    }

    @Test
    fun `first frame in period but second after end of period`() {
        val fpsCounter = FpsCounter(
                maxMeasurementPeriodsCount = 10,
                periodNanos = FpsCounter.NANOS_IN_SEC
        )

        val firstFrameTimestampNanos: Long = (FpsCounter.NANOS_IN_SEC / 4)
        val secondFrameTimestampNanos: Long = ((firstFrameTimestampNanos + FpsCounter.NANOS_IN_SEC)
                                               + (FpsCounter.NANOS_IN_SEC / 3))

        fpsCounter.doFrame(firstFrameTimestampNanos)
        fpsCounter.doFrame(secondFrameTimestampNanos)

        val expectedFullPeriodNanos = (secondFrameTimestampNanos - firstFrameTimestampNanos)
        val expectedFps = frameTimeToFps(expectedFullPeriodNanos.toDouble())

        assertFps(expectedFps, fpsCounter.currentSecondFps)
        assertFps(expectedFps, fpsCounter.fullObservedPeriodFps)
        assertEquals(expectedFullPeriodNanos, fpsCounter.fullObservedPeriodNanos)
    }

    @Test
    fun `many frames in one period with different rate`() {
        val fpsCounter = FpsCounter(
                maxMeasurementPeriodsCount = 10,
                periodNanos = FpsCounter.NANOS_IN_SEC
        )

        val baseFrameTimestampNanos: Long = (16 * 1000)

        var currentTimestampNanos: Long = 0
        var framesCounter: Int = 0
        while (currentTimestampNanos <= FpsCounter.NANOS_IN_SEC) {

            fpsCounter.doFrame(currentTimestampNanos)

            val (expectedFps, expectedFullPeriodNanos) = if (framesCounter > 0) {
                frameTimeToFps(currentTimestampNanos.toDouble() / framesCounter) to currentTimestampNanos
            } else {
                0.0 to 0L
            }

            assertFps(expectedFps, fpsCounter.currentSecondFps)
            assertFps(expectedFps, fpsCounter.fullObservedPeriodFps)
            assertEquals(expectedFullPeriodNanos, fpsCounter.fullObservedPeriodNanos)

            framesCounter++
            currentTimestampNanos += ((generateFakePositiveInt() % baseFrameTimestampNanos) + baseFrameTimestampNanos)
        }
    }

    @Test
    fun `many frames in one period with different rate but last after period`() {
        val fpsCounter = FpsCounter(
                maxMeasurementPeriodsCount = 10,
                periodNanos = FpsCounter.NANOS_IN_SEC
        )

        val baseFrameTimestampNanos: Long = (16 * 1000)

        var firstFrameTimestampNanos: Long = 0

        var currentTimestampNanos: Long = 0
        var framesCounter: Int = 0

        do {
            currentTimestampNanos += (baseFrameTimestampNanos + (generateFakePositiveInt() % baseFrameTimestampNanos))

            if (framesCounter == 0) {
                firstFrameTimestampNanos = currentTimestampNanos
            }

            fpsCounter.doFrame(currentTimestampNanos)

            val (expectedFps, expectedFullPeriodNanos) = if (framesCounter > 0) {
                val fullPeriodNanos = (currentTimestampNanos - firstFrameTimestampNanos)
                val averageFrameTimestampNanos = (fullPeriodNanos.toDouble() / framesCounter)
                val fps = frameTimeToFps(averageFrameTimestampNanos)
                fps to fullPeriodNanos
            } else {
                0.0 to 0L
            }

            assertFps(expectedFps, fpsCounter.currentSecondFps)
            assertFps(expectedFps, fpsCounter.fullObservedPeriodFps)
            assertEquals(expectedFullPeriodNanos, fpsCounter.fullObservedPeriodNanos)

            framesCounter++
        } while (currentTimestampNanos <= FpsCounter.NANOS_IN_SEC)

        assertTrue(currentTimestampNanos > FpsCounter.NANOS_IN_SEC)
    }

    @Test
    fun `many frames in many periods with same rate`() =
            `assert many frames in many periods with same frame`(
                    frameTime = (16 * 1000),
                    periodsCount = 10,
                    testedPeriodsCount = 50
            )

    @SuppressWarnings("LongMethod")
    @Test
    fun `many frames in many periods with different rate`() {
        val periodsCount = 10

        val fpsCounter = FpsCounter(
                maxMeasurementPeriodsCount = periodsCount,
                periodNanos = FpsCounter.NANOS_IN_SEC
        )

        val testedPeriodsCount = (20 * periodsCount)
        val lastAvailablePeriodTimestampNanos = (testedPeriodsCount * FpsCounter.NANOS_IN_SEC)

        val initialTimestampDelay: Long = (5 * 1000)
        val baseFrameTimestampNanos: Long = (16 * 1000)

        val timestamps = generateSequence(initialTimestampDelay) { prev ->
            val module = if (generateFakeBool()) {
                FpsCounter.NANOS_IN_SEC
            } else {
                baseFrameTimestampNanos
            }
            (prev + (generateFakePositiveInt() % module))
        }
                .takeWhile { currentTimestampNanos -> (currentTimestampNanos <= lastAvailablePeriodTimestampNanos) }
                .toList()

        val chunkedTimestamps: LinkedList<LinkedList<Long>> = LinkedList()

        for (timestampNanos in timestamps) {

            if (chunkedTimestamps.isEmpty()) {
                val newChunkList = LinkedList<Long>()
                newChunkList.add(timestampNanos)

                chunkedTimestamps.add(newChunkList)
            } else {
                val lastChunkList = chunkedTimestamps.last()
                lastChunkList.add(timestampNanos)

                if ((timestampNanos - lastChunkList.first()) > FpsCounter.NANOS_IN_SEC) {
                    val newChunkList = LinkedList<Long>()
                    newChunkList.add(timestampNanos)

                    chunkedTimestamps.add(newChunkList)
                }
            }
        }

        timestamps.forEachIndexed { index, timestampNanos ->

            fpsCounter.doFrame(timestampNanos)

            val expectedFullPeriodNanos = if (index > 0) {
                val chunkedListIndex = chunkedTimestamps.indexOfFirst { list -> (list.first() == timestampNanos) }

                if (chunkedListIndex >= periodsCount) {
                    chunkedTimestamps.removeAt(0)
                }
                (timestampNanos - chunkedTimestamps.first().first())
            } else {
                0L
            }

            val currentSecondFps = if (index > 0) {
                val currentSecondChunk = chunkedTimestamps.first { list -> (list.binarySearch(timestampNanos) >= 0) }
                val currentSecondPeriod = (timestampNanos - currentSecondChunk.first()).toDouble()
                val frameTimePerCurrentSecondPeriod = (currentSecondPeriod / currentSecondChunk.indexOf(timestampNanos))
                frameTimeToFps(frameTimePerCurrentSecondPeriod)
            } else {
                0.0
            }

            val fullPeriodFps = if (index > 0) {
                val countOfFrames = chunkedTimestamps
                        .asSequence()
                        .flatten()
                        .takeWhile { timestamp -> (timestamp <= timestampNanos) }
                        .toSet()
                        .size
                        .minus(1)
                val fullPeriod = (timestampNanos - chunkedTimestamps.first().first()).toDouble()
                val frameTimePerFullPeriod = (fullPeriod / countOfFrames)
                frameTimeToFps(frameTimePerFullPeriod)
            } else {
                0.0
            }

            assertFps(currentSecondFps, fpsCounter.currentSecondFps)
            assertFps(fullPeriodFps, fpsCounter.fullObservedPeriodFps)
            assertEquals("$timestampNanos", expectedFullPeriodNanos, fpsCounter.fullObservedPeriodNanos)
        }
    }

    @Test
    fun `many frames in many periods with same frame time that is greater than period`() =
            `assert many frames in many periods with same frame`(
                    frameTime = (1.3 * FpsCounter.NANOS_IN_SEC).toLong(),
                    periodsCount = 10,
                    testedPeriodsCount = 200
            )
}

private fun `assert many frames in many periods with same frame`(
        frameTime: Long,
        periodsCount: Int,
        testedPeriodsCount: Int
) {
    val fpsCounter = FpsCounter(
            maxMeasurementPeriodsCount = periodsCount,
            periodNanos = FpsCounter.NANOS_IN_SEC
    )

    val fps = frameTimeToFps(frameTime.toDouble())

    val initialTimestampDelay: Long = (frameTime / 3)

    val framesCount = ((testedPeriodsCount * FpsCounter.NANOS_IN_SEC) / frameTime)
    val lastFrameTimestamp = (initialTimestampDelay + (framesCount * frameTime))

    val maxObservedSinglePeriodNanos: Long = (((FpsCounter.NANOS_IN_SEC / frameTime) + 1) * frameTime)
    val periodsLastFrameTimestampNanos = (0..testedPeriodsCount)
            .map { i -> (initialTimestampDelay + (i * maxObservedSinglePeriodNanos)) }
            .toMutableList()

    (initialTimestampDelay..lastFrameTimestamp step frameTime).forEachIndexed { index, timestampNanos ->

        fpsCounter.doFrame(timestampNanos)

        val expectedFps = if (index > 0) {
            fps
        } else {
            0.0
        }

        val expectedFullPeriodNanos = if (index > 0) {
            val searchIndex = periodsLastFrameTimestampNanos.binarySearch { elem -> (elem - timestampNanos).toInt() }
            if (searchIndex >= periodsCount) {
                periodsLastFrameTimestampNanos.removeAt(0)
            }
            (timestampNanos - periodsLastFrameTimestampNanos.first())
        } else {
            0L
        }

        assertFps(expectedFps, fpsCounter.currentSecondFps)
        assertFps(expectedFps, fpsCounter.fullObservedPeriodFps)
        assertEquals(expectedFullPeriodNanos, fpsCounter.fullObservedPeriodNanos)
    }
}

fun assertFps(expected: Double, actual: Double, message: String? = null) =
        assertEquals(message, expected, actual, 0.05)