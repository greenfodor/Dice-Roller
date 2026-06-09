package com.greenfodor.diceroller.ui.dice.d6

import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.withSave
import com.greenfodor.diceroller.geometry.Point2D
import com.greenfodor.diceroller.ui.DiceConstants

/**
 * Draws the value of a single dice face as a number instead of pips.
 *
 * The digit is projected flat onto the face using [android.graphics.Matrix.setPolyToPoly], mapping
 * a fixed UV source square ([DiceConstants.D6_SRC_QUAD]) onto the four projected face corners. This
 * makes the number distort with the cube's perspective exactly like the pips do.
 *
 * @param canvas Target canvas (the face fill has already been drawn).
 * @param label The face value rendered as text.
 * @param vIndices The face's four vertex indices into [projectedVertices].
 * @param projectedVertices The cube's vertices projected to 2D screen space.
 * @param paints Reusable [D6Paints]; uses its [D6Paints.textPaint] and [D6Paints.dstArray].
 */
internal fun drawDiceNumberOnFace(
    canvas: Canvas,
    label: String,
    vIndices: List<Int>,
    projectedVertices: List<Point2D>,
    paints: D6Paints
) {
    canvas.nativeCanvas.withSave {
        vIndices.forEachIndexed { corner, vertexIndex ->
            paints.dstArray[corner * 2] = projectedVertices[vertexIndex].x
            paints.dstArray[corner * 2 + 1] = projectedVertices[vertexIndex].y
        }

        paints.numberMatrix.setPolyToPoly(DiceConstants.D6_SRC_QUAD, 0, paints.dstArray, 0, 4)
        concat(paints.numberMatrix)

        paints.textPaint.apply {
            color = Color.White.copy(alpha = DiceConstants.D6_DOT_ALPHA).toArgb()
            textSize = DiceConstants.D6_NUMBER_TEXT_SIZE_UV
        }

        drawText(
            label,
            0f,
            -(paints.textPaint.descent() + paints.textPaint.ascent()) / 2,
            paints.textPaint
        )
    }
}
