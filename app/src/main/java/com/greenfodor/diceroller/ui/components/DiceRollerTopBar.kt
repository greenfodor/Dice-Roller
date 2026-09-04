package com.greenfodor.diceroller.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.greenfodor.diceroller.R
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

/**
 * Custom TopAppBar for the Dice Roller app.
 *
 * It features two action buttons: one that opens the roll history and one that opens the
 * settings screen (where the theme is configured).
 *
 * @param onOpenHistory Callback to open the roll history.
 * @param onOpenSettings Callback to open the settings screen.
 * @param modifier Modifier for the top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollerTopBar(
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

@LightDarkPreview
@Composable
private fun DiceRollerTopBarPreview() {
    DiceRollerTheme {
        DiceRollerTopBar(
            onOpenHistory = {},
            onOpenSettings = {}
        )
    }
}
