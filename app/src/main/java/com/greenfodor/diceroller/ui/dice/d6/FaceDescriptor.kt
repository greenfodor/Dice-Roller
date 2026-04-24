package com.greenfodor.diceroller.ui.dice.d6

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.greenfodor.diceroller.ui.DiceConstants

/**
 * Describes a single face of the dice cube.
 *
 * @param vertexIndices Indices into the cube's vertex array (always 4 vertices).
 * @param baseColor The unshaded base color for this face.
 * @param dotCount Number of pips to render (1–6).
 */
data class FaceDescriptor(
    val vertexIndices: List<Int>,
    val baseColor: Color,
    val dotCount: Int
)

/**
 * Precomputed UV-space pip positions for each die face value.
 * Coordinates are in the range [-1, 1] on both axes.
 */
object DotLayouts {
    private const val S = DiceConstants.DOT_SPACING_FACTOR

    val positions: Map<Int, List<Offset>> = mapOf(
        1 to listOf(
            Offset(0f, 0f)
        ),
        2 to listOf(
            Offset(-S, S),
            Offset(S, -S)
        ),
        3 to listOf(
            Offset(-S, S),
            Offset(0f, 0f),
            Offset(S, -S)
        ),
        4 to listOf(
            Offset(-S, -S),
            Offset(S, -S),
            Offset(-S, S),
            Offset(S, S)
        ),
        5 to listOf(
            Offset(-S, -S),
            Offset(S, -S),
            Offset(0f, 0f),
            Offset(-S, S),
            Offset(S, S)
        ),
        6 to listOf(
            Offset(-S, -S),
            Offset(-S, 0f),
            Offset(-S, S),
            Offset(S, -S),
            Offset(S, 0f),
            Offset(S, S)
        )
    )
}
