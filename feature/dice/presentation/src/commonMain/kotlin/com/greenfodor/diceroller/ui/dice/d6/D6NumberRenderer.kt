package com.greenfodor.diceroller.ui.dice.d6

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import com.greenfodor.diceroller.geometry.Point2D
import com.greenfodor.diceroller.geometry.quadToQuadTransform
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.dice.drawCenteredLabel
import com.greenfodor.diceroller.ui.dice.setProjective

/**
 * Draws the value of a single dice face as a number instead of pips.
 *
 * The digit is projected flat onto the face with the projective transform from
 * [quadToQuadTransform], mapping a fixed UV source square ([DiceConstants.D6_SRC_QUAD]) onto the
 * four projected face corners. This makes the number distort with the cube's perspective exactly
 * like the pips do. Nothing is drawn when the projected face is degenerate.
 *
 * @param label The face value rendered as text.
 * @param vIndices The face's four vertex indices into [projectedVertices].
 * @param projectedVertices The cube's vertices projected to 2D screen space.
 * @param paints Reusable [D6Paints]; uses its number matrix, coefficients, layouts and [D6Paints.dstArray].
 * @param textMeasurer Measures the label on a layout cache miss.
 */
internal fun DrawScope.drawDiceNumberOnFace(
    label: String,
    vIndices: List<Int>,
    projectedVertices: List<Point2D>,
    paints: D6Paints,
    textMeasurer: TextMeasurer
) {
    vIndices.forEachIndexed { corner, vertexIndex ->
        paints.dstArray[corner * 2] = projectedVertices[vertexIndex].x
        paints.dstArray[corner * 2 + 1] = projectedVertices[vertexIndex].y
    }

    if (!quadToQuadTransform(DiceConstants.D6_SRC_QUAD, paints.dstArray, paints.numberCoefficients)) return
    paints.numberMatrix.setProjective(paints.numberCoefficients)

    val labels = paints.numberLayouts
    val layout = labels.layout(
        textMeasurer = textMeasurer,
        label = label,
        fontSize = DiceConstants.D6_NUMBER_TEXT_SIZE_UV,
        color = Color.White.copy(alpha = DiceConstants.D6_DOT_ALPHA)
    )

    withTransform({ transform(paints.numberMatrix) }) {
        drawCenteredLabel(layout, -(labels.descent + labels.ascent) / 2)
    }
}
