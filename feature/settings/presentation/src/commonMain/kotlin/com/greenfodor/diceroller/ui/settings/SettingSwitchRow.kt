package com.greenfodor.diceroller.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.Res
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.settings_unsupported
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * A labeled [Switch] row for a boolean setting. When [supported] is `false` the device lacks
 * the required capability, so the switch is forced off, disabled (grayed out), and the
 * [unsupportedMessage] explanation is shown beneath it.
 */
@Composable
fun SettingSwitchRow(
    label: String,
    enabled: Boolean,
    supported: Boolean,
    unsupportedMessage: StringResource,
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
                text = stringResource(unsupportedMessage),
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
        Surface {
            SettingSwitchRow(
                label = "Roll vibration",
                enabled = true,
                supported = true,
                unsupportedMessage = Res.string.settings_unsupported,
                onToggle = {}
            )
        }
    }
}

@LightDarkPreview
@Composable
private fun SettingSwitchRowUnsupportedPreview() {
    DiceRollerTheme {
        Surface {
            SettingSwitchRow(
                label = "Shake to roll",
                enabled = false,
                supported = false,
                unsupportedMessage = Res.string.settings_unsupported,
                onToggle = {}
            )
        }
    }
}
