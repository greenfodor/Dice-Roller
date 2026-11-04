package com.greenfodor.diceroller.ui.utils

import androidx.compose.runtime.Composable
import com.greenfodor.diceroller.sensors.ShakeDetector

/**
 * Remembers and manages the lifecycle of a [ShakeDetector].
 *
 * This composable starts listening to the accelerometer when entered and
 * automatically stops when leaving the composition. When [enabled] is `false` nothing
 * listens, so a disabled shake-to-roll setting incurs no sensor cost.
 *
 * @param enabled Whether the shake gesture should be listened for.
 * @param onShake Callback to be invoked when a shake gesture is detected.
 */
@Suppress("ComposableNaming")
@Composable
expect fun rememberShakeDetector(enabled: Boolean, onShake: () -> Unit)
