package com.greenfodor.diceroller.ui.utils

import androidx.compose.runtime.compositionLocalOf

/**
 * Whether shake-to-roll should be active, resolved from the persisted user setting AND device
 * support (an accelerometer). Provided by the root composable; defaults to `true` so previews
 * and screens used outside the app shell still behave normally.
 */
val LocalShakeToRollEnabled = compositionLocalOf { true }
