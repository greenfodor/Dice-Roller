package com.greenfodor.diceroller.sensors

import androidx.compose.runtime.Composable

/** Haptics of a device without a vibrator. */
private object NoRollHaptics : RollHaptics {
    override val isSupported: Boolean = false

    override fun perform() = Unit
}

@Composable
actual fun rememberRollHaptics(): RollHaptics = NoRollHaptics
