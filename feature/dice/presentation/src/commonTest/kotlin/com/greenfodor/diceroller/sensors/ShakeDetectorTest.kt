package com.greenfodor.diceroller.sensors

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

/** [Clock] that reads [nowMillis], moved forward by the test. */
private class FakeClock(
    var nowMillis: Long
) : Clock {
    override fun now(): Instant = Instant.fromEpochMilliseconds(nowMillis)
}

class ShakeDetectorTest {
    @Test
    fun `static gravity results in zero acceleration`() {
        // Device sitting flat on table
        val acc = ShakeDetector.calculateAcceleration(0f, 0f, ShakeDetector.STANDARD_GRAVITY)
        assertEquals(0f, acc, 0.01f)
    }

    @Test
    fun `shake on X axis results in positive acceleration`() {
        // Device moved quickly sideways
        val acc = ShakeDetector.calculateAcceleration(15f, 0f, ShakeDetector.STANDARD_GRAVITY)

        // sqrt(15^2 + 9.8^2) - 9.8 = sqrt(225 + 96.04) - 9.8 = 17.91 - 9.8 = 8.11
        assertEquals(8.11f, acc, 0.01f)
    }

    @Test
    fun `large shake exceeds threshold`() {
        val acc = ShakeDetector.calculateAcceleration(25f, 25f, 25f)
        // threshold is 12f
        assertTrue(acc > 12f, "Large shake should be significant")
    }

    @Test
    fun `a reading above the threshold reports a shake`() {
        var shakes = 0
        val detector = ShakeDetector(FakeClock(START_MILLIS)) { shakes++ }

        detector.onAcceleration(25f, 25f, 25f)

        assertEquals(1, shakes)
    }

    @Test
    fun `a reading at or below the threshold reports nothing`() {
        var shakes = 0
        val detector = ShakeDetector(FakeClock(START_MILLIS)) { shakes++ }

        detector.onAcceleration(0f, 0f, ShakeDetector.STANDARD_GRAVITY)
        detector.onAcceleration(0f, 0f, ShakeDetector.STANDARD_GRAVITY + ShakeDetector.SHAKE_THRESHOLD)

        assertEquals(0, shakes)
    }

    @Test
    fun `a second shake within the cooldown is ignored`() {
        var shakes = 0
        val clock = FakeClock(START_MILLIS)
        val detector = ShakeDetector(clock) { shakes++ }

        detector.onAcceleration(25f, 25f, 25f)
        clock.nowMillis += ShakeDetector.SHAKE_COOLDOWN_MS
        detector.onAcceleration(25f, 25f, 25f)

        assertEquals(1, shakes)
    }

    @Test
    fun `a shake after the cooldown is reported again`() {
        var shakes = 0
        val clock = FakeClock(START_MILLIS)
        val detector = ShakeDetector(clock) { shakes++ }

        detector.onAcceleration(25f, 25f, 25f)
        clock.nowMillis += ShakeDetector.SHAKE_COOLDOWN_MS + 1
        detector.onAcceleration(25f, 25f, 25f)

        assertEquals(2, shakes)
    }

    private companion object {
        const val START_MILLIS = 1_786_999_000_000L
    }
}
