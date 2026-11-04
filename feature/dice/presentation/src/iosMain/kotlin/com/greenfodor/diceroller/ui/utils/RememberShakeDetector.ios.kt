package com.greenfodor.diceroller.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.greenfodor.diceroller.sensors.ShakeDetector
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue

/** Interval between accelerometer updates, in seconds. */
private const val ACCELEROMETER_UPDATE_INTERVAL_SECONDS = 1.0 / 15.0

/**
 * Listens to the accelerometer through [CMMotionManager] accelerometer updates on the main queue.
 * Readings arrive in g and are converted to m/s² for [ShakeDetector].
 */
@OptIn(ExperimentalForeignApi::class)
@Suppress("ComposableNaming")
@Composable
actual fun rememberShakeDetector(enabled: Boolean, onShake: () -> Unit) {
    val currentOnShake by rememberUpdatedState(onShake)

    DisposableEffect(enabled) {
        if (!enabled) return@DisposableEffect onDispose {}

        val motionManager = CMMotionManager()
        if (!motionManager.accelerometerAvailable) return@DisposableEffect onDispose {}

        val detector = ShakeDetector { currentOnShake() }
        motionManager.accelerometerUpdateInterval = ACCELEROMETER_UPDATE_INTERVAL_SECONDS
        motionManager.startAccelerometerUpdatesToQueue(NSOperationQueue.mainQueue) { data, _ ->
            data?.acceleration?.useContents {
                detector.onAcceleration(
                    x = (x * ShakeDetector.STANDARD_GRAVITY).toFloat(),
                    y = (y * ShakeDetector.STANDARD_GRAVITY).toFloat(),
                    z = (z * ShakeDetector.STANDARD_GRAVITY).toFloat()
                )
            }
        }

        onDispose {
            motionManager.stopAccelerometerUpdates()
        }
    }
}
