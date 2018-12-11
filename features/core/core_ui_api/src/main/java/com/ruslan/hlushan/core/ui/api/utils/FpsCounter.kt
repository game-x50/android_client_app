package com.ruslan.hlushan.core.ui.api.utils

import androidx.annotation.IntRange
import androidx.annotation.VisibleForTesting
import java.util.LinkedList

class FpsCounter(
        @IntRange(from = 1) private val maxMeasurementPeriodsCount: Int,
        @IntRange(from = 1) private val periodNanos: Long
) {

    @SuppressWarnings("ClassOrdering")
    companion object {
        const val NANOS_IN_SEC: Long = (1_000 * 1000)
    }

    private val periods = LinkedList<PeriodInfo>()

    var fullObservedPeriodNanos: Long = 0
        private set

    var fullObservedPeriodFps: Double = 0.0
        private set

    var currentSecondFps: Double = 0.0
        private set

    fun doFrame(frameTimestampNanos: Long) {

        if (periods.isEmpty()) {
            periods.add(PeriodInfo(startNanos = frameTimestampNanos))
        } else {
            val lastItem = periods.last()

            lastItem.onFrame(frameTimestampNanos = frameTimestampNanos)
            currentSecondFps = lastItem.fpsPerPeriod

            if ((frameTimestampNanos - lastItem.startNanos) > periodNanos) {

                if (periods.size == maxMeasurementPeriodsCount) {
                    periods.removeFirst()
                }

                periods.add(PeriodInfo(startNanos = frameTimestampNanos))
            }
        }

        recalculateFullPeriodFps(frameTimestampNanos = frameTimestampNanos)
//todo
//        Choreographer.getInstance().postFrameCallback(this)
    }

    private fun recalculateFullPeriodFps(frameTimestampNanos: Long) {
        val totalFramesCount = periods.sumBy { p -> p.countFrames }
        if (totalFramesCount > 0) {
            fullObservedPeriodNanos = (frameTimestampNanos - periods.first().startNanos)
            val averageFrameTimePerPeriod = (fullObservedPeriodNanos.toDouble() / totalFramesCount)
            fullObservedPeriodFps = frameTimeToFps(averageFrameTimePerPeriod)
        }
    }
}

private class PeriodInfo(
        val startNanos: Long
) {
    var countFrames: Int = 0

    var fpsPerPeriod: Double = 0.0
        private set

    fun onFrame(frameTimestampNanos: Long) {
        this.countFrames++

        val averageFrameTimePerPeriod = ((frameTimestampNanos - this.startNanos).toDouble() / countFrames)

        this.fpsPerPeriod = frameTimeToFps(averageFrameTimePerPeriod)
    }
}

@VisibleForTesting
internal fun frameTimeToFps(averageFrameTimePerPeriod: Double): Double =
        (FpsCounter.NANOS_IN_SEC / averageFrameTimePerPeriod)