package com.greenfodor.diceroller.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
    themeMode: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    hapticFeedbackEnabled: Boolean,
    hapticFeedbackSupported: Boolean,
    onHapticFeedbackToggled: (Boolean) -> Unit,
    shakeToRollEnabled: Boolean,
    shakeToRollSupported: Boolean,
    onShakeToRollToggled: (Boolean) -> Unit,
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
                // Transparent container so the Scaffold's animated background shows through and
                // recolors in lockstep on theme change. An opaque container is double-animated by
                // TopAppBar's internal animateColorAsState, which lags behind the theme transition.
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
                selected = themeMode,
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
                enabled = hapticFeedbackEnabled,
                supported = hapticFeedbackSupported,
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
                enabled = shakeToRollEnabled,
                supported = shakeToRollSupported,
                unsupportedMessageResId = R.string.settings_unsupported,
                onToggle = onShakeToRollToggled
            )
        }
    }
}

@LightDarkPreview
@Composable
private fun SettingsScreenPreview() {
    DiceRollerTheme {
        SettingsScreen(
            themeMode = ThemeMode.FOLLOW_SYSTEM,
            onThemeModeSelected = {},
            hapticFeedbackEnabled = true,
            hapticFeedbackSupported = true,
            onHapticFeedbackToggled = {},
            shakeToRollEnabled = true,
            shakeToRollSupported = true,
            onShakeToRollToggled = {},
            onBack = {}
        )
    }
}
