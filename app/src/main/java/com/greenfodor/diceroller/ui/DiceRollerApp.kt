package com.greenfodor.diceroller.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
import com.greenfodor.diceroller.ui.components.DiceTypeRail
import com.greenfodor.diceroller.ui.components.shouldShowDiceRail
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
 *
 * @param windowSizeClass Size class of the window the app is drawn in, used to decide whether the
 * dice screen selects its die type through a permanent rail or a floating action button.
 * @param onReady Callback once the persisted theme has loaded and the first frame can be drawn.
 * @param appSettingsViewModel Activity-scoped source of the app-wide settings.
 */
@Composable
fun DiceRollerApp(
    windowSizeClass: WindowSizeClass,
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
                    windowSizeClass = windowSizeClass,
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
    windowSizeClass: WindowSizeClass,
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
                windowSizeClass = windowSizeClass,
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
    windowSizeClass: WindowSizeClass,
    selectedDiceType: DiceType,
    onDiceTypeSelected: (DiceType) -> Unit,
    onRollSettled: (RollOutcome) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val orientation = LocalConfiguration.current.orientation
    val showRail = shouldShowDiceRail(windowSizeClass.widthSizeClass, orientation)

    if (showRail) {
        Row(modifier = Modifier.fillMaxSize()) {
            DiceTypeRail(
                selectedDiceType = selectedDiceType,
                onDiceTypeSelected = onDiceTypeSelected
            )
            DiceContent(
                selectedDiceType = selectedDiceType,
                onDiceTypeSelected = onDiceTypeSelected,
                onRollSettled = onRollSettled,
                onOpenHistory = onOpenHistory,
                onOpenSettings = onOpenSettings,
                showDiceTypeFab = false,
                modifier = Modifier
                    .weight(1f)
                    .consumeWindowInsets(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Start)
                    )
            )
        }
    } else {
        DiceContent(
            selectedDiceType = selectedDiceType,
            onDiceTypeSelected = onDiceTypeSelected,
            onRollSettled = onRollSettled,
            onOpenHistory = onOpenHistory,
            onOpenSettings = onOpenSettings,
            showDiceTypeFab = true
        )
    }
}

/**
 * Scaffold holding the top bar and the screen for [selectedDiceType]. With [showDiceTypeFab] set
 * it also hosts the [DiceTypeFab] and the [DiceTypePickerSheet] it opens; otherwise the die type
 * is picked outside this scaffold and neither is composed.
 */
@Composable
private fun DiceContent(
    selectedDiceType: DiceType,
    onDiceTypeSelected: (DiceType) -> Unit,
    onRollSettled: (RollOutcome) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    showDiceTypeFab: Boolean,
    modifier: Modifier = Modifier
) {
    var isDiceTypePickerVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DiceRollerTopBar(
                onOpenHistory = onOpenHistory,
                onOpenSettings = onOpenSettings
            )
        },
        floatingActionButton = {
            if (showDiceTypeFab) {
                DiceTypeFab(
                    selectedDiceType = selectedDiceType,
                    onClick = { isDiceTypePickerVisible = true }
                )
            }
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

        if (showDiceTypeFab && isDiceTypePickerVisible) {
            DiceTypePickerSheet(
                selectedDiceType = selectedDiceType,
                onDiceTypeSelected = onDiceTypeSelected,
                onDismissRequest = { isDiceTypePickerVisible = false }
            )
        }
    }
}
