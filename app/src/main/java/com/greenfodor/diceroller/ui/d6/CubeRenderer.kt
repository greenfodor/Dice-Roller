package com.greenfodor.diceroller.ui.d6

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import com.greenfodor.diceroller.geometry.Point3D
import com.greenfodor.diceroller.geometry.projectPoint
import com.greenfodor.diceroller.geometry.rotatePoint
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.theme.DiceColors

/**
 * Paints used for cube face rendering. Instantiate once and reuse across frames.
 */
class CubePaints {
    val face = Paint()
    val stroke = Paint()
}

/**
 * Performs the full 3D cube draw onto the canvas.
 *
 * Pipeline:
 * 1. Build unit cube vertices
 * 2. Rotate by (rotationX, rotationY)
 * 3. Project to 2D with perspective
 * 4. Sort faces back-to-front (Painter's Algorithm)
 * 5. For each visible face: shade, fill, draw pips, stroke edge
 *
 * @param size         Side length of the cube in pixels.
 * @param centerX      Horizontal center of the draw area.
 * @param centerY      Vertical center of the draw area.
 * @param rotationX    Current X-axis rotation in degrees.
 * @param rotationY    Current Y-axis rotation in degrees.
 * @param facePath     Reusable [Path] for face geometry.
 * @param dotPath      Reusable [Path] for pip geometry.
 * @param paints       Reusable [CubePaints] to avoid per-frame allocations.
 * @param diceColors   Theme colors for each face.
 */
fun DrawScope.drawCube(
    size: Float,
    centerX: Float,
    centerY: Float,
    rotationX: Float,
    rotationY: Float,
    facePath: Path,
    dotPath: Path,
    paints: CubePaints,
    diceColors: DiceColors
) {
    val halfSize = size / 2

    // --- Geometry ---

    val vertices = listOf(
        Point3D(-halfSize, -halfSize, -halfSize), // 0
        Point3D(halfSize, -halfSize, -halfSize), // 1
        Point3D(halfSize, halfSize, -halfSize), // 2
        Point3D(-halfSize, halfSize, -halfSize), // 3
        Point3D(-halfSize, -halfSize, halfSize), // 4
        Point3D(halfSize, -halfSize, halfSize), // 5
        Point3D(halfSize, halfSize, halfSize), // 6
        Point3D(-halfSize, halfSize, halfSize)  // 7
    )

    val rotatedVertices = vertices.map { rotatePoint(it, rotationX, rotationY) }
    val projectedVertices = rotatedVertices.map { projectPoint(it, centerX, centerY) }

    // Standard dice layout: opposite faces sum to 7
    val faces = listOf(
        FaceDescriptor(listOf(4, 5, 6, 7), diceColors.face1, 1), // Front  (Z+)
        FaceDescriptor(listOf(0, 1, 2, 3), diceColors.face6, 6), // Back   (Z-)
        FaceDescriptor(listOf(0, 1, 5, 4), diceColors.face2, 2), // Bottom (Y-)
        FaceDescriptor(listOf(2, 3, 7, 6), diceColors.face5, 5), // Top    (Y+)
        FaceDescriptor(listOf(0, 3, 7, 4), diceColors.face4, 4), // Left   (X-)
        FaceDescriptor(listOf(1, 2, 6, 5), diceColors.face3, 3)  // Right  (X+)
    )

    // Sort back-to-front so closer faces paint over farther ones
    val sortedFaces = faces.sortedBy { face ->
        face.vertexIndices.map { rotatedVertices[it].z }.average()
    }

    // --- Rendering ---

    val cornerRadius = DiceConstants.CORNER_RADIUS
    val pathEffect = PathEffect.cornerPathEffect(cornerRadius)
    val lightSource = Point3D(0.5f, -1f, 1.5f).normalize()

    sortedFaces.forEach { face ->
        val v0 = rotatedVertices[face.vertexIndices[0]]
        val v1 = rotatedVertices[face.vertexIndices[1]]
        val v3 = rotatedVertices[face.vertexIndices[3]]

        // Compute surface normal for diffuse shading
        val normal = (v1 - v0).cross(v3 - v0).normalize()
        val intensity = normal.dot(lightSource).coerceIn(
            DiceConstants.MIN_SHADING_INTENSITY,
            DiceConstants.MAX_SHADING_INTENSITY
        )
        val shadedColor = face.baseColor.shade(intensity)

        // Build the face polygon path
        facePath.reset()
        facePath.moveTo(
            projectedVertices[face.vertexIndices[0]].x,
            projectedVertices[face.vertexIndices[0]].y
        )
        for (i in 1 until face.vertexIndices.size) {
            facePath.lineTo(
                projectedVertices[face.vertexIndices[i]].x,
                projectedVertices[face.vertexIndices[i]].y
            )
        }
        facePath.close()

        // 1. Fill face
        drawIntoCanvas { canvas ->
            paints.face.apply {
                color = shadedColor
                this.pathEffect = pathEffect
                style = PaintingStyle.Fill
            }
            canvas.drawOutline(Outline.Generic(facePath), paints.face)
        }

        // 2. Draw pips clipped to face bounds
        val dotOffset = normal * DiceConstants.DOT_OFFSET_FACTOR
        clipPath(facePath) {
            drawDiceDotsOnFace(
                dotCount = face.dotCount,
                vVertices = face.vertexIndices.map { rotatedVertices[it] },
                centerX = centerX,
                centerY = centerY,
                normalOffset = dotOffset,
                dotPath = dotPath
            )
        }

        // 3. Stroke face edge
        drawIntoCanvas { canvas ->
            paints.stroke.apply {
                color = Color.White.copy(alpha = 0.5f)
                style = PaintingStyle.Stroke
                strokeWidth = DiceConstants.STROKE_WIDTH
                this.pathEffect = pathEffect
            }
            canvas.drawOutline(Outline.Generic(facePath), paints.stroke)
        }
    }
}

/** Multiplies RGB channels by [intensity] while preserving alpha. */
private fun Color.shade(intensity: Float) = Color(
    red   = red   * intensity,
    green = green * intensity,
    blue  = blue  * intensity,
    alpha = alpha
)
