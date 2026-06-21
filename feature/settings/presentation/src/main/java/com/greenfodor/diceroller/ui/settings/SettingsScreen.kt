package com.greenfodor.diceroller.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.greenfodor.diceroller.data.D6FaceStyle
import com.greenfodor.diceroller.data.ThemeMode
import com.greenfodor.diceroller.feature.settings.presentation.R
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.spacing

/**
 * Settings screen. Currently, exposes the theme selector; structured so future settings
 * are added as additional sections in the column.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onHapticFeedbackToggled: (Boolean) -> Unit,
    onShakeToRollToggled: (Boolean) -> Unit,
    onD6FaceStyleSelected: (D6FaceStyle) -> Unit,
    onOpenDiceColors: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
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
        ) {
            Text(
                text = stringResource(R.string.settings_theme_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            ThemeModeSelector(
                selected = state.themeMode,
                onSelected = onThemeModeSelected
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            Text(
                text = stringResource(R.string.settings_haptics_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            SettingSwitchRow(
                label = stringResource(R.string.settings_haptics_label),
                enabled = state.hapticFeedbackEnabled,
                supported = state.hapticFeedbackSupported,
                unsupportedMessageResId = R.string.settings_unsupported,
                onToggle = onHapticFeedbackToggled
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            Text(
                text = stringResource(R.string.settings_shake_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            SettingSwitchRow(
                label = stringResource(R.string.settings_shake_label),
                enabled = state.shakeToRollEnabled,
                supported = state.shakeToRollSupported,
                unsupportedMessageResId = R.string.settings_unsupported,
                onToggle = onShakeToRollToggled
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            Text(
                text = stringResource(R.string.settings_d6_face_style_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            D6FaceStyleSelector(
                selected = state.d6FaceStyle,
                onSelected = onD6FaceStyleSelected
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            SettingsNavigationRow(
                title = stringResource(R.string.settings_dice_colors_title),
                subtitle = stringResource(R.string.settings_dice_colors_label),
                onClick = onOpenDiceColors
            )
        }
    }
}

/** A clickable settings row that navigates to a sub-screen, with a trailing chevron. */
@Composable
private fun SettingsNavigationRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@LightDarkPreview
@Composable
private fun SettingsScreenPreview() {
    DiceRollerTheme {
        SettingsScreen(
            state = SettingsUiState(
                themeMode = ThemeMode.FOLLOW_SYSTEM,
                hapticFeedbackEnabled = true,
                hapticFeedbackSupported = true,
                shakeToRollEnabled = true,
                shakeToRollSupported = false,
                d6FaceStyle = D6FaceStyle.PIPS
            ),
            onThemeModeSelected = {},
            onHapticFeedbackToggled = {},
            onShakeToRollToggled = {},
            onD6FaceStyleSelected = {},
            onOpenDiceColors = {},
            onBack = {}
        )
    }
}
