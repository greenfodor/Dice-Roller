package com.greenfodor.diceroller.sensors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.CoreMotion.CMMotionManager

@Composable
actual fun rememberShakeDetectionSupported(): Boolean = remember { CMMotionManager().accelerometerAvailable }
