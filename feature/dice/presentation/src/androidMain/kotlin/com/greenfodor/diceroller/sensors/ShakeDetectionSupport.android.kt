package com.greenfodor.diceroller.sensors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberShakeDetectionSupported(): Boolean {
    val context = LocalContext.current
    return remember(context) { context.supportsShakeDetection() }
}
