package com.greenfodor.diceroller.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
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
import com.greenfodor.diceroller.data.ThemeMode
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.Res
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.theme_dark
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.theme_follow_system
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.theme_light
import com.greenfodor.diceroller.ui.icons.BrightnessAuto
import com.greenfodor.diceroller.ui.icons.DarkMode
import com.greenfodor.diceroller.ui.icons.DiceIcons
import com.greenfodor.diceroller.ui.icons.LightMode
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private data class ThemeModeOption(
    val mode: ThemeMode,
    val label: StringResource,
    val icon: ImageVector
)

private val themeModeOptions = listOf(
    ThemeModeOption(ThemeMode.FOLLOW_SYSTEM, Res.string.theme_follow_system, DiceIcons.BrightnessAuto),
    ThemeModeOption(ThemeMode.LIGHT, Res.string.theme_light, DiceIcons.LightMode),
    ThemeModeOption(ThemeMode.DARK, Res.string.theme_dark, DiceIcons.DarkMode)
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
                        text = stringResource(option.label),
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
