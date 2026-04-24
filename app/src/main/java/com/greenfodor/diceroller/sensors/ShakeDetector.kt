package com.greenfodor.diceroller.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.annotation.VisibleForTesting
import kotlin.math.sqrt

/**
 * Detects device shake gestures using the accelerometer sensor.
 *
 * It calculates the total acceleration (excluding gravity) and compares it against
 * a threshold. To prevent multiple triggers from a single shake, it implements
 * a cooldown period.
 *
 * @param onShake Callback invoked when a shake is detected.
 */
class ShakeDetector(
    private val onShake: () -> Unit
) : SensorEventListener {
    private var lastShakeTime = 0L

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val acceleration =
            calculateAcceleration(
                x = event.values[0],
                y = event.values[1],
                z = event.values[2],
            )

        if (acceleration > SHAKE_THRESHOLD) {
            val now = System.currentTimeMillis()
            if (now - lastShakeTime > SHAKE_COOLDOWN_MS) {
                lastShakeTime = now
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) = Unit

    companion object {
        private const val SHAKE_THRESHOLD = 12f // m/s²
        private const val SHAKE_COOLDOWN_MS = 1_000L

        /**
         * Calculates the net acceleration felt by the device, excluding gravity.
         */
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        fun calculateAcceleration(
            x: Float,
            y: Float,
            z: Float
        ): Float = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
    }
}
