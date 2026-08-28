package com.greenfodor.diceroller.ui.settings

import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.data.DiceColorOption
import com.greenfodor.diceroller.feature.settings.presentation.R
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.spacing
import com.greenfodor.diceroller.ui.theme.toColor

/**
 * A row of palette swatches for the current theme. Each swatch shows a [DiceColorOption] resolved
 * to its [isDark] shade; the [selected] one gets a ring and a check mark. Selecting a swatch
 * immediately invokes [onSelected].
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiceColorPicker(
    selected: DiceColorOption,
    isDark: Boolean,
    onSelected: (DiceColorOption) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        DiceColorOption.entries.forEach { option ->
            val color = option.toColor(isDark)
            val isSelected = option == selected
            val name = stringResource(option.labelResId)
            val description = if (isSelected) {
                stringResource(R.string.cd_dice_color_swatch_selected, name)
            } else {
                name
            }

            val borderWidth = if (isSelected) SELECTED_BORDER_WIDTH else BORDER_WIDTH
            val borderColor = if (isSelected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }

            Box(
                modifier = Modifier
                    .size(SWATCH_SIZE)
                    .background(color = color, shape = CircleShape)
                    .border(width = borderWidth, color = borderColor, shape = CircleShape)
                    .clickable(onClickLabel = name) { onSelected(option) }
                    .semantics {
                        this.selected = isSelected
                        contentDescription = description
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    val checkScale = remember { Animatable(0f) }
                    LaunchedEffect(Unit) {
                        checkScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    }
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (color.luminance() > LUMINANCE_THRESHOLD) Color.Black else Color.White,
                        modifier = Modifier
                            .padding(CHECK_PADDING)
                            .graphicsLayer {
                                scaleX = checkScale.value
                                scaleY = checkScale.value
                                alpha = checkScale.value
                            }
                    )
                }
            }
        }
    }
}

@get:StringRes
private val DiceColorOption.labelResId: Int
    get() = when (this) {
        DiceColorOption.RED -> R.string.dice_color_red
        DiceColorOption.ORANGE -> R.string.dice_color_orange
        DiceColorOption.YELLOW -> R.string.dice_color_yellow
        DiceColorOption.GREEN -> R.string.dice_color_green
        DiceColorOption.TEAL -> R.string.dice_color_teal
        DiceColorOption.BLUE -> R.string.dice_color_blue
        DiceColorOption.PURPLE -> R.string.dice_color_purple
        DiceColorOption.PINK -> R.string.dice_color_pink
        DiceColorOption.BROWN -> R.string.dice_color_brown
        DiceColorOption.GRAY -> R.string.dice_color_gray
    }

private val SWATCH_SIZE = 48.dp
private val BORDER_WIDTH = 1.dp
private val SELECTED_BORDER_WIDTH = 3.dp
private val CHECK_PADDING = 4.dp
private const val LUMINANCE_THRESHOLD = 0.5f

@LightDarkPreview
@Composable
private fun DiceColorPickerPreview() {
    DiceRollerTheme {
        Surface {
            DiceColorPicker(
                selected = DiceColorOption.TEAL,
                isDark = isSystemInDarkTheme(),
                onSelected = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
