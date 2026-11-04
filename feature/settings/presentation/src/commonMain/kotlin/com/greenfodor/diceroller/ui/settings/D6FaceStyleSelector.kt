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
import com.greenfodor.diceroller.data.D6FaceStyle
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.Res
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.d6_face_style_numbers
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.d6_face_style_pips
import com.greenfodor.diceroller.ui.icons.Casino
import com.greenfodor.diceroller.ui.icons.DiceIcons
import com.greenfodor.diceroller.ui.icons.Numbers
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private data class D6FaceStyleOption(
    val style: D6FaceStyle,
    val label: StringResource,
    val icon: ImageVector
)

private val d6FaceStyleOptions = listOf(
    D6FaceStyleOption(D6FaceStyle.PIPS, Res.string.d6_face_style_pips, DiceIcons.Casino),
    D6FaceStyleOption(D6FaceStyle.NUMBERS, Res.string.d6_face_style_numbers, DiceIcons.Numbers)
)

/**
 * Connected single-select control for choosing how the D6's faces are marked. Rendered under the
 * app's Material 3 Expressive theme; selecting a segment immediately invokes [onSelected].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun D6FaceStyleSelector(
    selected: D6FaceStyle,
    onSelected: (D6FaceStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        d6FaceStyleOptions.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option.style == selected,
                onClick = { onSelected(option.style) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = d6FaceStyleOptions.size),
                icon = { Icon(imageVector = option.icon, contentDescription = null) },
                label = { Text(text = stringResource(option.label)) }
            )
        }
    }
}

@LightDarkPreview
@Composable
private fun D6FaceStyleSelectorPreview() {
    DiceRollerTheme {
        Surface {
            D6FaceStyleSelector(selected = D6FaceStyle.PIPS, onSelected = {})
        }
    }
}
