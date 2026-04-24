package com.greenfodor.diceroller.ui.dice.d6

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
import com.greenfodor.diceroller.geometry.Point2D
import com.greenfodor.diceroller.geometry.Point3D
import com.greenfodor.diceroller.geometry.calculateNormalZ
import com.greenfodor.diceroller.geometry.projectPoint
import com.greenfodor.diceroller.geometry.rotatePoint
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.DiceConstants.LIGHT_SOURCE
import com.greenfodor.diceroller.ui.theme.DiceColors
import com.greenfodor.diceroller.ui.utils.shade

/**
 * Holds reusable [Paint] and [PathEffect] objects used for cube face rendering.
 *
 * Instantiating these once and reusing them across frames prevents unnecessary object allocation
 * and garbage collection during the animation loop.
 */
class CubePaints {
    /** Paint used for the main face surface (fill). */
    val face = Paint()
    
    /** Paint used for the face borders (stroke). */
    val stroke = Paint()

    /** Pre-allocated vertex buffers used to avoid per-frame allocations during the rotation loop. */
    val rotatedVertices = ArrayList<Point3D>(8).apply { repeat(8) { add(Point3D(0f, 0f, 0f)) } }
    
    /** Pre-allocated projection buffers used to avoid per-frame allocations during the 2D mapping loop. */
    val projectedVertices = ArrayList<Point2D>(8).apply { repeat(8) { add(Point2D(0f, 0f)) } }
}

/**
 * Performs the full 3D cube draw onto the canvas.
 *
 * This function handles the entire 3D-to-2D pipeline:
 * 1. **Geometry Calculation**: Scales, rotates, and projects the 8 vertices of the cube in a 
 *    single pass to minimize list allocations.
 * 2. **Back-face Culling**: Calculates the surface normal for each face and skips rendering 
 *    those pointing away from the camera (at most 3 faces are visible for a convex cube).
 * 3. **Painter's Algorithm Sorting**: Sorts the visible faces by their average Z-depth (back-to-front)
 *    to ensure correct overlapping.
 * 4. **Incremental Rendering**: Iterates through sorted faces to apply shading, pips, and strokes.
 *
 * @param size Side length of the cube in pixels.
 * @param centerX Horizontal center of the draw area.
 * @param centerY Vertical center of the draw area.
 * @param rotationX Current X-axis rotation in degrees.
 * @param rotationY Current Y-axis rotation in degrees.
 * @param facePath Reusable [Path] for face polygon geometry.
 * @param dotPath Reusable [Path] for pip geometry.
 * @param paints Reusable [CubePaints] to avoid per-frame allocations.
 * @param diceColors Theme colors assigned to each face.
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

    // --- 1. Geometry Calculation ---
    // Update pre-allocated lists to minimize garbage collection
    UNIT_CUBE_BASE_VERTICES.forEachIndexed { index, baseV ->
        val v = Point3D(baseV.x * halfSize, baseV.y * halfSize, baseV.z * halfSize)
        val rotated = v.rotatePoint(rotationX, rotationY)
        paints.rotatedVertices[index] = rotated
        paints.projectedVertices[index] = rotated.projectPoint(centerX, centerY)
    }

    val rotatedVertices = paints.rotatedVertices
    val projectedVertices = paints.projectedVertices

    // --- 2. Culling and Sorting ---
    val faces = createDiceFaceDescriptors(diceColors)

    // Back-face Culling: Skip faces pointing away from the camera.
    // For a convex cube, at most 3 faces are visible at once.
    val visibleFaces = faces.mapNotNull { face ->
        val vIndices = face.vertexIndices
        val v0 = rotatedVertices[vIndices[0]]
        val v1 = rotatedVertices[vIndices[1]]
        val v3 = rotatedVertices[vIndices[3]]

        // Surface normal Z-component determines visibility (+Z is towards camera)
        val normalZ = calculateNormalZ(v0, v1, v3)
        
        if (normalZ > 0) {
            val normal = (v1 - v0).cross(v3 - v0).normalize()
            val avgDepth = vIndices.sumOf { rotatedVertices[it].z.toDouble() }
            Triple(face, normal, avgDepth)
        } else null
    }.sortedBy { it.third } // Sort back-to-front (Painter's Algorithm)

    // --- 3. Rendering ---
    visibleFaces.forEach { (face, normal, _) ->
        renderFace(
            face = face,
            normal = normal,
            rotatedVertices = rotatedVertices,
            projectedVertices = projectedVertices,
            centerX = centerX,
            centerY = centerY,
            facePath = facePath,
            dotPath = dotPath,
            paints = paints
        )
    }
}

/** 
 * Unit cube vertices centered at the origin.
 * Indices follow a specific winding order used to define face surfaces.
 */
