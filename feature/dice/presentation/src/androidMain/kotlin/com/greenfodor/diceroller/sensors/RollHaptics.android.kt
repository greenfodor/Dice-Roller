package com.greenfodor.diceroller.sensors

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/** Plays the roll vibration through the vibrator of [context]. */
private class VibratorRollHaptics(
    private val context: Context
) : RollHaptics {
    override val isSupported: Boolean by lazy { context.supportsHaptics() }

    override fun perform() = context.performRollHaptics()
}

@Composable
actual fun rememberRollHaptics(): RollHaptics {
    val context = LocalContext.current
    return remember(context) { VibratorRollHaptics(context) }
}
