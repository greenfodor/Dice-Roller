package com.greenfodor.diceroller.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.greenfodor.diceroller.data.D6FaceStyle
import com.greenfodor.diceroller.data.RollOutcome
import com.greenfodor.diceroller.sensors.supportsHaptics
import com.greenfodor.diceroller.sensors.supportsShakeDetection
import com.greenfodor.diceroller.ui.components.DiceRollerTopBar
import com.greenfodor.diceroller.ui.components.DiceTypeFab
import com.greenfodor.diceroller.ui.components.DiceTypePickerSheet
import com.greenfodor.diceroller.ui.history.RollHistoryRoute
import com.greenfodor.diceroller.ui.history.rollHistoryEntry
import com.greenfodor.diceroller.ui.screens.D100Screen
import com.greenfodor.diceroller.ui.screens.D10Screen
import com.greenfodor.diceroller.ui.screens.D20Screen
import com.greenfodor.diceroller.ui.screens.D4Screen
import com.greenfodor.diceroller.ui.screens.D6Screen
import com.greenfodor.diceroller.ui.screens.D8Screen
import com.greenfodor.diceroller.ui.screens.DiceRoute
import com.greenfodor.diceroller.ui.screens.DiceType
import com.greenfodor.diceroller.ui.screens.DiceViewModel
import com.greenfodor.diceroller.ui.screens.DoubleD6Screen
import com.greenfodor.diceroller.ui.settings.DiceColorsRoute
import com.greenfodor.diceroller.ui.settings.SettingsRoute
import com.greenfodor.diceroller.ui.settings.SettingsViewModel
import com.greenfodor.diceroller.ui.settings.settingsEntries
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.resolveDarkTheme
import com.greenfodor.diceroller.ui.utils.LocalD6FaceStyle
import com.greenfodor.diceroller.ui.utils.LocalHapticsEnabled
import com.greenfodor.diceroller.ui.utils.LocalShakeToRollEnabled

/**
 * Root composable. Owns the back stack and the app-wide configuration.
 *
 * The [SettingsViewModel] resolved here is activity-scoped: [DiceRollerTheme] wraps every entry,
 * so the theme, dice colors and roll behaviour have to be readable before any entry is composed.
 * The settings entries render from that same instance and its already-loaded values, while the
 * dice and roll history entries resolve their own ViewModels, scoped to their entry by
 * [rememberViewModelStoreNavEntryDecorator]. While the theme mode is still loading (`null`)
 * nothing renders and the splash screen stays up (via [onReady]) to avoid a theme flash.
 */
@Composable
fun DiceRollerApp(
    onReady: () -> Unit = {},
    appSettingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val themeMode by appSettingsViewModel.themeMode.collectAsStateWithLifecycle()
    val hapticFeedbackEnabled by appSettingsViewModel.hapticFeedbackEnabled.collectAsStateWithLifecycle()
    val hapticFeedbackSupported = remember(context) { context.supportsHaptics() }
    val shakeToRollEnabled by appSettingsViewModel.shakeToRollEnabled.collectAsStateWithLifecycle()
    val shakeToRollSupported = remember(context) { context.supportsShakeDetection() }
    val d6FaceStyle by appSettingsViewModel.d6FaceStyle.collectAsStateWithLifecycle()
    val diceColorSettings by appSettingsViewModel.diceColorSettings.collectAsStateWithLifecycle()

    val mode = themeMode ?: return

    LaunchedEffect(Unit) { onReady() }

    val backStack = rememberNavBackStack(DiceRoute)

    DiceRollerTheme(darkTheme = resolveDarkTheme(mode), diceColorSettings = diceColorSettings) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            sceneStrategies = listOf(DialogSceneStrategy()),
            entryProvider = entryProvider {
                diceEntry(
                    hapticsEnabled = hapticFeedbackSupported && hapticFeedbackEnabled,
                    shakeToRollEnabled = shakeToRollSupported && shakeToRollEnabled,
                    d6FaceStyle = d6FaceStyle,
                    onOpenHistory = { backStack.add(RollHistoryRoute) },
                    onOpenSettings = { backStack.add(SettingsRoute) }
                )
                settingsEntries(
                    viewModel = appSettingsViewModel,
                    hapticFeedbackSupported = hapticFeedbackSupported,
                    shakeToRollSupported = shakeToRollSupported,
                    onOpenDiceColors = { backStack.add(DiceColorsRoute) },
                    onBack = { backStack.removeLastOrNull() }
                )
                rollHistoryEntry(onDismiss = { backStack.removeLastOrNull() })
            }
        )
    }
}

/**
 * Adds the dice entry. Its [DiceViewModel] is scoped to this entry and records every roll the
 * screens report once the dice settle.
 */
private fun EntryProviderScope<NavKey>.diceEntry(
    hapticsEnabled: Boolean,
    shakeToRollEnabled: Boolean,
    d6FaceStyle: D6FaceStyle,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    entry<DiceRoute> {
        val diceViewModel: DiceViewModel = hiltViewModel()
        var selectedDiceType by rememberSaveable { mutableStateOf(DiceType.SINGLE_D6) }

        CompositionLocalProvider(
            LocalHapticsEnabled provides hapticsEnabled,
            LocalShakeToRollEnabled provides shakeToRollEnabled,
            LocalD6FaceStyle provides d6FaceStyle
        ) {
            DiceHome(
                selectedDiceType = selectedDiceType,
                onDiceTypeSelected = { selectedDiceType = it },
                onRollSettled = diceViewModel::onRollSettled,
                onOpenHistory = onOpenHistory,
                onOpenSettings = onOpenSettings
            )
        }
    }
}

@Composable
private fun DiceHome(
    selectedDiceType: DiceType,
    onDiceTypeSelected: (DiceType) -> Unit,
    onRollSettled: (RollOutcome) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var isDiceTypePickerVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DiceRollerTopBar(
                onOpenHistory = onOpenHistory,
                onOpenSettings = onOpenSettings
            )
        },
        floatingActionButton = {
            DiceTypeFab(
                selectedDiceType = selectedDiceType,
                onClick = { isDiceTypePickerVisible = true }
            )
        },
        floatingActionButtonPosition = FabPosition.Start
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedDiceType) {
                DiceType.SINGLE_D4 -> D4Screen(onRollSettled = onRollSettled)
                DiceType.SINGLE_D6 -> D6Screen(onRollSettled = onRollSettled)
                DiceType.DOUBLE_D6 -> DoubleD6Screen(onRollSettled = onRollSettled)
                DiceType.SINGLE_D8 -> D8Screen(onRollSettled = onRollSettled)
                DiceType.SINGLE_D10 -> D10Screen(onRollSettled = onRollSettled)
                DiceType.SINGLE_D20 -> D20Screen(onRollSettled = onRollSettled)
                DiceType.PERCENTILE_D100 -> D100Screen(onRollSettled = onRollSettled)
            }
        }

        if (isDiceTypePickerVisible) {
            DiceTypePickerSheet(
                selectedDiceType = selectedDiceType,
                onDiceTypeSelected = onDiceTypeSelected,
                onDismissRequest = { isDiceTypePickerVisible = false }
            )
        }
    }
}
