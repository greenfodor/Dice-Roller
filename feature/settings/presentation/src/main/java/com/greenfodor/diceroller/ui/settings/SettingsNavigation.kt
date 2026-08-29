package com.greenfodor.diceroller.ui.settings

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** The settings screen. */
@Serializable
data object SettingsRoute : NavKey

/** The per-die color picker, reached from the settings screen. */
@Serializable
data object DiceColorsRoute : NavKey

/**
 * Adds the settings entries.
 *
 * Both entries collect the persisted settings from [viewModel] inside their own content, so a
 * change made on screen is rendered as soon as it is written. A `NavEntry` is rebuilt only when
 * the back stack changes, so a value read outside the entry content would stay frozen at whatever
 * it held when the entry was created. Values that describe the device rather than a setting
 * ([hapticFeedbackSupported], [shakeToRollSupported]) are fixed for the process and are passed in.
 *
 * @param onOpenDiceColors Pushes [DiceColorsRoute] onto the back stack.
 * @param onBack Pops the current entry.
 */
fun EntryProviderScope<NavKey>.settingsEntries(
    viewModel: SettingsViewModel,
    hapticFeedbackSupported: Boolean,
    shakeToRollSupported: Boolean,
    onOpenDiceColors: () -> Unit,
    onBack: () -> Unit
) {
    entry<SettingsRoute> {
        val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
        val hapticFeedbackEnabled by viewModel.hapticFeedbackEnabled.collectAsStateWithLifecycle()
        val shakeToRollEnabled by viewModel.shakeToRollEnabled.collectAsStateWithLifecycle()
        val d6FaceStyle by viewModel.d6FaceStyle.collectAsStateWithLifecycle()
        val mode = themeMode ?: return@entry

        SettingsScreen(
            state = SettingsUiState(
                themeMode = mode,
                hapticFeedbackEnabled = hapticFeedbackEnabled,
                hapticFeedbackSupported = hapticFeedbackSupported,
                shakeToRollEnabled = shakeToRollEnabled,
                shakeToRollSupported = shakeToRollSupported,
                d6FaceStyle = d6FaceStyle
            ),
            onThemeModeSelected = viewModel::setThemeMode,
            onHapticFeedbackToggled = viewModel::setHapticFeedbackEnabled,
            onShakeToRollToggled = viewModel::setShakeToRollEnabled,
            onD6FaceStyleSelected = viewModel::setD6FaceStyle,
            onOpenDiceColors = onOpenDiceColors,
            onClearRollHistory = viewModel::clearRollHistory,
            onBack = onBack
        )
    }
    entry<DiceColorsRoute> {
        val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
        val diceColorSettings by viewModel.diceColorSettings.collectAsStateWithLifecycle()
        val mode = themeMode ?: return@entry

        DiceColorsScreen(
            settings = diceColorSettings,
            themeMode = mode,
            onUseSingleColorToggled = viewModel::setUseSingleDiceColor,
            onSingleColorSelected = viewModel::setSingleDiceColor,
            onDiceColorSelected = viewModel::setDiceColor,
            onRestoreDefaults = viewModel::resetDiceColors,
            onBack = onBack
        )
    }
}
