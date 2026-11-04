package com.greenfodor.diceroller.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/** Whether this device has an accelerometer, required for shake-to-roll detection. */
fun Context.supportsShakeDetection(): Boolean {
    val sensorManager = getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    return sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
}

/** Forwards accelerometer events to [detector]. */
internal class ShakeSensorListener(
    private val detector: ShakeDetector
) : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        detector.onAcceleration(
            x = event.values[0],
            y = event.values[1],
            z = event.values[2]
        )
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) = Unit
}
