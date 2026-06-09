package com.greenfodor.diceroller.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Numbers
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
import com.greenfodor.diceroller.data.D6FaceStyle
import com.greenfodor.diceroller.feature.settings.presentation.R
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme

private data class D6FaceStyleOption(
    val style: D6FaceStyle,
    @field:StringRes val labelResId: Int,
    val icon: ImageVector
)

private val d6FaceStyleOptions = listOf(
    D6FaceStyleOption(D6FaceStyle.PIPS, R.string.d6_face_style_pips, Icons.Default.Casino),
    D6FaceStyleOption(D6FaceStyle.NUMBERS, R.string.d6_face_style_numbers, Icons.Default.Numbers)
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
                label = { Text(text = stringResource(option.labelResId)) }
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
