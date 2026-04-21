package com.greenfodor.diceroller.ui.d6

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.greenfodor.diceroller.geometry.Point3D
import com.greenfodor.diceroller.geometry.projectPoint
import com.greenfodor.diceroller.ui.DiceConstants
import kotlin.math.cos
import kotlin.math.sin

/**
 * Maps a 2D face coordinate (u, v in [-1, 1]) to 3D world space
 * using bilinear interpolation across the four face vertices.
 *
 * @param normalOffset Small vector that lifts the dot slightly above the face surface,
 *   preventing Z-fighting artifacts.
 */
internal fun getPoint3DOnFace(
    u: Float,
    v: Float,
    vVertices: List<Point3D>,
    normalOffset: Point3D
): Point3D {
    val s = (u + 1f) / 2f
    val t = (v + 1f) / 2f
    val (v0, v1, v2, v3) = vVertices
    return Point3D(
        x = (1 - s) * (1 - t) * v0.x + s * (1 - t) * v1.x + s * t * v2.x + (1 - s) * t * v3.x,
        y = (1 - s) * (1 - t) * v0.y + s * (1 - t) * v1.y + s * t * v2.y + (1 - s) * t * v3.y,
        z = (1 - s) * (1 - t) * v0.z + s * (1 - t) * v1.z + s * t * v2.z + (1 - s) * t * v3.z
    ) + normalOffset
}

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
    val dotColor = Color.White.copy(alpha = 0.9f)
    val segments = DiceConstants.DOT_SEGMENTS
    val dotCenters = DotLayouts.positions[dotCount] ?: return

    dotCenters.forEach { center ->
        dotPath.reset()
        for (i in 0 until segments) {
            val angle = 2.0 * Math.PI * i / segments
            val u = center.x + cos(angle).toFloat() * dotRadiusFactor
            val v = center.y + sin(angle).toFloat() * dotRadiusFactor

            val point3D = getPoint3DOnFace(u, v, vVertices, normalOffset)
            val projected = projectPoint(point3D, centerX, centerY)

            if (i == 0) dotPath.moveTo(projected.x, projected.y)
            else dotPath.lineTo(projected.x, projected.y)
        }
        dotPath.close()
        drawPath(path = dotPath, color = dotColor)
    }
}
