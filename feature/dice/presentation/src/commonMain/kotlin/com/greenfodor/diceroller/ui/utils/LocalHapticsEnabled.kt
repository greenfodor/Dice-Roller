package com.greenfodor.diceroller.ui.utils

import androidx.compose.runtime.compositionLocalOf

/**
 * Whether roll haptics should fire, resolved from the persisted user setting AND device
 * support. Provided by the root composable; defaults to `true` so previews and screens used
 * outside the app shell still behave normally.
 */
val LocalHapticsEnabled = compositionLocalOf { true }
