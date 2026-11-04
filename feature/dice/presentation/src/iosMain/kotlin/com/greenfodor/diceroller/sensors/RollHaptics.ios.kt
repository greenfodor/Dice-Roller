package com.greenfodor.diceroller.sensors

import androidx.compose.runtime.Composable
import platform.CoreHaptics.CHHapticEngine
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

/** Plays the roll haptics as one medium [UIImpactFeedbackGenerator] impact. */
private object ImpactRollHaptics : RollHaptics {
    override val isSupported: Boolean by lazy { CHHapticEngine.capabilitiesForHardware().supportsHaptics }

    override fun perform() {
        UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium).impactOccurred()
    }
}

@Composable
actual fun rememberRollHaptics(): RollHaptics = ImpactRollHaptics
