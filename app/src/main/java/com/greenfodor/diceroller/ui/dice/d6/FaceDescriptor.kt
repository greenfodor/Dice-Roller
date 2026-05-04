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

    private val topLeft = Offset(-S, -S)
    private val topRight = Offset(S, -S)
    private val middleLeft = Offset(-S, 0f)
    private val middleRight = Offset(S, 0f)
    private val bottomLeft = Offset(-S, S)
    private val bottomRight = Offset(S, S)
    private val center = Offset(0f, 0f)

    val positions: Map<Int, List<Offset>> = mapOf(
        1 to listOf(center),
        2 to listOf(bottomLeft, topRight),
        3 to listOf(bottomLeft, center, topRight),
        4 to listOf(topLeft, topRight, bottomLeft, bottomRight),
        5 to listOf(topLeft, topRight, center, bottomLeft, bottomRight),
        6 to listOf(topLeft, middleLeft, bottomLeft, topRight, middleRight, bottomRight)
    )
}
