package com.greenfodor.diceroller.sensors

import androidx.compose.runtime.Composable

/** Whether this device has an accelerometer, required for shake-to-roll detection. */
@Composable
expect fun rememberShakeDetectionSupported(): Boolean
