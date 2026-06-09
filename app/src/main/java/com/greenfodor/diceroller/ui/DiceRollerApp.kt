package com.greenfodor.diceroller.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.greenfodor.diceroller.data.DataStoreSettingsRepository
import com.greenfodor.diceroller.sensors.supportsHaptics
import com.greenfodor.diceroller.sensors.supportsShakeDetection
import com.greenfodor.diceroller.ui.components.DiceRollerTopBar
import com.greenfodor.diceroller.ui.screens.AppDestination
import com.greenfodor.diceroller.ui.screens.D100Screen
import com.greenfodor.diceroller.ui.screens.D10Screen
import com.greenfodor.diceroller.ui.screens.D20Screen
import com.greenfodor.diceroller.ui.screens.D4Screen
import com.greenfodor.diceroller.ui.screens.D6Screen
import com.greenfodor.diceroller.ui.screens.D8Screen
import com.greenfodor.diceroller.ui.screens.DiceType
import com.greenfodor.diceroller.ui.screens.DoubleD6Screen
import com.greenfodor.diceroller.ui.settings.SettingsScreen
import com.greenfodor.diceroller.ui.settings.SettingsUiState
import com.greenfodor.diceroller.ui.settings.SettingsViewModel
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.resolveDarkTheme
import com.greenfodor.diceroller.ui.utils.LocalD6FaceStyle
import com.greenfodor.diceroller.ui.utils.LocalHapticsEnabled
import com.greenfodor.diceroller.ui.utils.LocalShakeToRollEnabled

/**
 * Root composable. Loads the persisted [com.greenfodor.diceroller.data.ThemeMode] via
 * [SettingsViewModel], resolves the dark/light theme, and switches between the dice home
 * and the settings screen. While the theme mode is still loading (`null`) it renders nothing
 * and leaves the splash screen up (via [onReady]) to avoid a theme flash.
 */
@Composable
fun DiceRollerApp(onReady: () -> Unit = {}) {
    val context = LocalContext.current
    val repository = remember(context) { DataStoreSettingsRepository.create(context.applicationContext) }
    val settingsViewModel: SettingsViewModel =
        viewModel(factory = SettingsViewModel.provideFactory(repository))
    val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle()
    val hapticFeedbackEnabled by settingsViewModel.hapticFeedbackEnabled.collectAsStateWithLifecycle()
    val hapticFeedbackSupported = remember(context) { context.supportsHaptics() }
    val shakeToRollEnabled by settingsViewModel.shakeToRollEnabled.collectAsStateWithLifecycle()
    val shakeToRollSupported = remember(context) { context.supportsShakeDetection() }
    val d6FaceStyle by settingsViewModel.d6FaceStyle.collectAsStateWithLifecycle()

    val mode = themeMode ?: return

    LaunchedEffect(Unit) { onReady() }

    var destination by rememberSaveable { mutableStateOf(AppDestination.DICE) }
    var selectedDiceType by rememberSaveable { mutableStateOf(DiceType.SINGLE_D6) }

    DiceRollerTheme(darkTheme = resolveDarkTheme(mode)) {
        when (destination) {
            AppDestination.DICE ->
                CompositionLocalProvider(
                    LocalHapticsEnabled provides (hapticFeedbackSupported && hapticFeedbackEnabled),
                    LocalShakeToRollEnabled provides (shakeToRollSupported && shakeToRollEnabled),
                    LocalD6FaceStyle provides d6FaceStyle
                ) {
                    DiceHome(
                        selectedDiceType = selectedDiceType,
                        onDiceTypeSelected = { selectedDiceType = it },
                        onOpenSettings = { destination = AppDestination.SETTINGS }
                    )
                }

            AppDestination.SETTINGS -> {
                SettingsScreen(
                    state = SettingsUiState(
                        themeMode = mode,
                        hapticFeedbackEnabled = hapticFeedbackEnabled,
                        hapticFeedbackSupported = hapticFeedbackSupported,
                        shakeToRollEnabled = shakeToRollEnabled,
                        shakeToRollSupported = shakeToRollSupported,
                        d6FaceStyle = d6FaceStyle
                    ),
                    onThemeModeSelected = settingsViewModel::setThemeMode,
                    onHapticFeedbackToggled = settingsViewModel::setHapticFeedbackEnabled,
                    onShakeToRollToggled = settingsViewModel::setShakeToRollEnabled,
                    onD6FaceStyleSelected = settingsViewModel::setD6FaceStyle,
                    onBack = { destination = AppDestination.DICE }
                )
                BackHandler { destination = AppDestination.DICE }
            }
        }
    }
}

@Composable
private fun DiceHome(
    selectedDiceType: DiceType,
    onDiceTypeSelected: (DiceType) -> Unit,
    onOpenSettings: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DiceRollerTopBar(
                selectedDiceType = selectedDiceType,
                onDiceTypeSelected = onDiceTypeSelected,
                onOpenSettings = onOpenSettings
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedDiceType) {
                DiceType.SINGLE_D4 -> D4Screen()
                DiceType.SINGLE_D6 -> D6Screen()
                DiceType.DOUBLE_D6 -> DoubleD6Screen()
                DiceType.SINGLE_D8 -> D8Screen()
                DiceType.SINGLE_D10 -> D10Screen()
                DiceType.SINGLE_D20 -> D20Screen()
                DiceType.PERCENTILE_D100 -> D100Screen()
            }
        }
    }
}
