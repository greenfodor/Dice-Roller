package com.greenfodor.diceroller.ui.dice.d6

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextMeasurer
import com.greenfodor.diceroller.data.D6FaceStyle
import com.greenfodor.diceroller.geometry.HexahedronGeometry
import com.greenfodor.diceroller.geometry.PROJECTIVE_COEFFICIENT_COUNT
import com.greenfodor.diceroller.geometry.Point2D
import com.greenfodor.diceroller.geometry.Point3D
import com.greenfodor.diceroller.geometry.calculateNormalZ
import com.greenfodor.diceroller.geometry.projectPoint
import com.greenfodor.diceroller.geometry.rotatePoint
import com.greenfodor.diceroller.ui.DiceConstants
import com.greenfodor.diceroller.ui.DiceConstants.LIGHT_SOURCE
import com.greenfodor.diceroller.ui.dice.FaceLabelLayouts
import com.greenfodor.diceroller.ui.utils.shade

/**
 * Holds reusable paths, matrices, styles, text layouts and buffers used for cube face rendering.
 *
 * Instantiating these once and reusing them across frames prevents unnecessary object allocation
 * and garbage collection during the animation loop.
 */
class D6Paints {
    /** Reusable path for the face polygon (fill, stroke and pip clipping). */
    val facePath = Path()

    /** Reusable path for pip geometry. */
    val dotPath = Path()

    /** Stroke style for face borders. */
    val stroke = Stroke(width = DiceConstants.STROKE_WIDTH)

    /** Reusable matrix for projecting the number label onto a face quad. */
    val numberMatrix = Matrix()

    /** Projective coefficients of the number label transform. */
    val numberCoefficients = FloatArray(PROJECTIVE_COEFFICIENT_COUNT)

    /** Cached text layouts of the number labels (used only when the face style is numbers). */
    val numberLayouts = FaceLabelLayouts()

    /** Temporary buffer for the four face-corner destination coordinates (x/y per corner). */
    val dstArray = FloatArray(8)

    /** Pre-allocated vertex buffers used to avoid per-frame allocations during the rotation loop. */
    val rotatedVertices = ArrayList<Point3D>(HexahedronGeometry.vertices.size).apply {
        repeat(HexahedronGeometry.vertices.size) { add(Point3D(0f, 0f, 0f)) }
    }

    /** Pre-allocated projection buffers used to avoid per-frame allocations during the 2D mapping loop. */
    val projectedVertices = ArrayList<Point2D>(HexahedronGeometry.vertices.size).apply {
        repeat(HexahedronGeometry.vertices.size) { add(Point2D(0f, 0f)) }
    }
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
 * @param rotationZ Current Z-axis rotation in degrees.
 * @param paints Reusable [D6Paints] to avoid per-frame allocations (includes path buffers).
 * @param textMeasurer Measures the number labels.
 * @param color The base color applied uniformly to every face.
 * @param faceStyle Whether faces are marked with pips or numbers.
 */
fun DrawScope.drawD6(
    size: Float,
    centerX: Float,
    centerY: Float,
    rotationX: Float,
    rotationY: Float,
    rotationZ: Float,
    paints: D6Paints,
    textMeasurer: TextMeasurer,
    color: Color,
    faceStyle: D6FaceStyle = D6FaceStyle.PIPS
) {
    val halfSize = size / 2

    HexahedronGeometry.vertices.forEachIndexed { index, baseV ->
        val v = baseV * halfSize
        val rotated = v.rotatePoint(rotationX, rotationY, rotationZ)
        paints.rotatedVertices[index] = rotated
        paints.projectedVertices[index] = rotated.projectPoint(centerX, centerY)
    }

    val rotatedVertices = paints.rotatedVertices
    val projectedVertices = paints.projectedVertices

    val faces = createDiceFaceDescriptors(color)

    val visibleFaces =
        faces
            .mapNotNull { face ->
                val vIndices = face.vertexIndices
                val v0 = rotatedVertices[vIndices[0]]
                val v1 = rotatedVertices[vIndices[1]]
                val v3 = rotatedVertices[vIndices[3]]

                val normalZ = calculateNormalZ(v0, v1, v3)

                if (normalZ > 0) {
                    val normal = (v1 - v0).cross(v3 - v0).normalize()
                    val avgDepth = vIndices.sumOf { rotatedVertices[it].z.toDouble() }
                    Triple(face, normal, avgDepth)
                } else {
                    null
                }
            }.sortedBy { it.third }

    visibleFaces.forEach { (face, normal, _) ->
        renderFace(
            face = face,
            normal = normal,
            rotatedVertices = rotatedVertices,
            projectedVertices = projectedVertices,
            centerX = centerX,
            centerY = centerY,
            paints = paints,
            textMeasurer = textMeasurer,
            faceStyle = faceStyle
        )
    }
}

/**
 * Maps [HexahedronGeometry.faces] to render descriptors. Every face shares [color] so the whole
 * die is one uniform color.
 *
 * @param color The base color applied to every face.
 */
internal fun createDiceFaceDescriptors(color: Color) =
    HexahedronGeometry.faces.map { face ->
        FaceDescriptor(
            vertexIndices = face.vertexIndices,
            baseColor = color,
            dotCount = face.value
        )
    }

private fun DrawScope.renderFace(
    face: FaceDescriptor,
    normal: Point3D,
    rotatedVertices: List<Point3D>,
    projectedVertices: List<Point2D>,
    centerX: Float,
    centerY: Float,
    paints: D6Paints,
    textMeasurer: TextMeasurer,
    faceStyle: D6FaceStyle
) {
    val intensity =
        normal.dot(LIGHT_SOURCE).coerceIn(
            DiceConstants.MIN_SHADING_INTENSITY,
            DiceConstants.MAX_SHADING_INTENSITY
        )
    val shadedColor = face.baseColor.shade(intensity)
    val vIndices = face.vertexIndices
    val verts = vIndices.map { projectedVertices[it] }

    paints.facePath.rewind()
    paints.facePath.moveTo(verts[0].x, verts[0].y)
    for (i in 1 until verts.size) paints.facePath.lineTo(verts[i].x, verts[i].y)
    paints.facePath.close()

    drawPath(path = paints.facePath, color = shadedColor)

    when (faceStyle) {
        D6FaceStyle.PIPS -> {
            val dotOffset = normal * DiceConstants.DOT_OFFSET_FACTOR
            clipPath(paints.facePath) {
                drawDiceDotsOnFace(
                    dotCount = face.dotCount,
                    vVertices = vIndices.map { rotatedVertices[it] },
                    centerX = centerX,
                    centerY = centerY,
                    normalOffset = dotOffset,
                    dotPath = paints.dotPath
                )
            }
        }

        D6FaceStyle.NUMBERS ->
            drawDiceNumberOnFace(
                label = face.dotCount.toString(),
                vIndices = vIndices,
                projectedVertices = projectedVertices,
                paints = paints,
                textMeasurer = textMeasurer
            )
    }

    drawPath(
        path = paints.facePath,
        color = Color.White.copy(alpha = DiceConstants.D6_STROKE_ALPHA),
        style = paints.stroke
    )
}
