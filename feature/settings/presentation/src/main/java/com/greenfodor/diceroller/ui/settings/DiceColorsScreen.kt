package com.greenfodor.diceroller.ui.settings

import androidx.annotation.StringRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.util.lerp
import com.greenfodor.diceroller.data.DiceColorOption
import com.greenfodor.diceroller.data.DiceColorSettings
import com.greenfodor.diceroller.data.DieColorTarget
import com.greenfodor.diceroller.data.ThemeMode
import com.greenfodor.diceroller.feature.settings.presentation.R
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.resolveDarkTheme
import com.greenfodor.diceroller.ui.theme.spacing

/**
 * Sub-screen for choosing dice colors. A switch toggles "single color for all dice": when on,
 * one [DiceColorPicker] recolors every die; when off, each [DieColorTarget] gets its own picker.
 * Swatches are resolved to the current light/dark variant via the active [themeMode].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceColorsScreen(
    settings: DiceColorSettings,
    themeMode: ThemeMode,
    onUseSingleColorToggled: (Boolean) -> Unit,
    onSingleColorSelected: (DiceColorOption) -> Unit,
    onDiceColorSelected: (DieColorTarget, DiceColorOption) -> Unit,
    onRestoreDefaults: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = resolveDarkTheme(themeMode)
    var showRestoreDialog by remember { mutableStateOf(false) }

    // Stable per-target callbacks: recreating these lambdas on every recomposition would break
    // strong-skipping and force all ~80 per-die swatches to recompose when the mode is toggled.
    val perDieColorSelected = remember(onDiceColorSelected) {
        DieColorTarget.entries.associateWith { target ->
            { option: DiceColorOption -> onDiceColorSelected(target, option) }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.dice_colors_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = MaterialTheme.spacing.medium)
                .verticalScroll(rememberScrollState())
        ) {
            SettingSwitchRow(
                label = stringResource(R.string.dice_colors_single_label),
                enabled = settings.useSingleColor,
                supported = true,
                unsupportedMessageResId = R.string.settings_unsupported,
                onToggle = onUseSingleColorToggled
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            // Both modes are composed once and cross-faded while the height springs between them
            // (the expand/collapse feel). The single/per-die swatches are heavy, so the animation
            // is driven entirely by deferred state reads inside ExpandingCrossfade — no per-frame
            // recomposition or re-measure, just re-layout + a GPU alpha blit.
            ExpandingCrossfade(
                showFirst = settings.useSingleColor,
                first = {
                    ColorSection(
                        titleResId = R.string.dice_colors_all_dice_title,
                        selected = settings.singleColor,
                        isDark = isDark,
                        onSelected = onSingleColorSelected
                    )
                },
                second = {
                    Column {
                        DieColorTarget.entries.forEachIndexed { index, target ->
                            if (index > 0) {
                                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                            }
                            ColorSection(
                                titleResId = target.labelResId,
                                selected = settings.optionFor(target),
                                isDark = isDark,
                                onSelected = perDieColorSelected.getValue(target)
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            RestoreDefaultsButton(onClick = { showRestoreDialog = true })

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
        }
    }

    if (showRestoreDialog) {
        RestoreDefaultsDialog(
            onConfirm = {
                showRestoreDialog = false
                onRestoreDefaults()
            },
            onDismiss = { showRestoreDialog = false }
        )
    }
}

/**
 * Cross-fades between [first] and [second] while springing the height between their two intrinsic
 * sizes — the expand/collapse animation — without paying that cost every frame.
 *
 * Both slots are composed once (kept resident) and measured with stable constraints, so Compose's
 * measure cache returns them without re-running layout on the heavy swatch content. A single
 * [progress] spring (1 = [first], 0 = [second]) is read only in the draw-phase `graphicsLayer`
 * (alpha) and the layout-phase measure/placement lambdas, so changing it each frame invalidates
 * only layout + draw — never composition or measurement. The settled-away slot is left unplaced so
 * it is neither drawn nor interactive.
 */
@Composable
private fun ExpandingCrossfade(
    showFirst: Boolean,
    modifier: Modifier = Modifier,
    first: @Composable () -> Unit,
    second: @Composable () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = if (showFirst) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "expandProgress"
    )
    Layout(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds(),
        content = {
            // Only the target slot carries semantics; the other is faded out and on its way out,
            // so clearing its semantics keeps it (and its swatches) out of the accessibility tree
            // and prevents duplicate nodes while both remain composed.
            Box(
                modifier = Modifier
                    .graphicsLayer { alpha = progress }
                    .then(if (showFirst) Modifier else Modifier.clearAndSetSemantics {})
            ) { first() }
            Box(
                modifier = Modifier
                    .graphicsLayer { alpha = 1f - progress }
                    .then(if (showFirst) Modifier.clearAndSetSemantics {} else Modifier)
            ) { second() }
        }
    ) { measurables, constraints ->
        val firstPlaceable = measurables[0].measure(constraints)
        val secondPlaceable = measurables[1].measure(constraints)
        val width = maxOf(firstPlaceable.width, secondPlaceable.width)
        // Clamp the height fraction to [0, 1]: the spring is bouncy and overshoots past its
        // target, and an unclamped lerp would extrapolate a height *smaller* than the settled
        // content, which clipToBounds() then clips (the restore button rides up over the picker).
        val height = lerp(secondPlaceable.height, firstPlaceable.height, progress.coerceIn(0f, 1f))
        layout(width, height) {
            // Place only slots that are at least partially visible; the fully faded-out one is
            // omitted so it can't be drawn over or receive touches once the animation settles.
            if (progress > 0f) {
                firstPlaceable.place(0, 0)
            }
            if (progress < 1f) {
                secondPlaceable.place(0, 0)
            }
        }
    }
}

@Composable
private fun RestoreDefaultsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = stringResource(R.string.dice_colors_restore_defaults))
    }
}

@Composable
private fun RestoreDefaultsDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.dice_colors_restore_dialog_title)) },
        text = { Text(text = stringResource(R.string.dice_colors_restore_dialog_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.dice_colors_restore_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.dice_colors_restore_dialog_cancel))
            }
        }
    )
}

@Composable
private fun ColorSection(
    @StringRes titleResId: Int,
    selected: DiceColorOption,
    isDark: Boolean,
    onSelected: (DiceColorOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(titleResId),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
        DiceColorPicker(
            selected = selected,
            isDark = isDark,
            onSelected = onSelected
        )
    }
}

@get:StringRes
private val DieColorTarget.labelResId: Int
    get() = when (this) {
        DieColorTarget.D4 -> R.string.dice_colors_d4_title
        DieColorTarget.D6 -> R.string.dice_colors_d6_title
        DieColorTarget.D6_SECONDARY -> R.string.dice_colors_d6_secondary_title
        DieColorTarget.D8 -> R.string.dice_colors_d8_title
        DieColorTarget.D10 -> R.string.dice_colors_d10_title
        DieColorTarget.D20 -> R.string.dice_colors_d20_title
        DieColorTarget.D100 -> R.string.dice_colors_d100_title
        DieColorTarget.D100_SECONDARY -> R.string.dice_colors_d100_secondary_title
    }

@LightDarkPreview
@Composable
private fun DiceColorsScreenPreview() {
    DiceRollerTheme {
        DiceColorsScreen(
            settings = DiceColorSettings(),
            themeMode = ThemeMode.FOLLOW_SYSTEM,
            onUseSingleColorToggled = {},
            onSingleColorSelected = {},
            onDiceColorSelected = { _, _ -> },
            onRestoreDefaults = {},
            onBack = {}
        )
    }
}
