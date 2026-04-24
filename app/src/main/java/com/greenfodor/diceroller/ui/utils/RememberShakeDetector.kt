package com.greenfodor.diceroller.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.greenfodor.diceroller.sensors.ShakeDetector

/**
 * Remembers and manages the lifecycle of a [ShakeDetector].
 *
 * This composable registers a sensor listener for the accelerometer when entered and
 * automatically unregisters it when leaving the composition.
 *
 * @param onShake Callback to be invoked when a shake gesture is detected.
 */
@SuppressLint("ComposableNaming")
@Composable
fun rememberShakeDetector(onShake: () -> Unit) {
    val context = LocalContext.current
    val currentOnShake by rememberUpdatedState(onShake)

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val detector = ShakeDetector { currentOnShake() }

        sensorManager?.registerListener(
            detector,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )

        onDispose {
            sensorManager?.unregisterListener(detector)
        }
    }
}