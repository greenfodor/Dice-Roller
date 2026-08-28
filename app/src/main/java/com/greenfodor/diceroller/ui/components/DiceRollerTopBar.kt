package com.greenfodor.diceroller.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.screens.DiceType
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

/**
 * Custom TopAppBar for the Dice Roller app.
 *
 * It features a dropdown menu for selecting the type of die to roll and two action buttons:
 * one that opens the roll history and one that opens the settings screen (where the theme is
 * configured).
 *
 * @param selectedDiceType The currently active die type.
 * @param onDiceTypeSelected Callback when the user selects a different die type.
 * @param onOpenHistory Callback to open the roll history.
 * @param onOpenSettings Callback to open the settings screen.
 * @param modifier Modifier for the top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollerTopBar(
    selectedDiceType: DiceType,
    onDiceTypeSelected: (DiceType) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        // No own background: the app bar's container is transparent so the Scaffold's
        // (identically animated) background shows through and recolors in lockstep with the
        // rest of the screen on theme change, instead of lagging as a separate draw layer.
        modifier = modifier,
        title = { },
        navigationIcon = {
            DiceTypeDropDown(
                selectedDiceType = selectedDiceType,
                onDiceTypeSelected = onDiceTypeSelected
            )
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                actionIconContentColor = MaterialTheme.colorScheme.onBackground
            ),
        actions = {
            IconButton(onClick = onOpenHistory) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = stringResource(R.string.cd_open_roll_history)
                )
            }
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.cd_open_settings)
                )
            }
        }
    )
}

@Composable
private fun DiceTypeDropDown(
    selectedDiceType: DiceType,
    modifier: Modifier = Modifier,
    onDiceTypeSelected: (DiceType) -> Unit = {}
) {
    var isMenuExpanded by remember { mutableStateOf(value = false) }

    Box(modifier = modifier) {
        TextButton(onClick = { isMenuExpanded = true }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(selectedDiceType.labelResId),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false }
        ) {
            DiceType.entries.forEach { dice ->
                DropdownMenuItem(
                    text = { Text(stringResource(dice.labelResId)) },
                    onClick = {
                        onDiceTypeSelected(dice)
                        isMenuExpanded = false
                    }
                )
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun DiceRollerTopBarPreview() {
    DiceRollerTheme {
        DiceRollerTopBar(
            selectedDiceType = DiceType.SINGLE_D6,
            onDiceTypeSelected = {},
            onOpenHistory = {},
            onOpenSettings = {}
        )
    }
}
