package com.greenfodor.diceroller.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.screens.DiceType
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

/**
 * Custom TopAppBar for the Dice Roller app.
 *
 * It features a dropdown menu for selecting the type of die to roll and a toggle button
 * for switching between light and dark themes with a rotation animation.
 *
 * @param selectedDiceType The currently active die type.
 * @param onDiceTypeSelected Callback when the user selects a different die type.
 * @param isDarkMode Whether the app is currently in dark mode.
 * @param onToggleTheme Callback to toggle between light and dark themes.
 * @param modifier Modifier for the top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollerTopBar(
    selectedDiceType: DiceType,
    onDiceTypeSelected: (DiceType) -> Unit,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isDarkMode) 180f else 0f,
        animationSpec = tween(durationMillis = DiceConstants.ICON_ROTATION_DURATION_MILLIS),
        label = "iconRotation",
    )

    TopAppBar(
        modifier = modifier.background(color = MaterialTheme.colorScheme.background),
        title = { },
        navigationIcon = {
            DiceTypeDropDown(
                selectedDiceType = selectedDiceType,
                onDiceTypeSelected = onDiceTypeSelected,
            )
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                actionIconContentColor = MaterialTheme.colorScheme.onBackground,
            ),
        actions = {
            IconButton(onClick = onToggleTheme) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    modifier = Modifier.rotate(rotation),
                )
            }
        },
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
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
        ) {
            DiceType.entries.forEach { dice ->
                DropdownMenuItem(
                    text = { Text(stringResource(dice.labelResId)) },
                    onClick = {
                        onDiceTypeSelected(dice)
                        isMenuExpanded = false
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun DiceRollerTopBarPreview() {
    DiceRollerTheme {
        DiceRollerTopBar(
            selectedDiceType = DiceType.SINGLE_D6,
            onDiceTypeSelected = {},
            isDarkMode = isSystemInDarkTheme(),
            onToggleTheme = {},
        )
    }
}
