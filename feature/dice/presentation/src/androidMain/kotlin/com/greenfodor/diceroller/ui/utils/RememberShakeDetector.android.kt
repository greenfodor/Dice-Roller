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
import com.greenfodor.diceroller.sensors.ShakeSensorListener

/** Listens to the accelerometer through [SensorManager] at [SensorManager.SENSOR_DELAY_UI]. */
@SuppressLint("ComposableNaming")
@Suppress("ComposableNaming")
@Composable
actual fun rememberShakeDetector(enabled: Boolean, onShake: () -> Unit) {
    val context = LocalContext.current
    val currentOnShake by rememberUpdatedState(onShake)

    DisposableEffect(enabled) {
        if (!enabled) return@DisposableEffect onDispose {}

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = ShakeSensorListener(ShakeDetector { currentOnShake() })

        sensorManager?.registerListener(
            listener,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }
}
