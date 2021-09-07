@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.three_ten.extensions

import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import java.util.concurrent.TimeUnit

fun Long.fromUTCMillisToLocalDateTime(): LocalDateTime =
        Instant.ofEpochMilli(this).atOffset(ZoneOffset.UTC).toLocalDateTime()

fun Duration.hoursInNonFullDay(): Long =
        TimeUnit.SECONDS.toHours(seconds % TimeUnit.DAYS.toSeconds(1))

fun Duration.minutesInNonFullHour(): Long =
        TimeUnit.SECONDS.toMinutes(seconds % TimeUnit.HOURS.toSeconds(1))

fun Duration.secondsInNonFullMinute(): Long =
        (seconds % TimeUnit.MINUTES.toSeconds(1))

fun Duration?.orZero(): Duration = (this ?: Duration.ZERO)