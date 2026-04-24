package com.greenfodor.diceroller.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greenfodor.diceroller.ui.DiceConstants

/**
 * Defines design-system constants for the dice rendering and animation.
 *
 * @param canvasSize The bounding box size for the dice animation.
 * @param diceInternalSize The base scale factor for the 3D geometry.
 * @param rollDurationMillis Total time for the rolling animation.
 * @param strokeWidth The thickness of the dice edges.
 */
data class DiceSpecs(
    val canvasSize: Dp = 200.dp,
    val diceInternalSize: Float = DiceConstants.DEFAULT_CUBE_SIZE,
    val rollDurationMillis: Int = DiceConstants.ROLL_DURATION_MILLIS,
    val strokeWidth: Float = DiceConstants.STROKE_WIDTH
)

val LocalDiceSpecs = staticCompositionLocalOf { DiceSpecs() }

@Suppress("UnusedReceiverParameter")
val MaterialTheme.diceSpecs: DiceSpecs
    @Composable
    @ReadOnlyComposable
    get() = LocalDiceSpecs.current
