package com.greenfodor.diceroller.ui.settings

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.greenfodor.diceroller.data.DiceColorSettings
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
 * Both entries render from the [viewModel], [state] and [diceColorSettings] hoisted in by the
 * caller, so they show the persisted settings the moment they are composed rather than after a
 * ViewModel of their own has loaded them.
 *
 * @param onOpenDiceColors Pushes [DiceColorsRoute] onto the back stack.
 * @param onBack Pops the current entry.
 */
fun EntryProviderScope<NavKey>.settingsEntries(
    viewModel: SettingsViewModel,
    state: SettingsUiState,
    diceColorSettings: DiceColorSettings,
    onOpenDiceColors: () -> Unit,
    onBack: () -> Unit
) {
    entry<SettingsRoute> {
        SettingsScreen(
            state = state,
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
        DiceColorsScreen(
            settings = diceColorSettings,
            themeMode = state.themeMode,
            onUseSingleColorToggled = viewModel::setUseSingleDiceColor,
            onSingleColorSelected = viewModel::setSingleDiceColor,
            onDiceColorSelected = viewModel::setDiceColor,
            onRestoreDefaults = viewModel::resetDiceColors,
            onBack = onBack
        )
    }
}
