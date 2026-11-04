package com.greenfodor.diceroller.ui.utils

import androidx.compose.runtime.Composable

/** Does nothing: desktop has no accelerometer. */
@Suppress("ComposableNaming")
@Composable
actual fun rememberShakeDetector(enabled: Boolean, onShake: () -> Unit) = Unit
