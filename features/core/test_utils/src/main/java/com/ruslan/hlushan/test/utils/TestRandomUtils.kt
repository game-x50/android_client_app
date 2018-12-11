package com.ruslan.hlushan.test.utils

import org.threeten.bp.Duration
import org.threeten.bp.Instant
import java.util.Random
import kotlin.math.absoluteValue

private val random = Random()

fun generateFakeBool(): Boolean = random.nextBoolean()

fun generateFakeStringId(): String = generateFakeLong().toString()
fun generateFakeStringIdOrNull(): String? = generateFakeStringId().takeIf { generateFakeBool() }

fun generateFakeInt(): Int = random.nextInt()
fun generateFakeIntOrNull(): Int? = generateFakeInt().takeIf { generateFakeBool() }
fun generateFakePositiveInt(): Int = generateFakeInt().absoluteValue
fun generateFakePositiveIntOrNull(): Int? = generateFakePositiveInt().takeIf { generateFakeBool() }

fun generateFakeLong(): Long = random.nextLong()
fun generateFakeLongOrNull(): Long? = generateFakeLong().takeIf { generateFakeBool() }
fun generateFakePositiveLong(): Long = generateFakeLong().absoluteValue
fun generateFakePositiveLongOrNull(): Long? = generateFakePositiveLong().takeIf { generateFakeBool() }

fun generateFakeInstantTimestamp(): Instant = Instant.ofEpochSecond(random.nextInt(100_000_000).toLong())
fun generateFakeDuration(): Duration = Duration.ofSeconds(random.nextInt(100_000_000).toLong())