package com.greenfodor.diceroller.sensors

import kotlin.math.sqrt
import kotlin.time.Clock

/**
 * Detects device shake gestures from accelerometer readings.
 *
 * It calculates the total acceleration (excluding gravity) and compares it against
 * a threshold. To prevent multiple triggers from a single shake, it implements
 * a cooldown period measured with [clock].
 *
 * @param clock Source of the current time for the cooldown.
 * @param onShake Callback invoked when a shake is detected.
 */
class ShakeDetector(
    private val clock: Clock = Clock.System,
    private val onShake: () -> Unit
) {
    private var lastShakeTime = 0L

    /**
     * Feeds one accelerometer reading, in m/s² along the device's three axes with gravity
     * included, and invokes `onShake` when it completes a shake.
     */
    fun onAcceleration(
        x: Float,
        y: Float,
        z: Float
    ) {
        val acceleration = calculateAcceleration(x = x, y = y, z = z)

        if (acceleration > SHAKE_THRESHOLD) {
            val now = clock.now().toEpochMilliseconds()
            if (now - lastShakeTime > SHAKE_COOLDOWN_MS) {
                lastShakeTime = now
                onShake()
            }
        }
    }

    companion object {
        /** Standard gravity in m/s². */
        const val STANDARD_GRAVITY = 9.80665f

        internal const val SHAKE_THRESHOLD = 12f // m/s²
        internal const val SHAKE_COOLDOWN_MS = 1_000L

        /**
         * Calculates the net acceleration felt by the device, excluding gravity.
         */
        internal fun calculateAcceleration(
            x: Float,
            y: Float,
            z: Float
        ): Float = sqrt(x * x + y * y + z * z) - STANDARD_GRAVITY
    }
}