private val UNIT_CUBE_BASE_VERTICES = listOf(
    Point3D(-1f, -1f, -1f), Point3D(1f, -1f, -1f),
    Point3D(1f, 1f, -1f), Point3D(-1f, 1f, -1f),
    Point3D(-1f, -1f, 1f), Point3D(1f, -1f, 1f),
    Point3D(1f, 1f, 1f), Point3D(-1f, 1f, 1f)
)

/**
 * Defines the six faces of a D6, mapping vertex indices to colors and pip counts.
 * Follows the standard dice layout where opposite faces sum to 7.
 *
 * @param diceColors The theme colors to apply to each face value.
 */
private fun createDiceFaceDescriptors(diceColors: DiceColors) = listOf(
    FaceDescriptor(listOf(4, 5, 6, 7), diceColors.face1, 1), // Front  (Z+)
    FaceDescriptor(listOf(1, 0, 3, 2), diceColors.face6, 6), // Back   (Z-)
    FaceDescriptor(listOf(0, 1, 5, 4), diceColors.face2, 2), // Bottom (Y-)
    FaceDescriptor(listOf(7, 6, 2, 3), diceColors.face5, 5), // Top    (Y+)
    FaceDescriptor(listOf(0, 4, 7, 3), diceColors.face4, 4), // Left   (X-)
    FaceDescriptor(listOf(5, 1, 2, 6), diceColors.face3, 3)  // Right  (X+)
)

/**
 * Renders a single face of the cube onto the canvas.
 * 
 * The rendering process includes:
 * 1. **Shading**: Calculates light intensity based on the angle between the surface normal 
 *    and the global light source.
 * 2. **Geometry Construction**: Resets and builds a [Path] for the face polygon using projected 2D points.
 * 3. **Surface Fill**: Draws the shaded face surface with rounded corners using the provided path effect.
 * 4. **Pip Rendering**: Draws the dice dots, clipped to the face boundary and slightly offset 
 *    to prevent Z-fighting artifacts.
 * 5. **Edge Stroke**: Draws a subtle semi-transparent border around the face to enhance definition.
 */
private fun DrawScope.renderFace(
    face: FaceDescriptor,
    normal: Point3D,
    rotatedVertices: List<Point3D>,
    projectedVertices: List<Point2D>,
    centerX: Float,
    centerY: Float,
    facePath: Path,
    dotPath: Path,
    paints: CubePaints
) {
    // 1. Shading
    val intensity = normal.dot(LIGHT_SOURCE).coerceIn(
        DiceConstants.MIN_SHADING_INTENSITY,
        DiceConstants.MAX_SHADING_INTENSITY
    )
    val shadedColor = face.baseColor.shade(intensity)

    // 2. Build Face Geometry
    facePath.reset()
    val vIndices = face.vertexIndices
    facePath.moveTo(projectedVertices[vIndices[0]].x, projectedVertices[vIndices[0]].y)
    for (i in 1 until vIndices.size) {
        facePath.lineTo(projectedVertices[vIndices[i]].x, projectedVertices[vIndices[i]].y)
    }
    facePath.close()

    // 3. Render Layers
    drawIntoCanvas { canvas ->
        paints.face.apply {
            color = shadedColor
            style = PaintingStyle.Fill
        }
        canvas.drawOutline(Outline.Generic(facePath), paints.face)
    }

    // Pips
    val dotOffset = normal * DiceConstants.DOT_OFFSET_FACTOR
    clipPath(facePath) {
        drawDiceDotsOnFace(
            dotCount = face.dotCount,
            vVertices = vIndices.map { rotatedVertices[it] },
            centerX = centerX,
            centerY = centerY,
            normalOffset = dotOffset,
            dotPath = dotPath
        )
    }

    // Edge Stroke
    drawIntoCanvas { canvas ->
        paints.stroke.apply {
            color = Color.White.copy(alpha = DiceConstants.D6_STROKE_ALPHA)
            style = PaintingStyle.Stroke
            strokeWidth = DiceConstants.STROKE_WIDTH
        }
        canvas.drawOutline(Outline.Generic(facePath), paints.stroke)
    }
}
