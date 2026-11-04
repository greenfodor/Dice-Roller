package com.greenfodor.diceroller.sensors

import androidx.compose.runtime.Composable

/** Haptic feedback played when dice are rolled. */
interface RollHaptics {
    /** Whether this device is capable of haptic feedback. */
    val isSupported: Boolean

    /** Plays the roll haptic pattern, unless the device or its current sound mode rules it out. */
    fun perform()
}

/** Returns the [RollHaptics] of the device the app runs on. */
@Composable
expect fun rememberRollHaptics(): RollHaptics
