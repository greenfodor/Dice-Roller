package com.greenfodor.diceroller.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.greenfodor.diceroller.feature.settings.presentation.R
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

/**
 * A labeled [Switch] row for a boolean setting. When [supported] is `false` the device lacks
 * the required capability, so the switch is forced off, disabled (grayed out), and the
 * [unsupportedMessageResId] explanation is shown beneath it.
 */
@Composable
fun SettingSwitchRow(
    label: String,
    enabled: Boolean,
    supported: Boolean,
    unsupportedMessageResId: Int,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = supported && enabled,
                onCheckedChange = onToggle,
                enabled = supported
            )
        }
        if (supported.not()) {
            Text(
                text = stringResource(unsupportedMessageResId),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@LightDarkPreview
@Composable
private fun SettingSwitchRowPreview() {
    DiceRollerTheme {
        SettingSwitchRow(
            label = "Roll vibration",
            enabled = true,
            supported = true,
            unsupportedMessageResId = R.string.settings_unsupported,
            onToggle = {}
        )
    }
}

@Preview(name = "Unsupported")
@Composable
private fun SettingSwitchRowUnsupportedPreview() {
    DiceRollerTheme {
        SettingSwitchRow(
            label = "Shake to roll",
            enabled = false,
            supported = false,
            unsupportedMessageResId = R.string.settings_unsupported,
            onToggle = {}
        )
    }
}
