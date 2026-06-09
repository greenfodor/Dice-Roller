package com.greenfodor.diceroller.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.greenfodor.diceroller.data.ThemeMode
import com.greenfodor.diceroller.feature.settings.presentation.R
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

private data class ThemeModeOption(
    val mode: ThemeMode,
    @field:StringRes val labelResId: Int,
    val icon: ImageVector
)

private val themeModeOptions = listOf(
    ThemeModeOption(ThemeMode.FOLLOW_SYSTEM, R.string.theme_follow_system, Icons.Default.BrightnessAuto),
    ThemeModeOption(ThemeMode.LIGHT, R.string.theme_light, Icons.Default.LightMode),
    ThemeModeOption(ThemeMode.DARK, R.string.theme_dark, Icons.Default.DarkMode)
)

/**
 * Connected single-select control for choosing a [ThemeMode]. Rendered under the app's
 * Material 3 Expressive theme; selecting a segment immediately invokes [onSelected].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeModeSelector(
    selected: ThemeMode,
    onSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        themeModeOptions.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option.mode == selected,
                onClick = { onSelected(option.mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = themeModeOptions.size),
                icon = { Icon(imageVector = option.icon, contentDescription = null) },
                label = {
                    Text(
                        text = stringResource(option.labelResId),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            )
        }
    }
}

@LightDarkPreview
@Composable
private fun ThemeModeSelectorPreview() {
    DiceRollerTheme {
        Surface {
            ThemeModeSelector(selected = ThemeMode.FOLLOW_SYSTEM, onSelected = {})
        }
    }
}
