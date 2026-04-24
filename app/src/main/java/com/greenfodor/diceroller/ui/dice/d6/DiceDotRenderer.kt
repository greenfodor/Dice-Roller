package com.greenfodor.diceroller.ui.dice.d6

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.greenfodor.diceroller.geometry.Point3D
import com.greenfodor.diceroller.geometry.interpolatePoint3DOnFace
import com.greenfodor.diceroller.geometry.projectPoint
import com.greenfodor.diceroller.ui.DiceConstants
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws the pips (dots) for a single dice face.
 *
 * Dots are rendered as perspective-correct polygonal circles — they use the face's
 * own 3D coordinate system so they distort naturally with the cube's perspective.
 *
 * @param dotCount Number of pips to draw (1–6).
 * @param vVertices The four corners of the face in 3D (rotated) space.
 * @param centerX Horizontal screen center for projection.
 * @param centerY Vertical screen center for projection.
 * @param normalOffset Small offset vector to lift dots above the face surface.
 * @param dotPath Reusable [Path] object to avoid per-frame allocations.
 */
internal fun DrawScope.drawDiceDotsOnFace(
    dotCount: Int,
    vVertices: List<Point3D>,
    centerX: Float,
    centerY: Float,
    normalOffset: Point3D,
    dotPath: Path
) {
    val dotRadiusFactor = DiceConstants.DOT_RADIUS_FACTOR
    val dotColor = Color.White.copy(alpha = DiceConstants.D6_DOT_ALPHA)
    val segments = DiceConstants.DOT_SEGMENTS
    val dotCenters = DotLayouts.positions[dotCount] ?: return

    dotCenters.forEach { center ->
        dotPath.reset()
        for (i in 0 until segments) {
            val angle = 2.0 * Math.PI * i / segments
            val u = center.x + cos(angle).toFloat() * dotRadiusFactor
            val v = center.y + sin(angle).toFloat() * dotRadiusFactor

            val point3D = interpolatePoint3DOnFace(u, v, vVertices, normalOffset)
            val projected = point3D.projectPoint(centerX, centerY)

            if (i == 0) {
                dotPath.moveTo(projected.x, projected.y)
            } else {
                dotPath.lineTo(projected.x, projected.y)
            }
        }
        dotPath.close()
        drawPath(path = dotPath, color = dotColor)
    }
}
