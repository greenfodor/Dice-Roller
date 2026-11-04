package com.greenfodor.diceroller.ui.settings

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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.data.DiceColorOption
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.Res
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.cd_dice_color_swatch_selected
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.dice_color_blue
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.dice_color_brown
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.dice_color_gray
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.dice_color_green
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.dice_color_orange
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.dice_color_pink
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.dice_color_purple
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.dice_color_red
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.dice_color_teal
import com.greenfodor.diceroller.feature.settings.presentation.generated.resources.dice_color_yellow
import com.greenfodor.diceroller.ui.icons.Check
import com.greenfodor.diceroller.ui.icons.DiceIcons
import com.greenfodor.diceroller.ui.preview.LightDarkPreview
import com.greenfodor.diceroller.ui.theme.DiceRollerTheme
import com.greenfodor.diceroller.ui.theme.spacing
import com.greenfodor.diceroller.ui.theme.toColor
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

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
            val name = stringResource(option.label)
            val description = if (isSelected) {
                stringResource(Res.string.cd_dice_color_swatch_selected, name)
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
                        imageVector = DiceIcons.Check,
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

private val DiceColorOption.label: StringResource
    get() = when (this) {
        DiceColorOption.RED -> Res.string.dice_color_red
        DiceColorOption.ORANGE -> Res.string.dice_color_orange
        DiceColorOption.YELLOW -> Res.string.dice_color_yellow
        DiceColorOption.GREEN -> Res.string.dice_color_green
        DiceColorOption.TEAL -> Res.string.dice_color_teal
        DiceColorOption.BLUE -> Res.string.dice_color_blue
        DiceColorOption.PURPLE -> Res.string.dice_color_purple
        DiceColorOption.PINK -> Res.string.dice_color_pink
        DiceColorOption.BROWN -> Res.string.dice_color_brown
        DiceColorOption.GRAY -> Res.string.dice_color_gray
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
