package com.greenfodor.diceroller.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.greenfodor.diceroller.ui.components.DiceTypePickerSheet
import com.greenfodor.diceroller.ui.components.DiceTypeRail
import com.greenfodor.diceroller.ui.components.shouldShowDiceRail
import com.greenfodor.diceroller.ui.history.RecentRollUiModel
import com.greenfodor.diceroller.ui.history.RecentRollsViewModel
import com.greenfodor.diceroller.ui.history.RecentRollsWheel
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
import com.greenfodor.diceroller.ui.theme.diceSpecs
import com.greenfodor.diceroller.ui.theme.resolveDarkTheme
import com.greenfodor.diceroller.ui.theme.spacing
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
 * dice screen selects its die type through a permanent rail or a peeking bottom sheet.
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
                    settingsViewModel = appSettingsViewModel,
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
 *
 * The selected die type is collected from [settingsViewModel] inside the entry's own content, so
 * a pick made on the dice screen renders without the entry being rebuilt first.
 */
private fun EntryProviderScope<NavKey>.diceEntry(
    windowSizeClass: WindowSizeClass,
    hapticsEnabled: Boolean,
    shakeToRollEnabled: Boolean,
    d6FaceStyle: D6FaceStyle,
    settingsViewModel: SettingsViewModel,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    entry<DiceRoute> {
        val diceViewModel: DiceViewModel = hiltViewModel()
        val recentRollsViewModel: RecentRollsViewModel = hiltViewModel()
        val recentRolls by recentRollsViewModel.recentRolls.collectAsStateWithLifecycle()
        val selectedDiceTypeKey by settingsViewModel.selectedDiceType.collectAsStateWithLifecycle()

        CompositionLocalProvider(
            LocalHapticsEnabled provides hapticsEnabled,
            LocalShakeToRollEnabled provides shakeToRollEnabled,
            LocalD6FaceStyle provides d6FaceStyle
        ) {
            DiceHome(
                windowSizeClass = windowSizeClass,
                selectedDiceType = DiceType.fromName(selectedDiceTypeKey),
                recentRolls = recentRolls,
                onDiceTypeSelected = { settingsViewModel.setSelectedDiceType(it.name) },
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
    recentRolls: List<RecentRollUiModel>,
    onDiceTypeSelected: (DiceType) -> Unit,
    onRollSettled: (RollOutcome) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val orientation = LocalConfiguration.current.orientation
    val showRail = shouldShowDiceRail(windowSizeClass.widthSizeClass, orientation)
    val recentRollsPlacement = recentRollsPlacement(orientation)

    if (showRail) {
        Row(modifier = Modifier.fillMaxSize()) {
            DiceTypeRail(
                selectedDiceType = selectedDiceType,
                onDiceTypeSelected = onDiceTypeSelected
            )
            DiceContent(
                selectedDiceType = selectedDiceType,
                recentRolls = recentRolls,
                recentRollsPlacement = recentRollsPlacement,
                onRollSettled = onRollSettled,
                onOpenHistory = onOpenHistory,
                onOpenSettings = onOpenSettings,
                modifier = Modifier
                    .weight(1f)
                    .consumeWindowInsets(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Start)
                    )
            )
        }
    } else {
        DiceTypePickerSheet(
            selectedDiceType = selectedDiceType,
            onDiceTypeSelected = onDiceTypeSelected
        ) {
            DiceContent(
                selectedDiceType = selectedDiceType,
                recentRolls = recentRolls,
                recentRollsPlacement = recentRollsPlacement,
                onRollSettled = onRollSettled,
                onOpenHistory = onOpenHistory,
                onOpenSettings = onOpenSettings
            )
        }
    }
}

/** Where the recent rolls wheel is anchored over the dice screen, and how far off that edge. */
private data class RecentRollsPlacement(
    val alignment: Alignment,
    val padding: PaddingValues
)

/**
 * Anchors the recent rolls wheel to the end edge in landscape and to the top center in portrait,
 * where it sits directly under the top bar.
 *
 * @param orientation Orientation of the current configuration, one of the
 * `Configuration.ORIENTATION_*` values.
 */
@Composable
private fun recentRollsPlacement(orientation: Int): RecentRollsPlacement =
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        RecentRollsPlacement(
            alignment = Alignment.CenterEnd,
            padding = PaddingValues(end = MaterialTheme.spacing.medium)
        )
    } else {
        RecentRollsPlacement(
            alignment = Alignment.TopCenter,
            padding = PaddingValues(horizontal = MaterialTheme.spacing.medium)
        )
    }

/**
 * Scaffold holding the top bar and the screen for [selectedDiceType], with the [RecentRollsWheel]
 * anchored over it at [recentRollsPlacement]. The die type is picked outside this scaffold, by the
 * [DiceTypeRail] or the [DiceTypePickerSheet] it is hosted in.
 *
 * The content is inset at the bottom by the height of the top bar as well, so it is centered on
 * the window rather than on the space left under the bar.
 */
@Composable
private fun DiceContent(
    selectedDiceType: DiceType,
    recentRolls: List<RecentRollUiModel>,
    recentRollsPlacement: RecentRollsPlacement,
    onRollSettled: (RollOutcome) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val diceSpecs = MaterialTheme.diceSpecs

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DiceRollerTopBar(
                onOpenHistory = onOpenHistory,
                onOpenSettings = onOpenSettings
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = innerPadding.calculateTopPadding())
        ) {
            AnimatedContent(
                targetState = selectedDiceType,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = fadeIn(
                            animationSpec = tween(
                                durationMillis = diceSpecs.dieSwitchFadeMillis,
                                easing = LinearOutSlowInEasing
                            )
                        ),
                        initialContentExit = fadeOut(
                            animationSpec = snap(delayMillis = diceSpecs.dieSwitchFadeMillis)
                        ),
                        targetContentZIndex = 1f,
                        sizeTransform = SizeTransform(clip = false)
                    )
                },
                label = "DiceTypeSwitch"
            ) { diceType ->
                when (diceType) {
                    DiceType.SINGLE_D4 -> D4Screen(onRollSettled = onRollSettled)
                    DiceType.SINGLE_D6 -> D6Screen(onRollSettled = onRollSettled)
                    DiceType.DOUBLE_D6 -> DoubleD6Screen(onRollSettled = onRollSettled)
                    DiceType.SINGLE_D8 -> D8Screen(onRollSettled = onRollSettled)
                    DiceType.SINGLE_D10 -> D10Screen(onRollSettled = onRollSettled)
                    DiceType.SINGLE_D20 -> D20Screen(onRollSettled = onRollSettled)
                    DiceType.PERCENTILE_D100 -> D100Screen(onRollSettled = onRollSettled)
                }
            }

            RecentRollsWheel(
                rolls = recentRolls,
                modifier = Modifier
                    .align(recentRollsPlacement.alignment)
                    .padding(recentRollsPlacement.padding)
            )
        }
    }
}
