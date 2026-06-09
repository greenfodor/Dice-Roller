package com.greenfodor.diceroller.ui.settings

import com.greenfodor.diceroller.data.D6FaceStyle
import com.greenfodor.diceroller.data.ThemeMode

/**
 * Immutable snapshot of every value [SettingsScreen] renders. Bundling the values keeps the
 * screen's parameter list small as settings grow; the `*Supported` flags reflect device
 * capability so the matching control can be disabled when unavailable.
 */
data class SettingsUiState(
    val themeMode: ThemeMode,
    val hapticFeedbackEnabled: Boolean,
    val hapticFeedbackSupported: Boolean,
    val shakeToRollEnabled: Boolean,
    val shakeToRollSupported: Boolean,
    val d6FaceStyle: D6FaceStyle
)
