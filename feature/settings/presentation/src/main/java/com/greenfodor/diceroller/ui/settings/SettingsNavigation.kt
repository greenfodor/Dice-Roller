package com.greenfodor.diceroller.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
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
 * Each entry resolves its own [SettingsViewModel] through [hiltViewModel], so the instance is
 * scoped to that entry and cleared when the user leaves it.
 *
 * @param onOpenDiceColors Pushes [DiceColorsRoute] onto the back stack.
 * @param onBack Pops the current entry.
 */
fun EntryProviderScope<NavKey>.settingsEntries(
    hapticFeedbackSupported: Boolean,
    shakeToRollSupported: Boolean,
    onOpenDiceColors: () -> Unit,
    onBack: () -> Unit
) {
    entry<SettingsRoute> {
        SettingsRoot(
            hapticFeedbackSupported = hapticFeedbackSupported,
            shakeToRollSupported = shakeToRollSupported,
            onOpenDiceColors = onOpenDiceColors,
            onBack = onBack
        )
    }
    entry<DiceColorsRoute> {
        DiceColorsRoot(onBack = onBack)
    }
}

@Composable
private fun SettingsRoot(
    hapticFeedbackSupported: Boolean,
    shakeToRollSupported: Boolean,
    onOpenDiceColors: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val hapticFeedbackEnabled by viewModel.hapticFeedbackEnabled.collectAsStateWithLifecycle()
    val shakeToRollEnabled by viewModel.shakeToRollEnabled.collectAsStateWithLifecycle()
    val d6FaceStyle by viewModel.d6FaceStyle.collectAsStateWithLifecycle()

    val mode = themeMode ?: return

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

@Composable
private fun DiceColorsRoot(onBack: () -> Unit) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val diceColorSettings by viewModel.diceColorSettings.collectAsStateWithLifecycle()

    val mode = themeMode ?: return

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
