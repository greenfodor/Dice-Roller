package com.greenfodor.diceroller.ui.settings

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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

            AnimatedVisibility(
                visible = settings.useSingleColor,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    ColorSection(
                        titleResId = R.string.dice_colors_all_dice_title,
                        selected = settings.singleColor,
                        isDark = isDark,
                        onSelected = onSingleColorSelected
                    )
                }
            }

            AnimatedVisibility(
                visible = !settings.useSingleColor,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    DieColorTarget.entries.forEach { target ->
                        ColorSection(
                            titleResId = target.labelResId,
                            selected = settings.optionFor(target),
                            isDark = isDark,
                            onSelected = { onDiceColorSelected(target, it) }
                        )
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                    }
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

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

@Composable
private fun RestoreDefaultsButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
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
    onSelected: (DiceColorOption) -> Unit
) {
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
