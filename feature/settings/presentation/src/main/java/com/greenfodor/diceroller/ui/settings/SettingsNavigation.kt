package com.greenfodor.diceroller.ui.settings

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import com.greenfodor.diceroller.ui.DiceConstants
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
 * Both entries collect the persisted settings from [viewModel] inside their own content. Values
 * that describe the device rather than a setting ([hapticFeedbackSupported],
 * [shakeToRollSupported]) are passed in.
 *
 * Both entries carry [slideTransitionMetadata], so they slide in over whatever they were opened
 * from and slide back out on pop.
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
    val slideMetadata = slideTransitionMetadata()

    entry<SettingsRoute>(metadata = slideMetadata) {
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
    entry<DiceColorsRoute>(metadata = slideMetadata) {
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

/**
 * Entry metadata that slides the incoming screen in from the right edge while the outgoing one
 * slides off to the left, and reverses both directions on pop and on a predictive back gesture.
 */
private fun slideTransitionMetadata(): Map<String, Any> {
    val slideSpec = tween<IntOffset>(DiceConstants.SCREEN_TRANSITION_DURATION_MILLIS)
    val push: AnimatedContentTransitionScope<Scene<*>>.() -> ContentTransform = {
        ContentTransform(
            targetContentEnter = slideInHorizontally(slideSpec) { width -> width },
            initialContentExit = slideOutHorizontally(slideSpec) { width -> -width }
        )
    }
    val pop: AnimatedContentTransitionScope<Scene<*>>.() -> ContentTransform = {
        ContentTransform(
            targetContentEnter = slideInHorizontally(slideSpec) { width -> -width },
            initialContentExit = slideOutHorizontally(slideSpec) { width -> width }
        )
    }

    return NavDisplay.transitionSpec(push) +
        NavDisplay.popTransitionSpec(pop) +
        NavDisplay.predictivePopTransitionSpec { pop() }
}
